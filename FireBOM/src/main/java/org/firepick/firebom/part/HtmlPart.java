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
import java.util.ArrayList;
import java.util.List;

public class HtmlPart extends Part {

    public HtmlPart(PartFactory partFactory, URL url) {
        super(partFactory, url);

    }

    @Override
    protected void refreshFromRemoteContent(String content) throws IOException {
        String[] ulParts = content.split("</ul>");
        List<String> newSourceList = null;
        Part newSourcePart = null;
        List<PartUsage> newRequiredParts = null;

        for (String ulPart : ulParts) {
            if (ulPart.contains("@Source")) {
                newSourceList = parseListItemStrings(ulPart);
                if (newSourceList.size() == 0) {
                    throw new ProxyResolutionException("Html page has no @Sources tag");
                }
                URL sourceUrl = parseLink(newSourceList.get(0));
                newSourcePart = PartFactory.getInstance().createPart(sourceUrl);
          //      newSourcePart.refresh();
            } else if (ulPart.contains("@Require")) {
                List<String> requiredItems = parseListItemStrings(ulPart);
                newRequiredParts = new ArrayList<PartUsage>();
                for (String required : requiredItems) {
                    URL link = parseLink(required);
                    double quantity = parseQuantity(required, 1);
                    Part part = PartFactory.getInstance().createPart(link);
         //           part.refresh();
                    PartUsage partUsage = new PartUsage().setPart(part).setQuantity(quantity);
                    newRequiredParts.add(partUsage);
                }
            }
        }

        if (newRequiredParts != null) {
            requiredParts = newRequiredParts;
        }
        if (newSourcePart != null) {
            sourceList = newSourceList;
            setSourcePart(newSourcePart);
        }
    }


}


