package org.firepick.relation;
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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AggregatorTest {
    @Test
    public void testDoubleAggregator() {
        DoubleAggregator aggAverage = new DoubleAggregator(NumericAggregationType.AVERAGE);
        DoubleAggregator aggMin = new DoubleAggregator(NumericAggregationType.MIN);
        DoubleAggregator aggMax = new DoubleAggregator(NumericAggregationType.MAX);
        DoubleAggregator aggCount = new DoubleAggregator(NumericAggregationType.COUNT);
        DoubleAggregator aggSum = new DoubleAggregator(NumericAggregationType.SUM);

        assertEquals(0d, aggAverage.getAggregate(), 0);
        assertEquals(Double.NaN, aggMin.getAggregate(), 0);
        assertEquals(Double.NaN, aggMax.getAggregate(), 0);
        assertEquals(0d, aggCount.getAggregate(), 0);
        assertEquals(0d, aggSum.getAggregate(), 0);

        for (double d = 1.0; d < 5.0; d += 1.0) {
            assertEquals((int) d, aggAverage.aggregate(d).getCount());
            assertEquals((int) d, aggMin.aggregate(d).getCount());
            assertEquals((int) d, aggMax.aggregate(d).getCount());
            assertEquals((int) d, aggCount.aggregate(d).getCount());
            assertEquals((int) d, aggSum.aggregate(d).getCount());
        }

        assertEquals(2.5d, aggAverage.getAggregate(), 0);
        assertEquals(1d, aggMin.getAggregate(), 0);
        assertEquals(4d, aggMax.getAggregate(), 0);
        assertEquals(4d, aggCount.getAggregate(), 0);
        assertEquals(10d, aggSum.getAggregate(), 0);
    }
}
