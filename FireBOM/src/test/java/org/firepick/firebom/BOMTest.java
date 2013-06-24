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
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.*;

public class BOMTest {
    private PartFactory partFactory;

    @Before
    public void setup() {
        partFactory = PartFactory.getInstance();
  //      CacheManager.getInstance().clearAll();
    }

    @Test
    public void testBadUrl() {
        PartFactory factory = PartFactory.getInstance();
        Part part = null;
        URL url = null;
        try {
            url = new URL("http://shpws.me/badbadurl");
            part = factory.createPart(url);
        }
        catch (IOException e) {
            fail();
        }
        assertEquals(null, part.getId());
        assertEquals(null, part.getTitle());
        assertEquals(part, part.getPart());
        assertEquals(0, part.getPackageCost(), 0);
        assertEquals(1, part.getPackageUnits(), 0);
        assertEquals("www.shapeways.com", part.getVendor());
        assertFalse(part.isFresh());

        BOM bom = new BOM(url);
        bom.resolve();
        assertFalse(bom.isValid());
        new RelationPrinter().print(bom, System.out);
    }

    @Test
    public void testD7IH() throws Exception {
        URL url = new URL("https://github.com/firepick1/FirePick/wiki/D7IH");
        BOM bom = new BOM(url);
        assertEquals(0, bom.getRowCount());
        bom.resolve();
        assertEquals(6, bom.getRowCount());
        new RelationPrinter().print(bom, System.out);
        assertEquals("Total cost: ", 13.5696, bom.totalCost(), 0.005d);
        assertEquals("Part count:", 6, bom.partCount());
    }

    @Test
    public void testD7IHMarkdown() throws Exception {
        URL url = new URL("https://github.com/firepick1/FirePick/wiki/D7IH");
        BOM bom = new BOM(url);
        assertEquals(0, bom.getRowCount());
        bom.resolve();
        assertEquals(6, bom.getRowCount());
        assertEquals(6, bom.getRowCount());
        new BOMMarkdownPrinter().print(bom, System.out);
    }

    @Test
    public void testMaximumParts() throws IOException {
        URL url = new URL("http://www.shapeways.com/badpart");
        BOM bom = new BOM(url).setMaximumParts(5);
        assertEquals(5, bom.getMaximumParts());
        Exception caughtException = null;
        try {
            for (int iPart = 0; iPart < 10; iPart++) {
                Part part = partFactory.createPart(new URL("http://www.shapeways.com/badpart" + iPart));
                bom.addPart(part, 1);
            }
        }
        catch (Exception e) {
            caughtException = e;
        }
        assert (caughtException instanceof ApplicationLimitsException);
        assertEquals(5, bom.getRowCount());
    }

    @Test
    public void testRecursiveBOM() throws IOException {
        URL url1 = Main.class.getResource("/evilPart1.html");
        URL url2 = Main.class.getResource("/evilPart2.html");
        System.out.println(url1);
        System.out.println(url2);
        BOM bom = new BOM(url1);
        Exception caughtException = null;
        try {
            bom.resolve();
        }
        catch (Exception e) {
            caughtException = e;
        }
        assert (caughtException instanceof ApplicationLimitsException);
        assertEquals(1, bom.getRowCount());
        assert (caughtException.getMessage().contains("Recursive"));
        new RelationPrinter().print(bom, System.out);
    }

    @Test
    public void testBOMFactory() throws MalformedURLException, InterruptedException {
        BOMFactory bomFactory = new BOMFactory();
        URL url = new URL("https://github.com/firepick1/FirePick/wiki/D7IH");
        bomFactory.setWorkerPaused(true);
        BOM bom = bomFactory.create(url);
        assertEquals(url, bom.getUrl());
        int iterations = 0;
        assertEquals(0, bom.getRowCount());
        bomFactory.printBOM(System.out, bom); // we can print an empty BOM
        assertEquals(BOM.UNRESOLVED, bom.getTitle());
        bomFactory.setWorkerPaused(false);
        do {
            iterations++;
            System.out.print(".");
            Thread.sleep(1000);
        } while (!bom.isResolved());
        System.out.println();
        assert (iterations > 0);
        assertEquals(6, bom.getRowCount());
        assertEquals("Adjustable idler, 6-7mm belt, horizontal extrusions", bom.getTitle());
        bomFactory.printBOM(System.out, bom);

        // everything should be cached with no additional URL requests
        long requests = partFactory.getNetworkRequests();
        bomFactory.setWorkerPaused(true);
        bom = bomFactory.create(url);
        assert (bomFactory.isWorkerPaused());
        assert (!bom.isResolved());
        Thread.sleep(1000);
        bomFactory.setWorkerPaused(false);
        Thread.sleep(1000);
        assert (bom.isResolved());
        assertEquals(6, bom.getRowCount());
        assert (bom.isResolved());
        assertEquals("Adjustable idler, 6-7mm belt, horizontal extrusions", bom.getTitle());
        bomFactory.printBOM(System.out, bom);
        assertEquals(requests, partFactory.getNetworkRequests());
    }
}
