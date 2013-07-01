package org.firepick.firebom.part;
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

import java.io.Serializable;

public class PartUsage implements Serializable, IPartComparable {
    private Part part;
    private double quantity;
    private String vendor;

    public Part getPart() {
        return part;
    }

    public PartUsage setPart(Part part) {
        this.part = part;
        return this;
    }

    public double getQuantity() {
        return quantity;
    }

    public PartUsage setQuantity(double quantity) {
        this.quantity = quantity;
        return this;
    }

    public double getCost() {
        return quantity * part.getUnitCost();
    }

    public String getVendor() {
        if (vendor == null) {
            return getPart().getVendor();
        }
        return vendor;
    }

    public PartUsage setVendor(String vendor) {
        this.vendor = vendor;
        return this;
    }

    @Override
    public String toString() {
        return part.toString();
    }

    @Override
    public int compareTo(IPartComparable that) {
        int cmp = getPart().compareTo(that.getPart());
        if (cmp == 0 && that instanceof PartUsage) {
            PartUsage thatPartUsage = (PartUsage) that;
            cmp = (int) Math.signum(getQuantity() - thatPartUsage.getQuantity());
        }
        return cmp;
    }

}
