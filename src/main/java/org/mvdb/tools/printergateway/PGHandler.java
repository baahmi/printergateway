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

import java.io.InputStream;

/**
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 *
 */
public interface PGHandler {

    void handle(String target, InputStream scan);

    void setConfiguration(Configuration configuration);

    Configuration getConfiguration();

    /**
     * The prefix is needed to read the correct configuration section,
     * since the naming is based on the configuration, we cannot
     * hardcode it.
     */
    String getPrefix();

    void setPrefix(String prefix);

    String getLocalConfigItem(String key);

    /**
     * Specifies if the handler is slow, so it will be handled in a seperate thread and gives control back
     * to the printer faster.
     * @return
     */
    boolean isSlow();

}
