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
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.*;

public class BOMTest {
    private PartFactory partFactory;

    @Before
    public void setup() {
        partFactory = new PartFactory();
    }

    @Test
    public void testBadUrl() {
        IOExceptionPartFactory factory = new IOExceptionPartFactory();
        Part part = null;
        try {
            part = factory.createPart(new URL("http://shpws.me/nekC"));
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
        assertFalse(part.isValid());

        BOM bom = new BOM();
        bom.addPart(part, 1);
        assertFalse(bom.isValid());
        new RelationPrinter().print(bom, System.out);

        factory.setAvailable(true);
        part.validate();
        assertFalse(part.isValid());

        try {
            Thread.sleep(factory.getValidationMillis());
        }
        catch (InterruptedException e) {
            fail();
        }
        part.validate();
        assertTrue(part.isValid());

        bom = new BOM();
        bom.addPart(part, 1);
        assertTrue(bom.isValid());
        new RelationPrinter().print(bom, System.out);
    }

    @Test
    public void testD7IH() throws Exception {
        BOM bom = new BOM();
        assertEquals(0, bom.getRowCount());
        Part part = partFactory.createPart(new URL("https://github.com/firepick1/FirePick/wiki/D7IH"));
        bom.addPart(part, 2);
        assertEquals(6, bom.getRowCount());
        new RelationPrinter().print(bom, System.out);
        assertEquals("Total cost: ", 27.1392, bom.totalCost(), 0);
        assertEquals("Part count:", 12, bom.partCount());
    }

    @Test
    public void testD7IHMarkdown() throws Exception {
        BOM bom = new BOM();
        assertEquals(0, bom.getRowCount());
        Part part = partFactory.createPart(new URL("https://github.com/firepick1/FirePick/wiki/D7IH"));
        bom.addPart(part, 1);
        assertEquals(6, bom.getRowCount());
        new BOMMarkdownPrinter().print(bom, System.out);
    }

    @Test
    public void testMaximumParts() throws IOException {
        BOM bom = new BOM().setMaximumParts(5);
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
        BOM bom = new BOM();
        Part part1 = partFactory.createPart(url1);
        Exception caughtException = null;
        try {
            bom.addPart(part1, 1);
        }
        catch (Exception e) {
            caughtException = e;
        }
        assert (caughtException instanceof ApplicationLimitsException);
        assertEquals(2, bom.getRowCount());
        assert(caughtException.getMessage().contains("Recursive BOM"));
        new RelationPrinter().print(bom, System.out);
    }
}
