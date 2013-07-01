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

import org.firepick.relation.*;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;

public class BOMColumnDescription<T> extends ColumnDescription<T> {

    private BOMColumnDescription(int index, String id, String title, int width, Format format, IAggregator<T> aggregator) {
        setIndex(index).setAggregator(aggregator).setId(id).setTitle(title).setFormat(format).setWidth(width);
    }

    public static BOMColumnDescription create(BOMColumn column) {
        switch (column) {
            case ID:
                return new BOMColumnDescription<String>(column.ordinal(), "id", "ID", 4, new TextFormat(), new CountingAggregator());
            case TITLE:
                return new BOMColumnDescription<String>(column.ordinal(), "title", "TITLE", 0, null, new StringAggregator("TOTAL"));
            case QUANTITY:
                return new BOMColumnDescription<Double>(column.ordinal(), "qty", "QTY", 3, new DecimalFormat(), new DoubleAggregator(NumericAggregationType.SUM));
            case COST:
                return new BOMColumnDescription<Double>(column.ordinal(), "cost", "COST", 9, NumberFormat.getCurrencyInstance(), new DoubleAggregator(NumericAggregationType.SUM));
            case VENDOR:
                return new BOMColumnDescription<String>(column.ordinal(), "vendor", "VENDOR", 20, new TextFormat(), new StringAggregator("TOTAL"));
            case URL:
                return new BOMColumnDescription<String>(column.ordinal(), "url", "URL", 0, null, new StringAggregator("TOTAL"));
            case PROJECT:
                return new BOMColumnDescription<String>(column.ordinal(), "project", "PROJECT", 10, new TextFormat(), new StringAggregator("TOTAL"));
            case SOURCE:
                return new BOMColumnDescription<String>(column.ordinal(), "source", "SOURCE", 0, null, new StringAggregator("TOTAL"));
        }

        throw new RuntimeException("Unknown BOMColumn " + column);
    }

}
