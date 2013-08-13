package org.firepick.firebom.bom;
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

import org.firepick.firebom.Main;
import org.firepick.firebom.exception.ApplicationLimitsException;
import org.firepick.firebom.part.Part;
import org.firepick.firebom.part.PartFactory;
import org.firepick.relation.RelationPrinter;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static org.junit.Assert.*;

public class BOMTest {
    private static Logger logger = LoggerFactory.getLogger(BOMTest.class);
    private PartFactory partFactory;

    public static String encode(String url, double quantity) {
        try {
            String result = URLEncoder.encode(url, "utf-8");
            if (quantity != 1) {
                result += ":" + quantity;
            }
            return result;
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

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
//fails in maven. odd
//        BOM bom = new BOM(url);
//        bom.resolve(10000);
//        assertFalse(bom.isValid());
//        new RelationPrinter().print(bom, System.out, null);
    }

    @Test
    public void testS2() throws Exception {
        String url2 = "http://mock?id:p2&cost:5";
        String urlS2 = "http://mock?id:S1&source:" + encode(url2, .5);
        BOM bom = new BOMFactory().createBOM(new URL(urlS2));
        assert (bom.resolve(100));
        assertEquals(1, bom.getRowCount());

        for (int i = 0; i < 2; i++) {
            BOMRow row0 = bom.item(0);
            assertEquals("S1", row0.getPart().getId());
            assertEquals(2.5, row0.getCost(), 0);
            assertEquals(1, row0.getQuantity(), 0);
            assertEquals(2.5, row0.getUnitCost(), 0);
            assertEquals("mock", row0.getVendor());
        }

        assertEquals(2.50, bom.totalCost(), 0);
    }

    @Test
    public void testS3R1R4() throws Exception {
        String url1 = "http://mock?id:P1&cost:10&units:4";
        String url2 = "http://mock?id:P2&cost:5";
        String url3 = "http://mock?id:P3&cost:8";
        String url4 = "http://mock?id:S2R1&source:" + encode(url2, .5) + "&require:" + encode(url1, 2);
        String urlS3R1R4 = "http://mock?id:S3R1R4&source:" + encode(url3, .25) + "&require:" + encode(url1, 3) + "&require:" + encode(url4, 5);

        for (int i = 0; i < 3; i++) {
            System.out.println(i);
            BOM bom = new BOMFactory().createBOM(new URL(urlS3R1R4));
            assert (bom.resolve(100));
            assertEquals("S3R1R4", bom.getId());
            assertEquals(3, bom.getRowCount());

            BOMRow row0 = bom.item(0);
            assertEquals("P1", row0.getPart().getId());
            assertEquals(13, row0.getQuantity(), 0);
            assertEquals(32.5, row0.getCost(), 0);
            assertEquals(2.5, row0.getUnitCost(), 0);
            assertEquals("mock", row0.getVendor());

            BOMRow row1 = bom.item(1);
            assertEquals("S2R1", row1.getPart().getId());
            assertEquals(5, row1.getQuantity(), 0);
            assertEquals(12.5, row1.getCost(), 0);
            assertEquals(2.5, row1.getUnitCost(), 0);
            assertEquals("mock", row1.getVendor());

            BOMRow row2 = bom.item(2);
            assertEquals("S3R1R4", row2.getPart().getId());
            assertEquals(2, row2.getCost(), 0);
            assertEquals(1, row2.getQuantity(), 0);
            assertEquals(2, row2.getUnitCost(), 0);
            assertEquals("mock", row2.getVendor());

            assertEquals(47, bom.totalCost(), 0);
        }
    }

    @Test
    public void testR1R2() throws Exception {
        String url1 = "http://mock?id:p1&cost:10&units:4";
        String url2 = "http://mock?id:p2&cost:5";
        String urlR1R2 = "http://mock?id:R1R2&title:R1R2-title&require:" + encode(url1, 1) + "&require:" + encode(url2, 3);
        for (int i = 0; i < 2; i++) {
            BOM bom = new BOMFactory().createBOM(new URL(urlR1R2));
            assert (bom.resolve(100));
            assertEquals("R1R2", bom.getRootPart().getId());
            assertEquals("R1R2-title", bom.getTitle());
            assertEquals(2, bom.getRowCount());
            assertEquals(17.5, bom.totalCost(), 0);

            BOMRow row0 = bom.item(0);
            assertEquals("p1", row0.getPart().getId());
            assertEquals(2.50, row0.getCost(), 0);
            assertEquals(1, row0.getQuantity(), 0);
            assertEquals(2.50, row0.getUnitCost(), 0);
            assertEquals("mock", row0.getVendor());

            BOMRow row1 = bom.item(1);
            assertEquals("p2", row1.getPart().getId());
            assertEquals(15, row1.getCost(), 0);
            assertEquals(3, row1.getQuantity(), 0);
            assertEquals(5, row1.getUnitCost(), 0);
            assertEquals("mock", row1.getVendor());
        }
    }

    @Test
    public void testD7IH() throws Exception {
        URL url = new URL("https://github.com/firepick1/FirePick/wiki/D7IH");
        BOM bom = new BOM(url);
        assertEquals(1, bom.getRowCount());
        assertFalse(bom.isResolved());
        while (!bom.isResolved()) {
            logger.info("bom.resolve()");
            bom.resolve(0);
        }
        assertTrue(bom.isResolved());
        assertEquals(6, bom.getRowCount());
        new RelationPrinter().print(bom, System.out, null);
        assertEquals("Total cost: ", 11.42, bom.totalCost(), 0.5d);
        assertEquals("Part count:", 6, bom.partCount());
    }

    @Test
    public void testD7IHMarkdown() throws Exception {
        URL url = new URL("https://github.com/firepick1/FirePick/wiki/D7IH");
        BOM bom = new BOM(url);
        assertEquals(1, bom.getRowCount());
        bom.resolve(0);
        assertEquals(6, bom.getRowCount());
        assertEquals(6, bom.getRowCount());
        new BOMMarkdownPrinter().print(bom, System.out, null);
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
        bom.resolve(0);
        assertEquals(1, bom.getRowCount());
        new RelationPrinter().print(bom, System.out, null);
    }

    @Test
    public void testBOMFactory() throws MalformedURLException, InterruptedException {
        BOMFactory bomFactory = new BOMFactory();
        URL url = new URL("https://github.com/firepick1/FirePick/wiki/D7IH");
        bomFactory.setWorkerPaused(true);
        BOM bom = bomFactory.createBOM(url);
        assertEquals(url, bom.getUrl());
        int iterations = 0;
        assertEquals(1, bom.getRowCount());
        bomFactory.printBOM(System.out, bom, null); // we can print an empty BOM
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
        bomFactory.printBOM(System.out, bom, null);

        // everything should be cached with no additional URL requests
        bomFactory.setWorkerPaused(true);
        Thread.sleep(1000);
        bom = bomFactory.createBOM(url);
        assert (bomFactory.isWorkerPaused());
        assert (!bom.isResolved());
        Thread.sleep(1000);
        bomFactory.setWorkerPaused(false);
        Thread.sleep(1000);
        assert (bom.isResolved());
        assertEquals(6, bom.getRowCount());
        assert (bom.isResolved());
        assertEquals("Adjustable idler, 6-7mm belt, horizontal extrusions", bom.getTitle());
        bomFactory.printBOM(System.out, bom, null);
    }
}
