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
import java.util.List;
import java.util.regex.Pattern;

public class GitHubPart extends Part {
    private static Pattern startTitle = Pattern.compile("<title>");
    private static Pattern endTitle = Pattern.compile("[< ]");
    private List<String> sourceList;

    public GitHubPart(PartFactory partFactory, URL url) throws IOException {
        super(partFactory);
        setUrl(url);
    }

    @Override
    protected void update() throws IOException {
        String content = partFactory.urlTextContent(getUrl());
        String title = partFactory.scrapeText(content, startTitle, endTitle);
        setId(title);

        String[] ulParts = content.split("</ul>");
        double cost = 0;
        for (String ulPart : ulParts) {
            if (ulPart.contains("@Sources")) {
                sourceList = parseListItemStrings(ulPart);
                if (sourceList.size() == 0) {
                    throw new RuntimeException("GitHub page has no @Sources tag");
                }
                URL sourceUrl = parseLink(sourceList.get(0));
                Part sourcePart = partFactory.createPart(sourceUrl);
                cost += sourcePart.getUnitCost();
            } else if (ulPart.contains("@Required")) {
                List<String> requiredItems = parseListItemStrings(ulPart);
                requiredParts.clear();
                for (String required : requiredItems) {
                    URL link = parseLink(required);
                    double quantity = parseQuantity(required, 1);
                    Part part = partFactory.createPart(link);
                    PartUsage partUsage = new PartUsage().setPart(part).setQuantity(quantity);
                    requiredParts.add(partUsage);
                    cost += quantity * part.getUnitCost();
                }
            }
        }
        setPackageCost(cost);
    }

}
