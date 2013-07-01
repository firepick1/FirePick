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

import org.firepick.firebom.IPartComparable;
import org.firepick.firebom.IRefreshableProxy;
import org.firepick.firebom.RefreshableTimer;
import org.firepick.firebom.exception.CyclicReferenceException;
import org.firepick.firebom.exception.ProxyResolutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

public class Part implements IPartComparable, Serializable, IRefreshableProxy {
    private static Logger logger = LoggerFactory.getLogger(Part.class);
    private static Pattern startLink = Pattern.compile("<a\\s+href=\"");
    private static Pattern endLink = Pattern.compile("\"");

    protected List<String> sourceList;
    protected List<PartUsage> requiredParts;
    private Part sourcePart;
    private String id;
    private String title;
    private String vendor;
    private String project;
    private URL url;
    private Double packageCost;
    private Double packageUnits;
    private RefreshableTimer refreshableTimer;
    private RuntimeException refreshException;
    private boolean isResolved;
    private Lock refreshLock = new ReentrantLock();

    public Part() {
        this(PartFactory.getInstance());
    }

    public Part(PartFactory partFactory) {
        this.requiredParts = new ArrayList<PartUsage>();
        this.refreshableTimer = new RefreshableTimer();
        if (partFactory != null) {
            setMinRefeshInterval(partFactory.getMinRefreshInterval());
        }
    }

    public Part(PartFactory partFactory, URL url) {
        this(partFactory);
        setUrl(url);
    }

    public synchronized String getId() {
        String value = id;
        if (value == null) {
            if (sourcePart != null  && sourcePart.isResolved()) {
                value = sourcePart.getId();
            }
        }
        if (value == null && getRefreshException() != null) {
            value = "ERROR";
        }
        return value;
    }

    public synchronized Part setId(String id) {
        this.id = id;
        return this;
    }

    public URL getUrl() {
        return url;
    }

    public synchronized Part setUrl(URL url) {
        this.url = url;
        return this;
    }

    public synchronized URL getSourceUrl() {
        if (sourcePart == null) {
            return url;
        }
        return sourcePart.getUrl();
    }

    public synchronized double getPackageCost() {
        double cost = 0;

        if (packageCost == null) {
            if (sourcePart != null && sourcePart.isResolved()) {
                cost = sourcePart.getUnitCost(); // for abstract parts, the package cost is the unit cost
                logger.debug("packagetCost {} += {}", id, cost);
            }
            for (PartUsage partUsage : requiredParts) {
                double partCost = partUsage.getQuantity() * partUsage.getPart().getUnitCost();
                logger.debug("packagetCost {} += {} {}", new Object[]{id, partUsage.getPart().getId(), partCost});
                cost += partCost;
            }
        } else {
            cost = packageCost;
        }

        return cost;
    }

    public synchronized Part setPackageCost(Double packageCost) {
        this.packageCost = packageCost;
        return this;
    }

    public synchronized double getPackageUnits() {
        double units = 1;
        if (packageUnits == null) {
            if (sourcePart != null) {
                units = 1; // this part is abstract, so package units is always 1
            }
        } else {
            units = packageUnits;
        }
        return units;
    }

    public synchronized Part setPackageUnits(Double packageUnits) {
        if (packageUnits != null && packageUnits <= 0) {
            throw new IllegalArgumentException("package units cannot be zero or negative: " + packageUnits);
        }
        this.packageUnits = packageUnits;
        return this;
    }

