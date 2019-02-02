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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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
        String mappings = processCommandLine(args);
        ScanMailHandlerFactory factory = new ScanMailHandlerFactory(mappings);
        SMTPServer server = new SMTPServer(factory);
        server.setPort(25000);
        server.start();
    }

    private static String processCommandLine(String[] args) {
        Options options = new Options().
                addOption("mapping", true, "Property file containing mail mappings to directories").
                addOption("key", true, "The googledrive keyfile").
                addOption("help", false, "Help");
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("help")) {
                showHelp(options);
                System.exit(0);
            }
            return line.getOptionValue("mapping");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void showHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("PrinterGateway", options);
    }

}
