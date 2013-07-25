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
import java.util.regex.Pattern;

public class McMasterCarrPart extends Part {
    private static Pattern startPrice = Pattern.compile("\"PrceTxt\":\"");
    private static Pattern endPrice = Pattern.compile("\"");
    private static Pattern startPackageUnits = Pattern.compile("\"SellStdPkgQty\":");
    private static Pattern endPackageUnits = Pattern.compile(",");
    private static Pattern startDetail = Pattern.compile("data-mcm-attr-comp-itm-ids=\"");
    private static Pattern endDetail = Pattern.compile(",");
    private static Pattern startDetailPrice = Pattern.compile("\"PrceTxt\":\"\\$");
    private static Pattern endDetailPrice = Pattern.compile("[^0-9.]*\"");
    private static String queryUrlTemplate =
            "http://www.mcmaster.com/WebParts/Ordering/InLnOrdWebPart/InLnOrdWebPart.aspx?cntnridtxt=InLnOrd_ItmBxRw_1_{PART}&partnbrtxt={PART}&multipartnbrind=false&partnbrslctdmsgcntxtnm=FullPrsnttn&autoslctdind=false";
    private static String detailQueryTemplate =
            "http://www.mcmaster.com/WebParts/Content/ItmPrsnttnWebPart.aspx?partnbrtxt={PART}&attrnm=&attrval=&attrcompitmids=&cntnridtxt=MainContent&proddtllnkclickedInd=true&cntnrWdth=1680";
    private static String detailPriceQueryTemplate =
            "http://www.mcmaster.com/WebParts/Content/ItmPrsnttnDynamicDat.aspx?acttxt=dynamicdat&partnbrtxt={PART}&isinlnspec=true&attrCompIds={DETAIL}";

    public McMasterCarrPart(PartFactory partFactory, URL url) {
        super(partFactory, url);
    }

    @Override
    public URL normalizeUrl(URL url) {
        String normalizedUrl = url.toString().replaceAll("/=[a-z0-9]*", "");
        try {
            return new URL(normalizedUrl);
        }
        catch (MalformedURLException e) {
            throw new ProxyResolutionException(url.toString(), e);
        }
    }

    @Override
    protected void refreshFromRemote() throws IOException {
        String partNum = getUrl().toString().replace("http://www.mcmaster.com/#", "");
        String queryUrl = queryUrlTemplate.replaceAll("\\{PART\\}", partNum);
        String price = null;
        String queryContent = PartFactory.getInstance().urlTextContent(new URL(queryUrl));
        price = PartFactory.getInstance().scrapeText(queryContent, startPrice, endPrice);
        if (price != null && price.length() == 0) {
            String detailUrl = detailQueryTemplate.replaceAll("\\{PART\\}", partNum);
            String detailContent = PartFactory.getInstance().urlTextContent(new URL(detailUrl));
            String detail = PartFactory.getInstance().scrapeText(detailContent, startDetail, endDetail);
            String detailPriceUrl = detailPriceQueryTemplate.replaceAll("\\{DETAIL\\}", detail).replaceAll("\\{PART\\}", partNum);
            String detailPriceContent = PartFactory.getInstance().urlTextContent(new URL(detailPriceUrl));
            price = PartFactory.getInstance().scrapeText(detailPriceContent, startDetailPrice, endDetailPrice);
        }
        if (price != null) {
            setPackageCost(Double.parseDouble(price));
        }
        String packageUnits = PartFactory.getInstance().scrapeText(queryContent, startPackageUnits, endPackageUnits);
        if (packageUnits != null) {
            double value = Double.parseDouble(packageUnits);
            if (value == 0) {
                value = 1;
            }
            setPackageUnits(value);
        }

        setId(partNum);
    }
}
