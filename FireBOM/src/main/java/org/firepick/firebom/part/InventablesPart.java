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

public class InventablesPart extends Part {
    private static Pattern startId = Pattern.compile("<label for=\"sample_cart_item_sample_id_[0-9]*\">");
    private static Pattern endId = Pattern.compile("</label>");
    private static Pattern startPrice = Pattern.compile("<td>\\$");
    private static Pattern endPrice = Pattern.compile("</td>");
    private static Pattern startTitle = Pattern.compile("</label></td>\\s*<td>", Pattern.MULTILINE);
    private static Pattern endTitle = Pattern.compile("</td>");

    public InventablesPart(PartFactory partFactory, URL url, CachedUrlResolver urlResolver) {
        super(partFactory, url, urlResolver);
    }

    @Override
    protected void refreshFromRemoteContent(String content) throws IOException {
        String [] urlTokens = getUrl().toString().split("#");
        String searchContent = content;
        if (urlTokens.length > 1) {
            searchContent = content.split(urlTokens[1])[1];
        }
        String price = PartFactory.getInstance().scrapeText(searchContent, startPrice, endPrice);
        if (price != null) {
            setPackageCost(Double.parseDouble(price));
        }
        String id = PartFactory.getInstance().scrapeText(searchContent, startId, endId);
        if (id != null) {
            setId(id);
        }
        String title = PartFactory.getInstance().scrapeText(searchContent, startTitle, endTitle);
        if (title != null) {
            if (urlTokens.length > 1) {
                String [] urlPath = getUrl().getPath().split("/");
                title = urlPath[urlPath.length-1].toUpperCase() + " " + title;
            }
            setTitle(title);
        }
    }
}
