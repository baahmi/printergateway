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
package org.mvdb.tools.printergateway;

import org.apache.commons.configuration2.Configuration;
import org.apache.james.mime4j.dom.BinaryBody;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.message.DefaultMessageBuilder;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.RejectException;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 * 
 */
public class ScanMailMessageDispatcher implements MessageHandler {

    private static Logger logger = Logger.getLogger("org.mvdb.tools.printergateway");
    private static final String CONFIG_PREFIX = "mapping";
    private Configuration configuration;

    public ScanMailMessageDispatcher(Configuration configuration) {
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
            String configEntry = String.format("%s/%s", CONFIG_PREFIX, toAddress);
            String addressTarget = configuration.getString(configEntry);
            if(addressTarget == null) {
                logger.warning(String.format("Cannot find mapping for %s. Cannot continue.", toAddress));
                return;
            }
            String[] handlerTarget = addressTarget.split(":");
            String handlerName = handlerTarget.length == 1 ? configuration.getString("general/default") : handlerTarget[0];
            String target = handlerTarget[handlerTarget.length -1];
            PGHandler handler = getHandler(handlerName);
            handler.handle(target, bb.getInputStream());
        }
    }

    /**
     * Instantiates a new instance of a handler.
     * @param handlerName
     * @return
     */
    PGHandler getHandler(String handlerName) {

        PGHandler handler = null;
        String handlerClass = configuration.getString(handlerName + "/handler");
        Class clazz = null;

        try {
            try {
                clazz = Thread.currentThread().getContextClassLoader().loadClass(handlerClass);
            } catch (ClassNotFoundException e) {
                clazz = ScanMailMessageDispatcher.class.getClassLoader().loadClass(handlerClass);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            handler = (PGHandler) clazz.newInstance();
            handler.setConfiguration(configuration);
            handler.setPrefix(handlerName);
        } catch (Exception e) {
            logger.warning("Cannot create handler for " + handlerName);
        }
        return handler;
    }

    public void done() {
    }

}
