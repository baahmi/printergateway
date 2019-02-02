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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;

/**
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 *
 */
public class ScanMailHandlerFactory implements MessageHandlerFactory {

    private static final String MAILMAPPINGS = "scanmailmappings.properties";

    private PropertiesConfiguration configuration;

    public ScanMailHandlerFactory(String configMapping) {
        if (configMapping == null) {
            // use the default if there is no setting
            configMapping = MAILMAPPINGS;
        }

        try {
            configuration = new PropertiesConfiguration(configMapping);
            configuration.setReloadingStrategy(new FileChangedReloadingStrategy());
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }
    /**
     * @see org.subethamail.smtp.MessageHandlerFactory#create(org.subethamail.smtp.MessageContext)
     */
    public MessageHandler create(MessageContext ctx) {
        return new ScanMailMessageHandler(configuration);
    }

}
