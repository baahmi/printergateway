/*
   Copyright 2019 Martin van den Bemt

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distribu ted on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package org.mvdb.tools.printergateway.handlers;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.mvdb.tools.printergateway.AbstractPrinterGatewayTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 */
public class GoogleDriveTest extends AbstractPrinterGatewayTest {

    @Test
    public void testGetFile() {
        GoogleDriverHandler handler = new GoogleDriverHandler() {
            @Override
            String getFolderId(String folderName, Drive drive) throws IOException {
                return "parentfolderid";
            }
        };
        File file = handler.getFile(null, "/data/Scans");
        assertEquals("parentfolderid", file.getParents().get(0));
        // check if we have a filename that ends at pdf.
        assertEquals(true, file.getName().endsWith("pdf"));
    }

    @Test
    public void testGetFolderId() throws Exception {
        GoogleDriverHandler handler = new GoogleDriverHandler() {
            @Override
            HttpTransport createTransport() throws GeneralSecurityException, IOException {
                return setupMockTransport();
            }
        };
        handler.setPrefix("gdrive");
        handler.setConfiguration(createConfiguration());

        Drive drive = handler.createDrive();

        assertEquals("idscans1", handler.getFolderId("/data/Scans", drive));
        assertEquals("idscans2", handler.getFolderId("/data/dir1/dir2/dir3/Scans", drive));
        assertEquals(null, handler.getFolderId("/data/dontexist", drive));
        assertEquals(null, handler.getFolderId("/dontexist", drive));
    }

    private HttpTransport setupMockTransport() {
        Map<String, String> requestMap = createRequestMap();
        MockHttpTransport transport = new MockHttpTransport() {
            @Override
            public LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
                LowLevelHttpRequest request = null;
                if (requestMap.containsKey(url)) {
                    request = new MockLowLevelHttpRequest() {
                        @Override
                        public LowLevelHttpResponse execute() throws IOException {
                            MockLowLevelHttpResponse response = new MockLowLevelHttpResponse();
                            response.setStatusCode(200);
                            response.setContentType(Json.MEDIA_TYPE);
                            response.setContent(requestMap.get(url));
                            return response;
                        }
                    };
                } else {
                    request = super.buildRequest(method, url);
                }
                return request;
            }
        };
        return transport;
    }

    private Map<String, String> createRequestMap() {
        Map<String, String> result = new HashMap<>();
        // authentication
        result.put("https://oauth2.googleapis.com/token", "{'access_token': 'token', 'expires_in': 3600, 'token_type': 'Bearer'}");
        // get list of directories that are named Scans
        // for the path "/data/Scans"
        result.put("https://www.googleapis.com/drive/v3/files?q=name%3D'Scans'", "{ 'kind': 'drive#fileList', 'files': [\n" +
                "  {'kind': 'drive#file','id': 'idscans1','name': 'Scans','mimeType': 'application/vnd.google-apps.folder'},\n" +
                "  {'kind': 'drive#file','id': 'idscans2','name': 'Scans','mimeType': 'application/vnd.google-apps.folder'}\n" +
                "]}");
        result.put("https://www.googleapis.com/drive/v3/files/idscans1?fields=parents,name", "{'name': 'Scans','parents': ['iddata']}");
        // data is the root, doesn't have parents.
        result.put("https://www.googleapis.com/drive/v3/files/iddata?fields=parents,name", "{'name': 'data'}");

        result.put("https://www.googleapis.com/drive/v3/files/idscans2?fields=parents,name", "{'name': 'Scans','parents': ['iddir3']}");
        result.put("https://www.googleapis.com/drive/v3/files/iddir3?fields=parents,name", "{'name': 'dir3','parents': ['iddir2']}");
        result.put("https://www.googleapis.com/drive/v3/files/iddir2?fields=parents,name", "{'name': 'dir2','parents': ['iddir1']}");
        result.put("https://www.googleapis.com/drive/v3/files/iddir1?fields=parents,name", "{'name': 'dir1','parents': ['iddata']}");
        // no files that match this name.
        result.put("https://www.googleapis.com/drive/v3/files?q=name%3D'dontexist'", "{'kind': 'drive#fileList','files': []}");
        result.replaceAll((k,v) -> v.replaceAll("'", "\""));
        return result;
    }


    public static void main(String[] args) {
        String accountUser = "service account mail";
        String accountId = "service account mail";
        String accountPKFile = "location/to/p12/file";
        String testFileToUpload = "";
        String pathToUseForUpload = "";

        GoogleDriverHandler gdh = new GoogleDriverHandler() {
            @Override
            public String getLocalConfigItem(String key) {
                if(key.equals(GoogleDriverHandler.ACCOUNT_USER_KEY)) {
                    return accountUser;
                } else if(key.equals(GoogleDriverHandler.ACCOUNT_ID_KEY)) {
                    return accountId;
                } else if (key.equals(GoogleDriverHandler.ACCOUNT_PRIVATE_KEY_KEY)) {
                    return accountPKFile;
                }
                return super.getLocalConfigItem(key);
            }
        };
        // directory listing
        Drive drive = gdh.createDrive();
        try {
            System.out.println(drive.files().list());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // upload of file. Tends to succeed even if nothings happens
        try {
            gdh.handle(pathToUseForUpload, new FileInputStream(new java.io.File(testFileToUpload)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
