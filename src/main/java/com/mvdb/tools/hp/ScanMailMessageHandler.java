/**
 * 
 */
package com.mvdb.tools.hp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.james.mime4j.message.BinaryBody;
import org.apache.james.mime4j.message.Message;
import org.apache.james.mime4j.message.Multipart;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.RejectException;

/**
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 * 
 */
public class ScanMailMessageHandler implements MessageHandler {

    private static final String MAILMAPPINGS = "scanmailmappings.properties";
    private static final String SMPTRELAY = "smtprelay";
    
    MessageContext ctx;
    private String smtpRelay;
    Map<String, String> mapping; 

    @SuppressWarnings("unchecked")
    public ScanMailMessageHandler(MessageContext ctx) {
        this.ctx = ctx;
        Properties props = new Properties();
        try {
            props.load(getClass().getClassLoader().getResourceAsStream(MAILMAPPINGS));
        } catch (IOException e) {
            e.printStackTrace();
        }
        smtpRelay = (String) props.remove(SMPTRELAY);
        mapping = new HashMap(props);
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
            String folder = mapping.get(toAddress);
            if (folder != null) {
                createFolder(folder);
                String newFile = FilenameUtils.concat(folder, System.currentTimeMillis()+".pdf");
                System.out.println("Storing file : " + newFile);
                bb.writeTo(new FileOutputStream(new File(newFile)));
            } else {
                System.out.println("Mail relay is not yet supported! (current mailrelay setting : "+smtpRelay+")");
            }
        }
    }

    public void done() {
    }
    
    public void setMapping(String mailAddress, String directory) {
        mapping.put(mailAddress, directory);
    }
    
    private void createFolder(String folder) {
        new File(folder).mkdirs();
    }

}
