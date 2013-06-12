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

import org.firepick.relation.RelationPrinter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class Main {
    static OutputType outputType = OutputType.DEFAULT;
    static URL url;

    public static void main(String[] args) throws IOException {
        if (parseArgs(args)) {
            PartFactory partFactory = new PartFactory();
            BOM bom = new BOM();
            Part part = partFactory.createPart(url);
            bom.addPart(part, 1);

            switch (outputType) {
                case MARKDOWN:
                    new BOMMarkdownPrinter().print(bom, System.out);
                    break;
                default:
                case CSV:
                    new RelationPrinter().print(bom, System.out);
                    break;
            }
        }
    }

    private static boolean parseArgs(String[] args) throws IOException {
        if (args.length <= 0) {
            printHelp();
            return false;
        }
        for (String arg : args) {
            System.out.println(arg);
            if ("-markdown".equalsIgnoreCase(arg)) {
                outputType = OutputType.MARKDOWN;
            }
            if ("-csv".equalsIgnoreCase(arg)) {
                outputType = OutputType.CSV;
            }
            if (arg.startsWith("http") || arg.startsWith("file")) {
                url = new URL(arg);
            }
        }
        return url != null;
    }

    public static void printHelp() throws IOException {
        InputStream is = Main.class.getResourceAsStream("/help.txt");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        while (br.ready()) {
            String line = br.readLine();
            System.out.println(line);
        }
    }

    enum OutputType {
        DEFAULT,
        MARKDOWN,
        CSV
    }

}
