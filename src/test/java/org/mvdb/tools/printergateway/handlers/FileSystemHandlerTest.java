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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.Assert.assertEquals;

public class FileSystemHandlerTest {

    @Test
    public void testHandle() {
        String target = "target/filesystemhandlertest/";
        FileSystemHandler fsh = new FileSystemHandler() {
            @Override
            protected String createFileName() {
                return "filename.pdf";
            }
        };
        InputStream is = IOUtils.toInputStream("string", Charset.defaultCharset());
        File file = new File(target);
        fsh.handle(file.getAbsolutePath(), is);
        File resultFile = new File(target, "filename.pdf");
        assertEquals(true, resultFile.exists());
        FileUtils.deleteQuietly(resultFile.getParentFile());
    }
}
