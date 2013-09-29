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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class HtmlPart extends Part {
    protected static final Pattern startTitle = Pattern.compile("<title>");
    protected static final Pattern endTitle = Pattern.compile("</title>");

    public HtmlPart(PartFactory partFactory, URL url, CachedUrlResolver urlResolver) {
        super(partFactory, url, urlResolver);
    }

    @Override
    protected void refreshFromRemoteContent(String content) throws IOException {
        setId("UNSUPPORTED");
        setTitle("Unsupported FireBOM vendor http://bit.ly/16jPAOr");
        String[] ulParts = content.split("</ul>");
        List<String> newSourceList = null;
        PartUsage newSourcePartUsage = null;
        List<PartUsage> newRequiredParts = null;
        for (String ulPart : ulParts) {
            if (ulPart.contains("@Source")) {
                newSourceList = parseListItemStrings(ulPart);
                if (newSourceList.size() == 0) {
                    throw new ProxyResolutionException("Html page has no @Sources tag");
                }
                String primarySource = newSourceList.get(0);
                URL sourceUrl = parseLink(primarySource);
                Part sourcePart = PartFactory.getInstance().createPart(sourceUrl);
                Double quantity = parseQuantity(primarySource, null);
                if (quantity != null) {
                    // Package Unit Override
                    // ======================
                    // If source package units are specified, they apply to the source, which may not have the ability to
                    // specify the package units. Using the source package unit override automatically forces a
                    // package unit of 1 for THIS part. This convention permits the simplest specification of required parts
                    // (i.e., per single source unit).
                    newSourcePartUsage = new PartUsage(sourcePart, quantity);
                    this.setPackageUnits(1d);
                } else {
                    newSourcePartUsage = new PartUsage(sourcePart, 1);
                }
            } else if (ulPart.contains("@Require")) {
                List<String> requiredItems = parseListItemStrings(ulPart);
                newRequiredParts = new ArrayList<PartUsage>();
                for (String required : requiredItems) {
                    try {
                        URL link = parseLink(required);
                        double quantity = parseQuantity(required, 1d);
                        Part part = PartFactory.getInstance().createPart(link);
                        PartUsage partUsage = new PartUsage(part, quantity);
                        newRequiredParts.add(partUsage);
                    }
                    catch (MalformedURLException ex) {
                        if (required.startsWith("http")) {
                            throw ex;
                        } else {
                            // skip this part
                        }
                    }
                }
            }
        }

        if (newRequiredParts != null) {
            requiredParts = newRequiredParts;
        }
        if (newSourcePartUsage != null) {
            sourceList = newSourceList;
            setSourcePartUsage(newSourcePartUsage);
        }
    }

}


