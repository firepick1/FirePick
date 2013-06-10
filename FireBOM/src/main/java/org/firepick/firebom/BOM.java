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

import org.firepick.relation.IColumnDescription;
import org.firepick.relation.IRelation;
import org.firepick.relation.IRow;

import java.util.*;

public class BOM implements IRelation {
    private List<IColumnDescription> columnDescriptions;
    private TreeSet<IPartComparable> rows = new TreeSet<IPartComparable>();

    @Override
    public List<IColumnDescription> describeColumns() {
        if (columnDescriptions == null) {
            columnDescriptions = new ArrayList<IColumnDescription>();
            for (IColumnDescription columnDescription : BOMColumnDescription.values()) {
                columnDescriptions.add(columnDescription);
            }
        }
        return Collections.unmodifiableList(columnDescriptions);
    }

    @Override
    public long getRowCount() {
        return rows.size();
    }

    @Override
    public Iterator<IRow> iterator() {
        return new BOMRowIterator(rows.iterator());
    }

    public BOMRow lookup(IPartComparable part) {
        IPartComparable existingBOMRow = rows.floor(part);
        if (existingBOMRow != null && existingBOMRow.compareTo(part) == 0) {
            return (BOMRow) existingBOMRow;
        }
        return null;
    }

    public BOMRow addPart(Part part, double quantity) {
        BOMRow bomRow = lookup(part);
        if (bomRow != null) {
            bomRow.setQuantity(bomRow.getQuantity() + quantity);
        } else {
            bomRow = new BOMRow(this, part);
            bomRow.setQuantity(quantity);
            rows.add(bomRow);
        }
        for (PartUsage partUsage: part.getRequiredParts()) {
            addPart(partUsage.getPart(), partUsage.getQuantity() * quantity);
        }
        return bomRow;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (IColumnDescription columnDescription: describeColumns()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(columnDescription.getTitle());
        }
        return sb.toString();
    }

    public double totalCost() {
        double cost = 0;
        for (IPartComparable row: rows) {
            BOMRow bomRow = (BOMRow) row;
            cost += bomRow.getCost();
        }
        return cost;
    }

    public int partCount() {
        int count = 0;
        for (IPartComparable row: rows) {
            BOMRow bomRow = (BOMRow) row;
            count += bomRow.getQuantity();
        }
        return count;
    }
}
