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

import java.text.Format;

public class BOMRow extends PartUsage implements IRow, IPartComparable {
    private BOM bom;
    private boolean marked;

    public BOMRow(BOM bom, Part part) {
        this.bom = bom;
        setPart(part);
    }

    @Override
    public IRelation getRelation() {
        return bom;
    }

    @Override
    public Object item(int index) {
        Object value = null;
        if (index == BOMColumnDescription.ID.getIndex()) {
            value = getPart().getId();
        } else if (index == BOMColumnDescription.QUANTITY.getIndex()) {
            value = getQuantity();
        } else if (index == BOMColumnDescription.COST.getIndex()) {
            value = getCost();
        } else if (index == BOMColumnDescription.URL.getIndex()) {
            value = getPart().getUrl();
        } else if (index == BOMColumnDescription.SOURCE.getIndex()) {
            value = getPart().getSourceUrl();
        } else if (index == BOMColumnDescription.TITLE.getIndex()) {
            value = getPart().getTitle();
        } else if (index == BOMColumnDescription.VENDOR.getIndex()) {
            value = getVendor();
        } else if (index == BOMColumnDescription.PROJECT.getIndex()) {
            value = getPart().getProject();
        }
        return value;
    }

    @Override
    public int compareTo(IPartComparable that) {
        return getPart().compareTo(that.getPart());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (IColumnDescription columnDescription : BOMColumnDescription.values()) {
            Format format = columnDescription.getFormat();
            Object value = item(columnDescription.getIndex());
            if (sb.length() > 0) {
                sb.append(", ");
            }
            if (format == null) {
                sb.append(value);
            } else {
                sb.append(format.format(value));
            }
        }
        return sb.toString();
    }

    public boolean isMarked() {
        return marked;
    }

    public BOMRow setMarked(boolean marked) {
        this.marked = marked;
        return this;
    }
}
