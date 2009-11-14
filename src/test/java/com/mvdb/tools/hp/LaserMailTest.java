/**
 * 
 */
package com.mvdb.tools.hp;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.junit.Test;

import com.mvdb.tools.hp.ScanMailMessageHandler;

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
        ScanMailMessageHandler handler = new ScanMailMessageHandler(null);
        handler.setMapping("testmail@home.mvdb.net", "target/tmpfiles/");
        InputStream is = FileUtils.openInputStream(new File("src/test/resources/laserprintermail.example"));
        handler.data(is);
        String[] pdfFiles = tmpDir.list(new SuffixFileFilter(".pdf"));
        assertEquals(true, FileUtils.contentEquals(new File(originalFile), new File(tmpDir, pdfFiles[0])));
        is.close();
        FileUtils.cleanDirectory(tmpDir);
    }
}
