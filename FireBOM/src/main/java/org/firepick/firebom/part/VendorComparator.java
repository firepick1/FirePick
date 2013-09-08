package org.firepick.firebom.part;
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

import org.firepick.firebom.IPartComparable;

import java.util.Comparator;

public class VendorComparator implements Comparator<IPartComparable> {
    @Override
    public int compare(IPartComparable o1, IPartComparable o2) {
        Part part1 = o1.getPart();
        Part part2 = o2.getPart();
        int cmp = 0;
        if (part1 != part2) {
            if (part1 == null) {
                cmp = -1;
            } else if (part2 == null) {
                cmp = 1;
            } else {
                String vendor1 = part1.getVendor();
                String vendor2 = part2.getVendor();
                if (vendor1 != vendor2) {
                    if (vendor1 == null) {
                        cmp = -1;
                    } else if (vendor2 == null) {
                        cmp = 1;
                    } else {
                        cmp = vendor1.compareTo(vendor2);
                    }
                }
            }
        }
        if (cmp == 0) {
            cmp = part1.getUrl().toString().compareTo(part2.getUrl().toString());
        }

        return cmp;
    }
}
