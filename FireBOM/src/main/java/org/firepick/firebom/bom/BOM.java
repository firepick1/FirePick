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
import org.firepick.firebom.IRefreshableProxy;
import org.firepick.firebom.RefreshableTimer;
import org.firepick.firebom.exception.ApplicationLimitsException;
import org.firepick.firebom.part.Part;
import org.firepick.firebom.part.PartFactory;
import org.firepick.relation.IColumnDescription;
import org.firepick.relation.IRelation;
import org.firepick.relation.IRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

public class BOM implements IRelation, IRefreshableProxy {
    public final static String UNRESOLVED = "(Processing...)";
    private static Logger logger = LoggerFactory.getLogger(BOM.class);
    private List<IColumnDescription> columnDescriptions;
    private ConcurrentSkipListSet<IPartComparable> rows = new ConcurrentSkipListSet<IPartComparable>();
    private int maximumParts;
    private Map<BOMColumn, BOMColumnDescription> columnMap = new HashMap<BOMColumn, BOMColumnDescription>();
    private URL url;
    private String title;
    private RefreshableTimer refreshableTimer = new RefreshableTimer();
    private Part rootPart;

    public BOM(URL url) {
        this.url = url;
        this.title = UNRESOLVED;
        columnDescriptions = new ArrayList<IColumnDescription>();
        for (BOMColumn column : BOMColumn.values()) {
            BOMColumnDescription bomColumnDescription = BOMColumnDescription.create(column);
            columnDescriptions.add(bomColumnDescription);
            columnMap.put(column, bomColumnDescription);
        }
        this.rootPart = PartFactory.getInstance().createPart(url);
        addPart(rootPart, 1);
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
            bomRow.setQuantity(bomRow.getQuantity() + quantity);
        } else {
            if (maximumParts > 0 && rows.size() >= maximumParts) {
                throw new ApplicationLimitsException("Maximum part limit exceeded: " + maximumParts);
            }
            bomRow = new BOMRow(this, part);
            if (part == rootPart) {
                bomRow.setRootRow(true);
            }
            bomRow.setQuantity(quantity);
            rows.add(bomRow);
            logger.debug("addPart({})", part.getUrl());
        }
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
            Part part = row.getPart();
            if (part.getRefreshException() != null) {
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
        for (IPartComparable partComparable : rows) {
            BOMRow bomRow = (BOMRow) partComparable;
            if (!bomRow.isResolved()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public long getAge() {
        return refreshableTimer.getAge();
    }

    public boolean resolve() {
        if (!isResolved()) {
            for (IPartComparable partComparable : rows) {
                BOMRow bomRow = (BOMRow) partComparable;
                bomRow.resolve();
            }
        }
        if (rootPart != null && rootPart.isResolved()) {
            setTitle(rootPart.getTitle());
        }
        return isResolved();
    }

    @Override
    public void refresh() {
        for (IPartComparable row : rows) {
            Part part = row.getPart();
            if (!part.isFresh()) {
                part.refresh();
            }
        }
    }

    @Override
    public boolean isFresh() {
        for (IPartComparable row : rows) {
            Part part = row.getPart();
            if (!part.isFresh()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void sample() {
        refreshableTimer.sample();
    }

    public Part getRootPart() {
        return rootPart;
    }
}
