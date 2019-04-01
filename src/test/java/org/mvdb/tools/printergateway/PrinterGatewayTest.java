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
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 */
public class PrinterGatewayTest extends AbstractPrinterGatewayTest {


    @Test(expected = RuntimeException.class)
    public void testConfigFileDoesNotExist() {
        PrinterGateway pg = new PrinterGateway();
        pg.main(new String[] {});
    }

    @Test
    public void testConfig() {
        PrinterGateway pg = new PrinterGateway();
        pg.main(new String[]{ "-config src/test/resources/printergateway-test.conf" });
    }

    @Test
    public void testConfgKeyWithDots() throws Exception {
        Configuration conf = createConfiguration();
        // this is an issue : keys have dots (domains in our case)
        //, which doesn't match a key with a dot. Keys with a dot shoudl be double dot.
        // we test if the configuration is overriden with a /
        assertEquals("mock:/we/do/care", conf.getString("mapping/mock-scans@home.com"));
    }


}
