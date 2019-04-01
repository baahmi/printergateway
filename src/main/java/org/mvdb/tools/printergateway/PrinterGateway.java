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

import javafx.print.Printer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.BasePathLocationStrategy;
import org.apache.commons.configuration2.tree.DefaultExpressionEngine;
import org.apache.commons.configuration2.tree.DefaultExpressionEngineSymbols;
import org.subethamail.smtp.server.SMTPServer;

import java.io.File;
import java.io.StringWriter;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 */
public class PrinterGateway {

    private static int DEFAULT_PORT = 25000;

    private static Logger logger = Logger.getLogger("org.mvdb.tools.printergateway");

    private static String[] args;
    /**
     * @param args
     */
    public static void main(String[] args) {
        PrinterGateway.args = args;
        new PrinterGateway().start();
    }

    public static Configuration createConfiguration(String configPath) {
        File file = new File(configPath);
        Configuration configuration = null;
        try {
            Parameters params = new Parameters();
            // we have dots in our keys/properties, so the delim is /
            // inspired from https://commons.apache.org/proper/commons-configuration/userguide/howto_combinedbuilder.html
            DefaultExpressionEngineSymbols symbols = new DefaultExpressionEngineSymbols.Builder(
                    DefaultExpressionEngineSymbols.DEFAULT_SYMBOLS)
                    .setPropertyDelimiter("/").create();
            DefaultExpressionEngine engine = new DefaultExpressionEngine(symbols);
            FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<>(INIConfiguration.class);
            builder.configure(params.ini().setExpressionEngine(engine).setLocationStrategy(new BasePathLocationStrategy()).
                    setBasePath(file.getParentFile().getAbsolutePath()).setFileName(file.getName()));
            configuration = builder.getConfiguration();
            try {
                StringWriter configRead = new StringWriter();
                ((INIConfiguration) configuration).write(configRead);
                logger.finest("Config that was read :");
                logger.finest(configRead.toString());
            } catch(Exception e) {
            }
        } catch(ConfigurationException ce) {
            throw new RuntimeException("Configuration file "+configPath+" not found", ce);
        }
        return configuration;
    }

    public PrinterGateway() {
    }

    public void start() {
        String configPath = processCommandLine(PrinterGateway.args);
        Configuration configuration = PrinterGateway.createConfiguration(configPath);
        ScanMailHandlerFactory factory = new ScanMailHandlerFactory(configuration);
        SMTPServer server = new SMTPServer(factory);
        server.setPort(configuration.getInt("general/port", DEFAULT_PORT));
        server.start();
    }

    private String processCommandLine(String[] args) {
        Options options = new Options().addOption("h", "help", false, "Help").addOption(
            Option.builder().longOpt("config").argName("config").hasArg().desc("Gateway configuration file").build()
        );

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("help")) {
                showHelp(options);
                System.exit(0);
            }
            return line.getOptionValue("config", "/conf/printergateway.conf").trim();
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
