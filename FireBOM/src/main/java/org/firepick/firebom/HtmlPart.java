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

public class HtmlPart extends Part {
    private URL sourceUrl;

    public HtmlPart(PartFactory partFactory, URL url) {
        super(partFactory, url);

    }

    @Override
    protected void refreshFromRemoteContent(String content) throws IOException {
        String[] ulParts = content.split("</ul>");
        for (String ulPart : ulParts) {
            if (ulPart.contains("@Sources")) {
                sourceList = parseListItemStrings(ulPart);
                if (sourceList.size() == 0) {
                    throw new RuntimeException("GitHub page has no @Sources tag");
                }
                sourceUrl = parseLink(sourceList.get(0));
                Part sourcePart = PartFactory.getInstance().createPart(sourceUrl);
                sourcePart.refresh();
                setSourcePart(sourcePart);
            } else if (ulPart.contains("@Require")) {
                List<String> requiredItems = parseListItemStrings(ulPart);
                requiredParts.clear();
                for (String required : requiredItems) {
                    URL link = parseLink(required);
                    double quantity = parseQuantity(required, 1);
                    Part part = PartFactory.getInstance().createPart(link);
                    part.refresh();
                    PartUsage partUsage = new PartUsage().setPart(part).setQuantity(quantity);
                    requiredParts.add(partUsage);
                }
            }
        }
    }


}


