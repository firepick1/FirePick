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

import java.io.PrintStream;
import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BOMFactory implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(BOMFactory.class);
    private final ConcurrentLinkedQueue<BOM> bomQueue = new ConcurrentLinkedQueue<BOM>();
    private OutputType outputType = OutputType.DEFAULT;
    private Thread worker;
    private PartFactory partFactory;
    private boolean workerPaused;

    public void shutdown() {
        logger.info("Shutting down Ehcache");
        CacheManager.getInstance().shutdown();
    }

    public BOM createBOM(URL url) {
        BOM bom = new BOM(url);
        synchronized (bomQueue) {
            bomQueue.add(bom);
            if (worker == null) {
                worker = new Thread(this);
                worker.start();
            }
        }
        return bom;
    }

    @Override
    public void run() {
        for (; ; ) {
            synchronized (bomQueue) {
                if (bomQueue.size() == 0) {
                    worker = null;
                    return;
                }
            }

            if (isWorkerPaused()) {
                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException e) {
                    logger.error("interrupted", e);
                }
            } else {
                BOM bom;
                synchronized (bomQueue) {
                    bom = bomQueue.poll();
                }
                try {
                    if (bom != null) {
                        if (!bom.resolve()) {
                            synchronized (bomQueue) {
                                logger.info("Requeing bom for resolve() {}", bom.getUrl());
                                bomQueue.add(bom);
                            }
                        }
                    }
                }
                catch (Exception e) {
                    logger.error("Could not resolve BOM", e);
                }
            }
        }
    }

    public BOMFactory printBOM(PrintStream printStream, BOM bom) {
        switch (outputType) {
            case MARKDOWN:
                new BOMMarkdownPrinter().print(bom, printStream);
                break;
            case HTML:
                new BOMHtmlPrinter().setPrintHtmlWrapper(true).setTitle(bom.getTitle()).print(bom, printStream);
                break;
            case HTML_TABLE:
                new BOMHtmlPrinter().setPrintHtmlWrapper(false).setTitle(bom.getTitle()).print(bom, printStream);
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

    public PartFactory getPartFactory() {
        if (partFactory == null) {
            setPartFactory(PartFactory.getInstance());
        }
        return partFactory;
    }

    public BOMFactory setPartFactory(PartFactory partFactory) {
        this.partFactory = partFactory;
        return this;
    }

    public boolean isWorkerPaused() {
        return workerPaused;
    }

    public BOMFactory setWorkerPaused(boolean workerPaused) {
        this.workerPaused = workerPaused;
        return this;
    }

    public enum OutputType {
        DEFAULT,
        MARKDOWN,
        HTML,
        HTML_TABLE,
        CSV
    }

}
