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

import org.firepick.firebom.exception.ProxyResolutionException;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

public class AmazonPart extends Part {
    private static Pattern startPrice = Pattern.compile("priceLarge\">\\$");
    private static Pattern endPrice = Pattern.compile("<");
    private static Pattern startTitle = Pattern.compile("AsinTitle\"\\s*>");
    private static Pattern endTitle = Pattern.compile("</");
    private static Pattern startUnitCost = Pattern.compile("actualPriceExtraMessaging\">\\s*<span class=\"pricePerUnit\">\\(\\$", Pattern.MULTILINE);
    private static Pattern endUnitCost = Pattern.compile("/\\s?count");

    public AmazonPart(PartFactory partFactory, URL url)  {
        super(partFactory, url);
    }

    @Override
    protected void refreshFromRemoteContent(String content) throws IOException {
        String title = PartFactory.getInstance().scrapeText(content, startTitle, endTitle);
        if (title != null) {
            title = title.replaceAll("Amazon.com:\\s?", "");
            setTitle(title);
        }
        String price = PartFactory.getInstance().scrapeText(content, startPrice, endPrice);
        if (price != null) {
            setPackageCost(Double.parseDouble(price));
        }
        String unitCostStr = PartFactory.getInstance().scrapeText(content, startUnitCost, endUnitCost);
        if (unitCostStr != null) {
            try {
                double unitCost = Double.parseDouble(unitCostStr);
                long units = PartFactory.estimateQuantity(getPackageCost(), unitCost);
                setPackageUnits((double) units);
            } catch (Exception e) {
                // ignore
            }
        }
        String [] urlTokens = getUrl().toString().split("/");
        String id;
        switch (urlTokens.length) {
            case 5: id = urlTokens[4];
                break;
            case 6:
            case 7:
                id = urlTokens[5];
                break;
            default:
                throw new ProxyResolutionException("Could not parse www.amazon.com url: " + getUrl());
        }
        if (id != null) {
            setId(id);
        }
    }

    @Override
    public String getVendor() {
        return "www.amazon.com";
    }
}
