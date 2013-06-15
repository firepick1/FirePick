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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

public class BOM implements IRelation {
    private static Logger logger = LoggerFactory.getLogger(BOM.class);

    public final static String UNRESOLVED = "(Unresolved)";
    private List<IColumnDescription> columnDescriptions;
    private TreeSet<IPartComparable> rows = new TreeSet<IPartComparable>();
    private int maximumParts;
    private Map<BOMColumn, BOMColumnDescription> columnMap = new HashMap<BOMColumn, BOMColumnDescription>();
    private URL url;
    private String title;
    private boolean isResolved;

    public BOM(URL url) {
        this.url = url;
        this.title = UNRESOLVED;
        columnDescriptions = new ArrayList<IColumnDescription>();
        for (BOMColumn column : BOMColumn.values()) {
            BOMColumnDescription bomColumnDescription = BOMColumnDescription.create(column);
            columnDescriptions.add(bomColumnDescription);
            columnMap.put(column, bomColumnDescription);
        }
    }

    public synchronized BOM resolve(PartFactory partFactory) {
        if (!isResolved) {
            Part part = partFactory.createPart(url);
            addPart(part, 1);
            setResolved(true);
            setTitle(part.getTitle());
        }
        return this;
    }

    @Override
    public List<IColumnDescription> describeColumns() {
        return Collections.unmodifiableList(columnDescriptions);
    }

    public BOMColumnDescription getColumn(BOMColumn column) {
        return columnMap.get(column);
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

    protected BOMRow addPart(Part part, double quantity) {
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
            logger.info("addPart({})", part.getId());
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

    public URL getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public BOM setTitle(String title) {
        this.title = title;
        return this;
    }

    public boolean isResolved() {
        return isResolved;
    }

    public BOM setResolved(boolean resolved) {
        isResolved = resolved;
        return this;
    }
}
