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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.junit.Test;

/**
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 *
 */
public class LaserMailTest {
    
    @Test
    public void messageHandler() throws Exception {
        File tmpDir = new File("target/tmpfiles/");
        String originalFile = "src/test/resources/laserprintermail.pdf";
        tmpDir.mkdirs();
        ScanMailMessageHandler handler = new ScanMailMessageHandler(new PropertiesConfiguration("src/test/resources/testmappings.config"));
        InputStream is = FileUtils.openInputStream(new File("src/test/resources/laserprintermail.example"));
        handler.data(is);
        String[] pdfFiles = tmpDir.list(new SuffixFileFilter(".pdf"));
        assertEquals(true, FileUtils.contentEquals(new File(originalFile), new File(tmpDir, pdfFiles[0])));
        is.close();
        FileUtils.cleanDirectory(tmpDir);
    }
}
