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

import org.firepick.firebom.IPartComparable;
import org.firepick.firebom.part.VendorComparator;
import org.firepick.relation.IRow;

import java.util.Iterator;
import java.util.TreeSet;

public class BOMRowIterator implements Iterator<IRow> {
    private Iterator<BOMRow> iterator;


    public BOMRowIterator(Iterator<IPartComparable> iterator) {
        TreeSet<BOMRow> treeSet = new TreeSet(new VendorComparator());
        while (iterator.hasNext()) {
            treeSet.add((BOMRow) iterator.next());
        }
        this.iterator = treeSet.iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public IRow next() {
        return iterator.next();
    }

    @Override
    public void remove() {
        iterator.remove();
    }
}
