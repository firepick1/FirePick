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

public class DoubleAggregator implements IAggregator<Double> {
    private NumericAggregationType aggregationType;
    private Double sum;
    private int count;
    private double aggregateValue;

    public DoubleAggregator(NumericAggregationType aggregationType) {
        this.aggregationType = aggregationType;
        clear();
    }

    @Override
    public void clear() {
        switch (aggregationType) {
            case MIN:
                aggregateValue = Double.MAX_VALUE;
                break;
            case MAX:
                aggregateValue = Double.MIN_VALUE;
                break;
            default:
                aggregateValue = 0d;
                sum = 0d;
                break;
        }
    }

    @Override
    public DoubleAggregator aggregate(Object that) {
        Double value = (Double) that;
        switch (aggregationType) {
            case MIN:
                if (value < aggregateValue) {
                    aggregateValue = value;
                }
                break;
            case MAX:
                if (value > aggregateValue) {
                    aggregateValue = value;
                }
                break;
            case AVERAGE:
                sum += value;
                break;
            default:
            case SUM:
                aggregateValue += value;
                break;
        }

        count++;
        return this;
    }

    @Override
    public long getCount() {
        return count;
    }

    @Override
    public Double getAggregate() {
        switch (aggregationType) {
            case COUNT:
                return (double) getCount();
            case MIN:
            case MAX:
                return getCount() == 0 ? Double.NaN : aggregateValue;
            case AVERAGE:
                return getCount() == 0 ? 0d : sum / getCount();
            default:
                return aggregateValue;
        }
    }
}
