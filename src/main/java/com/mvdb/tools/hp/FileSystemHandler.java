/**
 * 
 */
package com.mvdb.tools.hp;

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
