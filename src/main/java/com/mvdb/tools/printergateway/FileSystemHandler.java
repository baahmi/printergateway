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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

/**
 * @author mvdb
 */
public class FileSystemHandler implements PGHandler {

    public void handle(String target, InputStream scan) {
        new File(target).mkdirs();
        String newFile = FilenameUtils.concat(target, System.currentTimeMillis() + ".pdf");
        System.out.println("Storing file : " + newFile);
        try {
            FileOutputStream fo = new FileOutputStream(new File(newFile));
            IOUtils.copy(scan, fo);
            fo.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
