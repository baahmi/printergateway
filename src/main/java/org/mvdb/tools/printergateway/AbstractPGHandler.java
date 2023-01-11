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

/**
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 */
public abstract class AbstractPGHandler implements PGHandler {

    protected Configuration configuration;
    protected String prefix;

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Configuration getConfiguration() {
        return this.configuration;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getLocalConfigItem(String key) {
        return configuration.getString(String.format("%s/%s",getPrefix(), key));
    }

    @Override
    public boolean isSlow() {
        return false;
    }
}
