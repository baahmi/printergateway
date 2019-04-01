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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.mvdb.tools.printergateway.AbstractPGHandler;

/**
 * Save the inputstream to a file with the timestamp as the filename
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 */
public class FileSystemHandler extends AbstractPGHandler {

    private static Logger logger = Logger.getLogger("org.mvdb.tools.printergateway");

    public FileSystemHandler() {
    }

    public void handle(String target, InputStream scan) {
        File targetFile = new File(target, createFileName());
        targetFile.getParentFile().mkdirs();
        logger.info("Storing scan to  : " + targetFile);
        try (FileOutputStream fo = new FileOutputStream(targetFile)) {
            IOUtils.copy(scan, fo);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    protected String createFileName() {
        return Instant.now().toEpochMilli() + ".pdf";
    }
}
