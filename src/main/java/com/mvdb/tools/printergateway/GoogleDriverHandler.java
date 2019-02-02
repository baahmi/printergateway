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
package com.mvdb.tools.printergateway;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * @author mvdb
 */
public class GoogleDriverHandler implements PGHandler {

    private static final String SERVICE_ACCOUNT_EMAIL = "794223890927-8piskiq7oljs2tp8rvhsl7k595p7u093@developer.gserviceaccount.com";

    @Override
    /**
     * This code doesn't traverse google drive paths. I just create a root folder give the rights to that
     * folder for this specific purpose and symlink it in the folder I need it.
     * Use shift z to make a symlink in the google drive gui.
     * If you would like path traversal, send a pull request.
     */
    public void handle(String target, InputStream scan) {
        HttpTransport httpTransport;
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            // Build service account credential.
            GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
                .setServiceAccountScopes(Collections.singleton(DriveScopes.DRIVE))
                .setServiceAccountPrivateKeyFromP12File(new java.io.File("src/main/resources/printergateway-62243e827a4a.p12"))
                .setServiceAccountUser("info@mvdb.net")
                .build();
            Drive drive = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("hp").build();
            FileList list = drive.files().list().setPageSize(10).execute();
            for(com.google.api.services.drive.model.File file : drive.files().list().setQ("name='Scans'").execute().getFiles()) {
                System.out.println(file.getId());
//                System.out.println("Owners : " + file.getOwners().get(0).getDisplayName());
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
                System.out.println(file.getParents());
            }
//            String parentId = drive.files().list().setQ
            String filename = System.currentTimeMillis() + ".pdf";
            String folderId = drive.files().list().setQ("name='Scans'").execute().getFiles().get(0).getId();
            File fileMetadata = new File();
            fileMetadata.setName(filename);
            fileMetadata.setParents(Collections.singletonList(folderId));
            InputStreamContent content = new InputStreamContent("application/pdf", scan);
            // discard the result
            drive.files().create(fileMetadata, content).execute();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
