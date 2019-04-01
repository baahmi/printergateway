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

import org.junit.Test;
import org.mvdb.tools.printergateway.handlers.MockHandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 */
public class ScanMailMessageDispatcherTest extends AbstractPrinterGatewayTest {

    @Test
    public void testData() throws Exception {

        final PGHandler[] handler = new PGHandler[] {null};
        final String[] name = new String[] {null};
        ScanMailMessageDispatcher smm = new ScanMailMessageDispatcher(createConfiguration()) {
            PGHandler getHandler(String handlerName) {
                name[0] = handlerName;
                handler[0] = super.getHandler(handlerName);
                return handler[0];
            }
        };
        smm.data(createMessageStream("mock-scans@home.com"));
        // correct handlerName ?
        assertEquals("mock", name[0]);
        // correct handler ?
        assertEquals(MockHandler.class, handler[0].getClass());
        assertEquals("/we/do/care", ((MockHandler)handler[0]).getTarget());
        // behaviour when address is not found in mapping
        smm.data(createMessageStream("idontexist@home"));
    }

    private InputStream createMessageStream(String address) throws Exception {
        String mail = getMail(address);
        return new ByteArrayInputStream(mail.getBytes("UTF-8"));
    }

    private String getMail(String address) {
        String result = "To:"+address+"\n" +
                "Content-Type: multipart/mixed; boundary=\"boundry\"\n" +
                "\n" +
                "--boundry\n" +
                "Content-Type: image/gif; name=\"Transparent.gif\"\n" +
                "Content-Disposition: attachment; filename=\"Transparent.gif\"\n" +
                "Content-Transfer-Encoding: base64\n" +
                "\n" +
                "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7\n" +
                "--boundry--\n";
        return result;

    }

}
