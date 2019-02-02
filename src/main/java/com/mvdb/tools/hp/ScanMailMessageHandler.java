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
package com.mvdb.tools.hp;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.configuration.Configuration;
import org.apache.james.mime4j.dom.BinaryBody;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.message.DefaultMessageBuilder;
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
        Message message = new DefaultMessageBuilder().parseMessage(data);
        String toAddress = message.getTo().get(0).toString();
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
