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

import org.firepick.relation.*;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class BOMColumnDescription<T> extends ColumnDescription<T> {
    public final static BOMColumnDescription<String> TITLE
            = new BOMColumnDescription<String>(0, "title", "TITLE", 0, null, new StringAggregator("TOTAL"));
    public final static BOMColumnDescription<String> ID
            = new BOMColumnDescription<String>(1, "id", "ID", 4, new TextFormat(), new CountingAggregator());
    public final static BOMColumnDescription<Double> QUANTITY
            = new BOMColumnDescription<Double>(2, "qty", "QTY", 3, new DecimalFormat(), new DoubleAggregator(NumericAggregationType.SUM));
    public final static BOMColumnDescription<Double> COST
            = new BOMColumnDescription<Double>(3, "cost", "COST", 9, NumberFormat.getCurrencyInstance(), new DoubleAggregator(NumericAggregationType.SUM));
    public final static BOMColumnDescription<String> VENDOR
            = new BOMColumnDescription<String>(4, "vendor", "VENDOR", 20, new TextFormat(), new StringAggregator("TOTAL"));
    public final static BOMColumnDescription<String> URL
            = new BOMColumnDescription<String>(5, "url", "URL", 0, null, new StringAggregator("TOTAL"));
    public final static BOMColumnDescription<String> PROJECT
            = new BOMColumnDescription<String>(6, "project", "PROJECT", 10, new TextFormat(), new StringAggregator("TOTAL"));
    public final static BOMColumnDescription<String> SOURCE
            = new BOMColumnDescription<String>(7, "source", "SOURCE", 0, null, new StringAggregator("TOTAL"));

    public BOMColumnDescription(int index, String id, String title, int width, Format format, IAggregator<T> aggregator) {
        setIndex(index).setAggregator(aggregator).setId(id).setTitle(title).setFormat(format).setWidth(width);
    }

    public static List<BOMColumnDescription> values() {
        ArrayList<BOMColumnDescription> list = new ArrayList<BOMColumnDescription>();
        list.add(PROJECT);
        list.add(ID);
        list.add(QUANTITY);
        list.add(COST);
        list.add(VENDOR);
        list.add(TITLE);
        list.add(URL);
        list.add(SOURCE);
        return list;
    }

    @Override
    public BOMColumnDescription clone() {
        try {
            return (BOMColumnDescription) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
