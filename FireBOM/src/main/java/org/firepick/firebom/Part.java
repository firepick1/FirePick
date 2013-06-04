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

import java.io.IOException;
import java.net.URL;

public abstract class Part {
    private String id;
    private URL url;
    private double packageCost;
    private double packageUnits;

    public Part() {
        setPackageUnits(1);
    }

    public String getId() {
        return id;
    }

    public Part setId(String id) {
        this.id = id;
        return this;
    }

    public URL getUrl() {
        return url;
    }

    public Part setUrl(URL url) {
        this.url = url;
        return this;
    }

    public double getPackageCost() {
        return packageCost;
    }

    public Part setPackageCost(double packageCost) {
        this.packageCost = packageCost;
        return this;
    }

    public double getPackageUnits() {
        return packageUnits;
    }

    public Part setPackageUnits(double packageUnits) {
        this.packageUnits = packageUnits;
        return this;
    }

    public double getUnitCost() {
        return getPackageCost() / getPackageUnits();
    }

    /**
     * Update part information from web
     */
    abstract public void update() throws IOException;
}
