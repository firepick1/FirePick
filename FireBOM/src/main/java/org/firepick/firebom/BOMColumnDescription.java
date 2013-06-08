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

import org.firepick.relation.ColumnDescription;
import org.firepick.relation.IColumnDescription;

import java.text.DecimalFormat;
import java.text.Format;
import java.net.URL;

public enum BOMColumnDescription implements IColumnDescription {
    ID(0, "id", "ID", String.class, null),
    TITLE(1, "title", "TITLE", String.class, null),
    QUANTITY(2, "qty", "QUANTITY", Double.class, new DecimalFormat()),
    UNIT_COST(3, "cost", "COST", Double.class, new DecimalFormat()),
    URL(4, "url", "URL", URL.class, null),
    ;

    private ColumnDescription columnDescription;
    private int index;

    private BOMColumnDescription(int index, String id, String title, Class dataClass, Format format) {
        this.index = index;
        this.columnDescription = new ColumnDescription().setId(id).setTitle(title).setFormat(format).setDataClass(dataClass);
    }

    @Override
    public String getTitle() {
        return columnDescription.getTitle();
    }

    @Override
    public String getId() {
        return columnDescription.getId();
    }

    @Override
    public Class getDataClass() {
        return columnDescription.getDataClass();
    }

    @Override
    public Format getFormat() {
        return columnDescription.getFormat();
    }

    public int getIndex() {
        return index;
    }
}
