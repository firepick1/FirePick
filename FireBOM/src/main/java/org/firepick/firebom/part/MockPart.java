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

import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

public class MockPart extends Part {
    private int refreshFromRemoteCount;

    public MockPart(PartFactory partFactory, URL url, CachedUrlResolver urlResolver) {
        super(partFactory, url, urlResolver);
        refreshFromRemote();
    }

    public void refreshFromRemote() {
        refreshFromRemoteCount++;
        try {
            URL url = getUrl();
            PartFactory partFactory = PartFactory.getInstance();
            String[] pathSegments = url.getQuery().split("&");
            ArrayList<PartUsage> newRequired = new ArrayList<PartUsage>();
            for (String pathSegment : pathSegments) {
                String[] tokens = pathSegment.split(":");
                String key = tokens[0];
                String value = tokens.length > 1 ? tokens[1] : null;
                String decodedValue = URLDecoder.decode(value,"utf-8");
                double qty = 1;
                if (tokens.length > 2) {
                    qty = Double.parseDouble(tokens[2]);
                }
                if (key.equals("id")) {
                    setId(value);
                } else if (key.equals("title")) {
                    setTitle(value);
                } else if (key.equals("source")) {
                    Part sourcePart = partFactory.createPart(new URL(decodedValue));
                    setSourcePartUsage(new PartUsage(sourcePart, qty));
                } else if (key.equals("require")) {
                    Part part = partFactory.createPart(new URL(decodedValue));
                    newRequired.add(new PartUsage(part, qty));
                } else if (key.equals("cost")) {
                    setPackageCost(Double.parseDouble(value));
                } else if (key.equals("units")) {
                    setPackageUnits(Double.parseDouble(value));
                } else if (key.equals("vendor")) {
                    setVendor(value);
                }
            }
            requiredParts = newRequired;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public int getRefreshFromRemoteCount() {
        return refreshFromRemoteCount;
    }
}
