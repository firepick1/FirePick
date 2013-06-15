package org.firepick.firebom;
/*
    Copyright (C) 2013 Karl Lew <karl@firepick.org>. All rights reserved.
    DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
    
    This file is part of FirePick Software.
    
    FirePick Software is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FirePick Software is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FirePick Software.  If not, see <http://www.gnu.org/licenses/>.
    
    For more information about FirePick Software visit http://firepick.org
 */

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws IOException {
        mainStream(args, System.out);
    }

    public static void mainStream(String[] args, PrintStream printStream) throws IOException {
        BOMFactory bomFactory = new BOMFactory(printStream);

        if (!parseArgs(args, bomFactory)) {
            printHelp(printStream);
        }
        bomFactory.shutdown();
    }


    private static boolean parseArgs(String[] args, BOMFactory bomFactory) throws IOException {
        int urlCount = 0;

        for (String arg: args) {
            if ("-markdown".equalsIgnoreCase(arg)) {
                bomFactory.setOutputType(BOMFactory.OutputType.MARKDOWN);
            } else if ("-csv".equalsIgnoreCase(arg)) {
                bomFactory.setOutputType(BOMFactory.OutputType.CSV);
            } else if ("-html".equalsIgnoreCase(arg)) {
                bomFactory.setOutputType(BOMFactory.OutputType.HTML_TABLE);
            } else {
                try {
                    URL url = new URL(arg);
                    urlCount++;
                    bomFactory.printBOM(url);
                } catch (MalformedURLException e) {
                    return false;
                }
            }
        }
        return urlCount > 0;
    }

    public static void printHelp(PrintStream printStream) throws IOException {
        InputStream is = Main.class.getResourceAsStream("/help.txt");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        while (br.ready()) {
            String line = br.readLine();
            printStream.println(line);
        }
    }

}
