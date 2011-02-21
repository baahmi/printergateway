/**
 * 
 */
package com.mvdb.tools.hp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.james.mime4j.message.BinaryBody;
import org.apache.james.mime4j.message.Message;
import org.apache.james.mime4j.message.Multipart;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.RejectException;

/**
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 * 
 */
public class ScanMailMessageHandler implements MessageHandler {

    private String smtpRelay;
    private Configuration configuration;

    public ScanMailMessageHandler(Configuration configuration) {
        this.configuration = configuration;
    }

    public void from(String from) throws RejectException {
    }

    public void recipient(String recipient) throws RejectException {
    }

    public void data(InputStream data) throws IOException {
        Message message = new Message(data);
        String toAddress = message.getTo().get(0).getEncodedString();
        if (message.isMultipart()) {
            Multipart mPart = (Multipart) message.getBody();
            BinaryBody bb = (BinaryBody) mPart.getBodyParts().get(0).getBody();
            String folder = configuration.getString(toAddress, null);
            System.out.println("configuration : " + ConfigurationUtils.toString(configuration));
            if (folder != null) {
                new File(folder).mkdirs();
                String newFile = FilenameUtils.concat(folder, System.currentTimeMillis() + ".pdf");
                System.out.println("Storing file : " + newFile);
                bb.writeTo(new FileOutputStream(new File(newFile)));
            } else {
                System.out.println("Mail relay is not yet supported! (current mailrelay setting : " + smtpRelay + ")");
            }
        }
    }

    public void done() {
    }

}
