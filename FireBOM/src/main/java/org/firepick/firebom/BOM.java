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
    private int maximumParts;

    @Override
    public List<IColumnDescription> describeColumns() {
        if (columnDescriptions == null) {
            columnDescriptions = new ArrayList<IColumnDescription>();
            for (BOMColumnDescription columnDescription : BOMColumnDescription.values()) {
                columnDescriptions.add(columnDescription.clone());
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
            if (bomRow.isMarked()) {
                throw new ApplicationLimitsException("Recursive BOM detected: " + part.getUrl());
            }
            bomRow.setQuantity(bomRow.getQuantity() + quantity);
        } else {
            if (maximumParts > 0 && rows.size() >= maximumParts) {
                throw new ApplicationLimitsException("Maximum part limit exceeded: " + maximumParts);
            }
            bomRow = new BOMRow(this, part);
            bomRow.setQuantity(quantity);
            rows.add(bomRow);
        }
        bomRow.setMarked(true);
        for (PartUsage partUsage : part.getRequiredParts()) {
            addPart(partUsage.getPart(), partUsage.getQuantity() * quantity);
        }
        bomRow.setMarked(false);
        return bomRow;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (IColumnDescription columnDescription : describeColumns()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(columnDescription.getTitle());
        }
        return sb.toString();
    }

    public double totalCost() {
        double cost = 0;
        for (IPartComparable row : rows) {
            BOMRow bomRow = (BOMRow) row;
            cost += bomRow.getCost();
        }
        return cost;
    }

    public int partCount() {
        int count = 0;
        for (IPartComparable row : rows) {
            BOMRow bomRow = (BOMRow) row;
            count += bomRow.getQuantity();
        }
        return count;
    }

    public boolean isValid() {
        for (IPartComparable row : rows) {
            if (!row.getPart().isValid()) {
                return false;
            }
        }
        return true;
    }

    public int getMaximumParts() {
        return maximumParts;
    }

    public BOM setMaximumParts(int maximumParts) {
        this.maximumParts = maximumParts;
        return this;
    }
}
