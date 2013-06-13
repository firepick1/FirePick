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
import java.util.regex.Pattern;

public class MisumiPart extends Part {
    private static final String priceTemplate = "http://us.misumi-ec.com/mydesk2/s/priceCalc?part_number={PART}&quantity=1&response_type=json&SKIP_LOGIN_CHECK=1";
    private static final String packageTemplate = "http://us.misumi-ec.com/us/StaticPageWysiwygArea.html?itemCd={ITEMCD}&tabNo=1";
    private static final Pattern startId = Pattern.compile("Keyword=|PNSearch=|KWSearch=");
    private static final Pattern endId = Pattern.compile("[^a-zA-Z0-9-]|$");
    private static final Pattern startPrice = Pattern.compile("\"CATALOG_PRICE\":");
    private static final Pattern endPrice = Pattern.compile(",");
    private static final Pattern startItem = Pattern.compile("/detail/");
    private static final Pattern endItem = Pattern.compile("/");
    private static final Pattern startPackage = Pattern.compile("\\]</font>");
    private static final Pattern endPackage = Pattern.compile(" pcs. per package");

    public MisumiPart(PartFactory partFactory, URL url) {
        super(partFactory, url);
    }
    
    @Override
    protected void parseContent(String content) throws IOException {
        String item = partFactory.scrapeText(content, startItem, endItem);
        item = item.substring(item.length()-11);
        String id = partFactory.scrapeText(getUrl().toString(), startId, endId);
        setId(id);
        String priceUrl = priceTemplate.replaceAll("\\{PART\\}",id);
        String partInfo = partFactory.urlTextContent(new URL(priceUrl));
        String price = partFactory.scrapeText(partInfo, startPrice, endPrice);
        if (price != null) {
            setPackageCost(Double.parseDouble(price));
        }
        String packageUrl = packageTemplate.replaceAll("\\{ITEMCD\\}", item);
        String packageText = partFactory.urlTextContent(new URL(packageUrl));
        String packageUnits = partFactory.scrapeText(packageText, startPackage, endPackage);
        if (packageUnits != null) {
            setPackageUnits(Double.parseDouble(packageUnits));
        }
    }
}
