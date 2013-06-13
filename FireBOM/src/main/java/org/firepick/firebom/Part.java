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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public abstract class Part implements IPartComparable {
    private static Logger logger = LoggerFactory.getLogger(Part.class);
    private static Pattern startLink = Pattern.compile("<a\\s+href=\"");
    private static Pattern endLink = Pattern.compile("\"");
    protected final PartFactory partFactory;
    protected List<String> sourceList;
    protected List<PartUsage> requiredParts;
    private String id;
    private String title;
    private String vendor;
    private String project;
    private URL url;
    private double packageCost;
    private double packageUnits;
    private long lastValidationMillis;
    private boolean isValid;
    private int validating;

    public Part(PartFactory partFactory) {
        this.partFactory = partFactory;
        setPackageUnits(1);
        requiredParts = new ArrayList<PartUsage>();
    }

    public Part(PartFactory partFactory, URL url) {
        this(partFactory);
        setUrl(url);
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
        if (validating == 0 && !isValid) {
            validating++;
            long elapsedMillis = System.currentTimeMillis() - lastValidationMillis;
            if (elapsedMillis > partFactory.getValidationMillis()) {
                try {
                    clear();
                    update();
                    setValid(true);
                    logger.info("{} {} {}x{} {}", new Object[]{getId(), getTitle(), getPackageCost(), getPackageUnits(), getUrl()});
                }
                catch (Throwable e) {
                    logger.warn("Could not validate part {}", getUrl(), e);
                }
                finally {
                    lastValidationMillis = System.currentTimeMillis();
                }
            }
            validating--;
        }
    }

    protected void update() throws IOException {
        String content = partFactory.urlTextContent(getUrl());
        parseContent(content);
    }

    private void clear() {
        setId(null);
        setTitle(null);
        setVendor(null);
        setPackageCost(0);
        setPackageUnits(1);
        // do not clear URL;
    }

    protected void parseContent(String content) throws IOException {
        throw new NotImplementedException();
    }

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
        String id1 = getPart().getId();
        String id2 = that.getPart().getId();
        int cmp = 0;

        if (id1 != id2) {
            if (id1 == null) {
                cmp = -1;
            } else if (id2 == null) {
                cmp = 1;
            } else {
                cmp = id1.compareTo(id2);
            }
        }
        if (cmp == 0) {
            URL url1 = getUrl();
            URL url2 = that.getPart().getUrl();
            cmp = url1.toString().compareTo(url2.toString());
        }
        return cmp;
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

    public List<PartUsage> getRequiredParts() {
        validate();
        return Collections.unmodifiableList(requiredParts);
    }

    public String getProject() {
        return project == null ? getVendor() : project;
    }

    public Part setProject(String project) {
        this.project = project;
        return this;
    }
}
