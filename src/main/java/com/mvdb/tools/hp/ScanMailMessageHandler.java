/**
 * 
 */
package com.mvdb.tools.hp;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.configuration.Configuration;
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
            String addressTarget = configuration.getString(toAddress, null);
            String[] tmp = addressTarget.split(":");
            String target = tmp.length == 1 ? "file" : tmp[0];
            String folder = tmp.length == 1 ? tmp[0] : tmp[1];
            if (target != null) {
                PGHandler handler = null;
                if(target.equals("gdrive")) {
                    handler = new GoogleDriverHandler();
                } else if (target.equals("file")){
                    handler = new FileSystemHandler();
                }
                handler.handle(folder, bb.getInputStream());
            } else {
                System.out.println("Mail relay is not yet supported! (current mailrelay setting : " + smtpRelay + ")");
            }
        }
    }

    public void done() {
    }

}
