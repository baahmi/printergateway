/**
 * 
 */
package com.mvdb.tools.hp;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.io.FilenameUtils;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;

/**
 * @author mvdb
 */
public class GoogleDriverHandler implements PGHandler {
    private static final String SERVICE_ACCOUNT_EMAIL = "794223890927-8piskiq7oljs2tp8rvhsl7k595p7u093@developer.gserviceaccount.com";
    private static final String CLIENT_ID = "794223890927-8piskiq7oljs2tp8rvhsl7k595p7u093.apps.googleusercontent.com";
    private static final String PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC372jMLhsQ7/nf\nLmbtQzeyPZo/UXt0PHxCYGl0p4Weh/uwDSprrthDiOxxppis1Yrd1HnD5uNOik8l\nxan7oB74P3m5hlArdAIfq8GZXCig8Q/RJ5oIqaD6tiF4jd8I4QwvcDy2DaRBDtEh\n6sVitt3qDs9WIAdn0Q6gkdwo+mSIjxuE9Yz65wqZrmwm8k8ha5V2uFEvQMUZp1H4\nRtLnGdUUcXGLGLLCzw5WTdvHgVj2/qjmXPU45UmgtQDQwVngXudU/7jZ7GInANFe\nqa9MNzCR1UDVtg1t6fGX2eY7qa2tLdEwe5yywzAN863JPK0QH3kQLcUPVSUtHynw\nXw6AC5mRAgMBAAECggEAMo4WEtC1VcFvv10Eoz/vZgkyhSbDNN6PFLtlINAOlNUx\nY2ErAK/s9xsCSfaNTivfYDtPQH4MigDP4kTjBKss9/SSY2C8VkUrdEMEEj/XVu0K\n+OTWnHzrJx26M2VjIRCiBcPmm+9B7zPAXX8mw4wZkISDjlt9TZaTW6Tbi1PPIA0l\n520eyLtSA4zYawKgLeidS9+xUcwZGItotOjjy6Xsnry68D3an3Pxx/SY0glQkS+l\n/xOgGVwzLZ7+sqUj05ACeZ/pHXJ9i3tRIj4sIugYyD22Uhv0uhfPhhjHJufXjUCE\nMRUqhZQeG+yIRB90miHc2HDzelnNLKQGO0m4DSaoQQKBgQDoJ0SbEy0aRx0KCQXF\nsw2TBIvqPMTMH3sn+L9vQTCLMbdDpC4R9G5L/s6dEgPg203U6qaErCuFPtu10OBo\na7sFdebqEJIWhLQ/Xz07E3r48iby7ZmOh6frUAov96eciOsYmUY3z1bW2ATyNJen\nXL2SQaG+6ZLFmB5wys2eFEjROQKBgQDK1DCt1P5Uj7IOmGNGagVzsbN4R2FtGBE6\nlDhYtiGQbKY69pS/SVTudQYtLTWwsWzDr6CQoFV62iLoCFZMgGyY5fSvRgaPBwkR\np+NJ+4kEGcEreUENGoWX9/bcxTWFnxuaezEUS8XGSW+7YvasgyeXGhJ8CAnAqFtP\n43gQ49eDGQKBgH4nIIvYbtDxxeD3KDDXbM/wT4u4/AQkM0t0S6rMVPG+0WSrafGN\nzwXy8BnAeWUOJwxFerlrybZ9gu0ZFWkI6bc8639UA97CvCQXv/FDU7bF985YuPIP\n23ruoqqQPH++k2ppmBOp0vmqp31qgOKMbeN1UMyqpnxDfn8yZWu6gBtZAoGAMNMj\nZLUBBBoTIrBMNYcQCOmd6jZEnZ5Nh2xmo/Qz4DBHU6pH5EQizcQd2IIpin8rT2/X\nlvkBmrEn9fML0x6XBbdVT1vr+EIZTMa9T/Hc6b/JYE2v+xeHUS2/nyVZ3PCQFH/T\nMK+MzE6hNtp7xstLnHQKPxEwR9A/Ktv26xntpwECgYEA6AAjWFlY41UAl8zeVuBt\nbl4t1cnUNL/8jlKgVYkbjSuDnaFWet8xW+4V2dalb8CAH0vKccZ+Byg7HW2g9fir\n61mvwu0hBHuNwyWw7Y675rWjKOXec2dVVn0VKin9kB0WWXirqRzjflDcXbrhoSJr\n3l1Cd5mgfrfxlpa7X5uSuXI\u003d\n-----END PRIVATE KEY-----\n";

    @Override
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
                .setServiceAccountPrivateKeyFromP12File(new java.io.File("conf/printergateway-62243e827a4a.p12"))
                .setServiceAccountUser("info@mvdb.net")
                .build();
            Drive drive = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("hp").build();
            FileList list = drive.files().list().setMaxResults(10).execute();
            for(com.google.api.services.drive.model.File file : list.getItems()) {
                System.out.println(file.getId());
                System.out.println("Owners : " + file.getOwnerNames());
                System.out.printf("%s (%s)\n", file.getTitle(), file.getId());
            }
            String parentId = drive.files().list().setQ("title= 'Scans'").execute().getItems().get(0).getId();
            System.out.println("parentId : " + parentId);
            File fileMetadata = new File();
            fileMetadata.setParents(Arrays.asList(new ParentReference().setId(parentId)));
            String filename = System.currentTimeMillis() + ".pdf";
            fileMetadata.setTitle(filename);
            InputStreamContent content = new InputStreamContent("application/pdf", scan);
            // discard the result
            drive.files().insert(fileMetadata, content).execute();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
