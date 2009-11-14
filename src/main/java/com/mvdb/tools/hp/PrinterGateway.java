/**
 * 
 */
package com.mvdb.tools.hp;

import org.subethamail.smtp.server.SMTPServer;

/**
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 *
 */
public class PrinterGateway {

    /**
     * @param args
     */
    public static void main(String[] args) {
        ScanMailHandlerFactory factory = new ScanMailHandlerFactory();
        SMTPServer server = new SMTPServer(factory);
        server.setPort(25000);
        server.start();
    }

}
