/*
   Copyright 2019 Martin van den Bemt

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package org.mvdb.tools.printergateway.handlers;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.mvdb.tools.printergateway.AbstractPGHandler;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 */
public class GoogleDriverHandler extends AbstractPGHandler {

    private static Logger logger = Logger.getLogger("org.mvdb.tools.printergateway");

    private static final String APPNAME = "Printergateway";
    static final String ACCOUNT_USER_KEY = "account-user";
    static final String ACCOUNT_ID_KEY = "account-id";
    static final String ACCOUNT_PRIVATE_KEY_KEY =  "account-privatekey-p12-file";

    /**
     *
     */
    @Override
    public void handle(String target, InputStream scan) {
        Drive drive = createDrive();
        File fileMetadata = getFile(drive, target);
        InputStreamContent content = new InputStreamContent("application/pdf", scan);
        try {
            drive.files().create(fileMetadata, content).execute();
            logger.info(String.format("%s/%s saved to Google Drive", target, fileMetadata.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    File getFile(Drive drive, String target) {
        String filename = System.currentTimeMillis() + ".pdf";
        File result = null;
        try {
            String folderId = getFolderId(target, drive);
            result = new File();
            result.setName(filename);
            result.setParents(Collections.singletonList(folderId));
        } catch(IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    Drive createDrive() {
        String accountUser = getLocalConfigItem(ACCOUNT_USER_KEY);
        String accountId = getLocalConfigItem(ACCOUNT_ID_KEY);
        String accountPKFile = getLocalConfigItem(ACCOUNT_PRIVATE_KEY_KEY);
        Drive result = null;
        try {
            HttpTransport httpTransport = createTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setServiceAccountId(accountId)
                .setServiceAccountScopes(Collections.singleton(DriveScopes.DRIVE))
                .setServiceAccountPrivateKeyFromP12File(new java.io.File(accountPKFile))
                .setServiceAccountUser(accountUser)
                .build();
            // Build service account credential.
            result = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName(APPNAME).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * @param folderName the foldername to search
     * @param drive the drive instance.
     * @return the folderId or null if not found.
     * @throws IOException
     */
    String getFolderId(String folderName, Drive drive) throws IOException {
        String result = null;
        folderName = StringUtils.stripStart(folderName, "/");
        String[] folders = folderName.split("/");
        ArrayUtils.reverse(folders);
        int foldersIndex = 1;
        String matchFolderName = foldersIndex >= folders.length ? null : folders[foldersIndex];
        List<File> files = drive.files().list().setQ(String.format("name='%s'",folders[0])).execute().getFiles();
        // loop through all matches
        for(File f : files) {
            if(result != null) {
                break;
            }
            File file = drive.files().get(f.getId()).setFields("parents,name").execute();
            // if no parent is needed and no parent is found, we are done
            // getParent doesn't return an empty list.
            if (matchFolderName == null && file.getParents() == null) {
                result = f.getId();
                break;
            }
            if(traverseParents(drive, file, folders, foldersIndex)) {
                result = f.getId();
            }
        }
        return result;
    }

    private boolean traverseParents(Drive drive, File file, String[] folders, int folderIndex) throws IOException {
        boolean result = false;
        if (file.getParents() == null && folderIndex >= folders.length) {
            // we have reached the end and we have no parents left, we
            // are done.
            return true;
        }
        // traverse the complete path to see if we found a match.
        for(String parent : file.getParents()) {
            File parentFile = drive.files().get(parent).setFields("parents,name").execute();
            if(parentFile.getName().equals(folders[folderIndex])) {
                // we have a match.
                File matchedFile = drive.files().get(parent).setFields("parents,name").execute();
                folderIndex++;
                result = traverseParents(drive, matchedFile, folders, folderIndex);
                if (result) break;
            }
        }
        return result;

    }

    /**
     * Package private so we can override the transport for testing.
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    HttpTransport createTransport() throws GeneralSecurityException, IOException {
        return GoogleNetHttpTransport.newTrustedTransport();
    }
}
