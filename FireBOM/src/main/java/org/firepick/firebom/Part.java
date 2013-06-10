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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public abstract class Part implements IPartComparable {
    private static Pattern startLink = Pattern.compile("<a\\s+href=\"");
    private static Pattern endLink = Pattern.compile("\"");
    protected final PartFactory partFactory;
    protected List<PartUsage> requiredParts;
    private String id;
    private String title;
    private String vendor;
    private URL url;
    private double packageCost;
    private double packageUnits;
    private boolean isValid;

    public Part(PartFactory partFactory) {
        this.partFactory = partFactory;
        setPackageUnits(1);
        requiredParts = new ArrayList<PartUsage>();
    }

    public Part(PartFactory partFactory, URL url) {
        this(partFactory);
        this.url = url;
    }

    public String getId() {
        validate();
        return id;
    }

    public Part setId(String id) {
        this.id = id;
        return this;
    }

    public URL getUrl() {
        return url;
    }

    public Part setUrl(URL url) {
        this.url = url;
        return this;
    }

    public double getPackageCost() {
        validate();
        return packageCost;
    }

    public Part setPackageCost(double packageCost) {
        this.packageCost = packageCost;
        return this;
    }

    public double getPackageUnits() {
        validate();
        return packageUnits;
    }

    public Part setPackageUnits(double packageUnits) {
        this.packageUnits = Math.max(1, packageUnits);
        return this;
    }

    public double getUnitCost() {
        validate();
        return getPackageCost() / getPackageUnits();
    }

    public synchronized void validate() {
        if (!isValid) {
            try {
                update();
            }
            catch (IOException e) {
                setTitle("WEBSITE UNAVAILABLE");
                setPackageCost(0);
                setPackageUnits(1);
            }
            setValid(true);
        }
    }

    /**
     * Update part information from web
     */
    protected abstract void update() throws IOException;

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    protected List<String> parseListItemStrings(String ul) throws IOException {
        List<String> result = new ArrayList<String>();
        String[] liParts = ul.split("</li>");
        for (String li : liParts) {
            String[] items = li.split("<li>");
            result.add(items[1]);
        }
        return result;
    }

    protected URL parseLink(String value) throws MalformedURLException {
        String url = partFactory.scrapeText(value, startLink, endLink);
        return new URL(getUrl(), url);
    }

    protected double parseQuantity(String value, double defaultValue) {
        double result = defaultValue;

        String[] phrases = value.split("\\(");
        if (phrases.length > 1) {
            String quantity = phrases[phrases.length - 1].split("\\)")[0];
            result = Double.parseDouble(quantity);
        }

        return result;
    }

    public List<PartUsage> getRequiredParts() {
        validate();
        return Collections.unmodifiableList(requiredParts);
    }

    public String getTitle() {
        validate();
        return title == null ? getId() : title;
    }

    public Part setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public Part getPart() {
        return this;
    }

    @Override
    public int compareTo(IPartComparable that) {
        return getId().compareTo(that.getPart().getId());
    }

    public String getVendor() {
        validate();
        if (vendor == null) {
            return getUrl().getHost();
        }
        return vendor;
    }

    public Part setVendor(String vendor) {
        this.vendor = vendor;
        return this;
    }
}