    public synchronized double getUnitCost() {
        return getPackageCost() / getPackageUnits();
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
        String url = PartFactory.getInstance().scrapeText(value, startLink, endLink);
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

    public synchronized String getTitle() {
        String value = title;
        if (value == null) {
            if (getRefreshException() != null) {
                value = getRefreshException().getMessage();
            } else if (sourcePart != null && sourcePart.isResolved()) {
                value = sourcePart.getTitle();
            } else {
                value = getId();
            }
        }
        return value;
    }

    public synchronized Part setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public Part getPart() {
        return this;
    }

    @Override
    public int compareTo(IPartComparable that) {
        URL url1 = getUrl();
        URL url2 = that.getPart().getUrl();
        int cmp = url1.toString().compareTo(url2.toString());
        return cmp;
    }

    public synchronized String getVendor() {
        if (vendor == null) {
            if (sourcePart != null && sourcePart.isResolved()) {
                return sourcePart.getVendor();
            } else {
                return getUrl().getHost();
            }
        }
        return vendor;
    }

    public synchronized Part setVendor(String vendor) {
        this.vendor = vendor;
        return this;
    }

    public synchronized List<PartUsage> getRequiredParts() {
        return Collections.unmodifiableList(requiredParts);
    }

    public synchronized String getProject() {
        return project == null ? getVendor() : project;
    }

    public synchronized Part setProject(String project) {
        this.project = project;
        return this;
    }

    @Override
    public final void refresh() {
        if (isFresh() && getAge() < getMinRefeshInterval()) {
            return; // avoid busy work
        }
        synchronized (refreshLock) {
            try {
                long msStart = System.currentTimeMillis();
                setRefreshException(null);
                refreshFromRemote();
                long msElapsed = System.currentTimeMillis() - msStart;
                validate(this, null);
                isResolved = true;
                logger.info("refreshed {} {} {}x{} {} {}ms", new Object[]{id, packageCost, packageUnits, title, url, msElapsed});
                refreshableTimer.refresh();
            }
            catch (Exception e) {
                throw createRefreshException(e);
            }
        }
    }

    private RuntimeException createRefreshException(Exception e) {
        logger.warn("Could not refresh part {}", getUrl(), e);
        if (e != getRefreshException()) {
            if (e instanceof ProxyResolutionException) {
                setRefreshException((ProxyResolutionException) e);
            } else {
                ProxyResolutionException proxyResolutionException = new ProxyResolutionException(e);
                setRefreshException(proxyResolutionException);
            }
        }

        // The refresh exception may be temporary, so the proxy is treated as "fresh and resolved with error"
        isResolved = true;
        sourceList = null;
        sourcePart = null;
        requiredParts.clear();
        refreshableTimer.refresh();

        return getRefreshException();
    }

    public Part refreshAll() {
        refresh();
        for (PartUsage partUsage: requiredParts) {
            Part part = partUsage.getPart();
            part.refreshAll();
        }
        if (sourcePart != null) {
            sourcePart.refresh();
        }
        refresh();

        return this;
    }

    private void validate(Part part, Part rootPart) {
        if (part == rootPart) {
            rootPart.setRefreshException(new CyclicReferenceException("Cyclic part reference detected: " + url));
            throw getRefreshException();
        } else if (rootPart == null) {
            rootPart = part;
        }
        if (part.sourcePart != null) {
            validate(part.sourcePart, rootPart);
        }
        for (PartUsage partUsage : part.requiredParts) {
            validate(partUsage.getPart(), rootPart);
        }
    }

    protected void refreshFromRemote() throws Exception {
        String content = PartFactory.getInstance().urlTextContent(getUrl());
        refreshFromRemoteContent(content);
    }

    protected void refreshFromRemoteContent(String content) throws Exception {
        throw new NotImplementedException();
    }

    @Override
    public synchronized boolean isFresh() {
        return refreshableTimer.isFresh();
    }

    public boolean isFresh(boolean deep) {
        if (!isFresh()) {
            logger.info("stale1 {}", getUrl());
            return false;
        }
        if (deep) {
            if (sourcePart != null && !sourcePart.isFresh()) {
                logger.info("stale2 {}", sourcePart.getUrl());
                return false;
            }
            for (PartUsage partUsage : requiredParts) {
                Part part = partUsage.getPart();
                if (!part.isFresh()) {
                    logger.info("stale3 {}", part.getUrl());
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public synchronized void sample() {
        refreshableTimer.sample();
    }

    public synchronized long getRefreshInterval() {
        return refreshableTimer.getRefreshInterval();
    }

    public synchronized long getAge() {
        return refreshableTimer.getAge();
    }

    @Override
    public String toString() {
        return getId() + " " + getUrl().toString();
    }

    public synchronized Part getSourcePart() {
        return sourcePart;
    }

    public synchronized Part setSourcePart(Part sourcePart) {
        this.sourcePart = sourcePart;
        return this;
    }

    public RuntimeException getRefreshException() {
        return refreshException;
    }

    public void setRefreshException(RuntimeException refreshException) {
        this.refreshException = refreshException;
    }

    public boolean isResolved() {
        return isResolved;
    }

    public long getMinRefeshInterval() {
        return refreshableTimer.getMinRefreshInterval();
    }

    public void setMinRefeshInterval(long minRefeshInterval) {
        refreshableTimer.setMinRefreshInterval(minRefeshInterval);
    }
}
