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

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

public class PartTester {
    private URL url;
    private Part part;

    public PartTester(PartFactory partFactory, String url) throws IOException {
        this.url = new URL(url);
        part = partFactory.createPart(this.url);
        part.validate();
        assert(part.isValid());
    }

    public PartTester testId(String id) {
        assertEquals(id, part.getId());
        return this;
    }

    public PartTester testUnitCost(double value) {
        assertEquals(value, part.getUnitCost(), 0);
        return this;
    }

    public PartTester testPackageCost(double value) {
        assertEquals(value, part.getPackageCost(), 0);
        return this;
    }

    public PartTester testPackageUnits(double value) {
        assertEquals(value, part.getPackageUnits(), 0);
        return this;
    }

    public PartTester testProject(String value) {
        assertEquals(value, part.getProject());
        return this;
    }

    public PartTester testRequiredParts(int value) {
        List<PartUsage> partUsages = part.getRequiredParts();
        assertNotNull(partUsages);
        assertEquals(value, partUsages.size());
        return this;
    }

    public PartTester testRequiredPart(int index, String partId, double quantity, double unitCost) {
        List<PartUsage> partUsages = part.getRequiredParts();
        PartUsage partUsage = partUsages.get(index);
        assertEquals(partId, partUsage.getPart().getId());
        assertEquals(partId + " quantity", quantity, partUsage.getQuantity(), 0);
        assertEquals(partId + " unit cost", unitCost, partUsage.getPart().getUnitCost(), 0);
        return this;
    }

    public Part getPart() {
        return part;
    }

}
