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

import net.sf.ehcache.CacheManager;
import org.firepick.relation.RelationPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;

public class BOMFactory {
    private static Logger logger = LoggerFactory.getLogger(BOMFactory.class);

    private OutputType outputType = OutputType.DEFAULT;
    private PrintStream printStream;

    public BOMFactory(PrintStream printStream) {
        this.printStream = printStream;
    }

    public void shutdown() {
        logger.info("Shutting down Ehcache");
        CacheManager.getInstance().shutdown();
    }

    public BOMFactory printBOM(URL partUrl)  {
        BOM bom = new BOM();
        Part part = PartFactory.getInstance().createPart(partUrl);
        bom.addPart(part, 1);

        switch (outputType) {
            case MARKDOWN:
                new BOMMarkdownPrinter().print(bom, printStream);
                break;
            default:
            case CSV:
                new RelationPrinter().print(bom, printStream);
                break;
        }

        return this;
    }

    public OutputType getOutputType() {
        return outputType;
    }

    public BOMFactory setOutputType(OutputType outputType) {
        this.outputType = outputType;
        return this;
    }

    public enum OutputType {
        DEFAULT,
        MARKDOWN,
        CSV
    }

}
