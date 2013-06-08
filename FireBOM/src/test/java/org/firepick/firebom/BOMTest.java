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

import org.firepick.relation.IRow;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.assertEquals;

public class BOMTest {
private PartFactory partFactory;

    @Before
    public void setup() {
        partFactory = new PartFactory();
    }

    @Test
    public void testD7IH() throws Exception{
        BOM bom = new BOM();
        assertEquals(0, bom.getRowCount());
        Part part = partFactory.createPart(new URL("https://github.com/firepick1/FirePick/wiki/D7IH"));
        bom.addPart(part, 2);
        assertEquals(6, bom.getRowCount());
        System.out.println(bom);
        for (IRow row: bom) {
            assertEquals(bom, row.getRelation());
            System.out.println(row);
        }
        System.out.println("Total cost: " + bom.totalCost());
        System.out.println("Part count:" + bom.partCount());
    }
}
