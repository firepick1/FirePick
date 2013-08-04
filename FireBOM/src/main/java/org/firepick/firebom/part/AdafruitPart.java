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

import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

public class AdafruitPart extends HtmlPart {
    private static final Pattern startPrice = Pattern.compile("productPrices\" class=\"productGeneral\">\\s*\\$",Pattern.MULTILINE);
    private static final Pattern endPrice = Pattern.compile("<");
    private static final Pattern endTitle = Pattern.compile(":");
    private static final Pattern startId = Pattern.compile("<a href=\"products/");
    private static final Pattern endId = Pattern.compile("#");

    public AdafruitPart(PartFactory partFactory, URL url, CachedUrlResolver urlResolver) {
        super(partFactory, url, urlResolver);
    }

    @Override
    protected void refreshFromRemoteContent(String content) throws IOException {
        String title = PartFactory.getInstance().scrapeText(content, startTitle, endTitle);
        if (title != null) {
            setTitle(title);
        }
        String price = PartFactory.getInstance().scrapeText(content, startPrice, endPrice);
        if (price != null) {
            setPackageCost(Double.parseDouble(price));
        }
        String id = PartFactory.getInstance().scrapeText(content, startId, endId);
        if (id != null) {
            setId(id);
        }
    }

    @Override
    public String getVendor() {
        return "www.sparkfun.com";
    }
}
