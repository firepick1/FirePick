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

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.firepick.firebom.exception.ProxyResolutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PartFactory implements Iterable<Part>, Runnable {
    public static long MIN_REFRESH_INTERVAL = 10000;
    private static Logger logger = LoggerFactory.getLogger(PartFactory.class);
    private static Thread worker;
    private static ConcurrentLinkedQueue<Part> refreshQueue = new ConcurrentLinkedQueue<Part>();
    private static PartFactory partFactory;
    private CachedUrlResolver urlResolver;
    private String accept;
    private String language;
    private String userAgent;
    private long urlRequests;
    private long networkRequests;
    private long minRefreshInterval = MIN_REFRESH_INTERVAL;

    protected PartFactory() {
        this(Locale.getDefault());
    }

    protected PartFactory(Locale locale) {
        this.urlResolver = new CachedUrlResolver(locale);
    }

    public static PartFactory getInstance() {
        if (partFactory == null) {
            partFactory = new PartFactory();
        }
        return partFactory;
    }

    public static String scrapeText(String value, Pattern start, Pattern end) {
        String result;
        Matcher startMatcher = start.matcher(value);
        if (!startMatcher.find()) {
            return null;
        }
        int iStart = startMatcher.end();

        Matcher endMatcher = end.matcher(value);
        if (!endMatcher.find(iStart)) {
            return null;
        }
        int iEnd = endMatcher.start();
        result = value.substring(iStart, iEnd);

        return result;
    }

    public static int estimateQuantity(double packageCost, double unitCost) {
        double highCost = unitCost + .005;
        double lowCost = unitCost - .005;
        int highQuantity = (int) Math.floor(packageCost / lowCost);
        int lowQuantity = (int) Math.ceil(packageCost / highCost);

        if (highQuantity == lowQuantity) {
            return highQuantity;
        }
        if (highQuantity == lowQuantity + 1) {
            return highQuantity % 2 == 0 ? highQuantity : lowQuantity; // even number is more likely
        }
        return (int) Math.round(packageCost / unitCost);
    }

    public List<Part> getRefreshQueue() {
        List<Part> list = new ArrayList<Part>();
        for (Part part : refreshQueue) {
            list.add(part);
        }
        return Collections.unmodifiableList(list);
    }

    public String urlTextContent(URL url) throws IOException {
        return urlResolver.get(url);
    }

    private Ehcache getCache(String name) {
        return CacheManager.getInstance().addCacheIfAbsent(name);
    }

    public Part createPart(URL url) {
        return createPart(url, urlResolver);
    }

    public Part createPart(URL url, CachedUrlResolver urlResolver) {
        Element cacheElement = getCache("org.firepick.firebom.part.Part").get(url);
        Part part;
        if (cacheElement == null) {
            String host = url.getHost();
            part = createPartForHost(url, host, urlResolver);
            cacheElement = new Element(url, part);
            getCache("org.firepick.firebom.part.Part").put(cacheElement);
            refreshQueue.add(part);
        } else {
            part = (Part) cacheElement.getObjectValue();
            part.sample();
            if (!part.isFresh() && !refreshQueue.contains(part)) {
                refreshQueue.add(part);
            }
        }
        if (worker == null) {
            worker = new Thread(this);
            worker.start();
        }
        return part;
    }

    private Part createPartForHost(URL url, String host, CachedUrlResolver urlResolver) {
        Part part;
        if ("www.shapeways.com".equalsIgnoreCase(host)) {
            part = new ShapewaysPart(this, url, urlResolver);
        } else if ("shpws.me".equalsIgnoreCase(host)) {
            part = new ShapewaysPart(this, url, urlResolver);
        } else if ("www.mcmaster.com".equalsIgnoreCase(host)) {
            part = new McMasterCarrPart(this, url, urlResolver);
        } else if ("github.com".equalsIgnoreCase(host)) {
            part = new GitHubPart(this, url, urlResolver);
        } else if ("us.misumi-ec.com".equalsIgnoreCase(host)) {
            part = new MisumiPart(this, url, urlResolver);
        } else if ("www.inventables.com".equalsIgnoreCase(host)) {
            part = new InventablesPart(this, url, urlResolver);
        } else if ("www.ponoko.com".equalsIgnoreCase(host)) {
            part = new PonokoPart(this, url, urlResolver);
        } else if ("www.amazon.com".equalsIgnoreCase(host)) {
            part = new AmazonPart(this, url, urlResolver);
        } else if ("trinitylabs.com".equalsIgnoreCase(host)) {
            part = new TrinityLabsPart(this, url, urlResolver);
        } else if ("mock".equalsIgnoreCase(host)) {
            part = new MockPart(this, url, urlResolver);
        } else if ("www.sparkfun.com".equalsIgnoreCase(host)) {
            part = new SparkfunPart(this, url, urlResolver);
        } else if ("www.adafruit.com".equalsIgnoreCase(host)) {
            part = new AdafruitPart(this, url, urlResolver);
        } else if ("www.digikey.com".equalsIgnoreCase(host)) {
            part = new DigiKeyPart(this, url, urlResolver);
        } else if ("synthetos.myshopify.com".equalsIgnoreCase(host)) {
            part = new SynthetosPart(this, url, urlResolver);
        } else {
            part = new HtmlPart(this, url, urlResolver);
        }
        return part;
    }

    @Override
    public ListIterator<Part> iterator() {
        Ehcache cache = getCache("org.firepick.firebom.part.Part");
        return new CacheIterator(cache);
    }

    @Override
    public void run() {
        while (refreshQueue.size() > 0) {
            Part part = refreshQueue.poll();
            if (part != null && !part.isFresh()) {
                try {
                    part.refresh();
                }
                catch (Exception e) {
                    if (e != part.getRefreshException()) {
                        throw new ProxyResolutionException("Uncaught exception", e);
                    }
                }
            }
        }
        worker = null;
    }

    public long getMinRefreshInterval() {
        return minRefreshInterval;
    }

    public PartFactory setMinRefreshInterval(long minRefreshInterval) {
        this.minRefreshInterval = minRefreshInterval;
        return this;
    }

    public class CacheIterator implements ListIterator<Part> {
        ListIterator<URL> listIterator;
        Ehcache ehcache;

        public CacheIterator(Ehcache ehcache) {
            this.listIterator = ehcache.getKeys().listIterator();
            this.ehcache = ehcache;
        }

        @Override
        public boolean hasNext() {
            return listIterator.hasNext();
        }

        @Override
        public Part next() {
            return (Part) ehcache.get(listIterator.next()).getObjectValue();
        }

        @Override
        public boolean hasPrevious() {
            return listIterator.hasPrevious();
        }

        @Override
        public Part previous() {
            return (Part) ehcache.get(listIterator.previous()).getObjectValue();
        }

        @Override
        public int nextIndex() {
            return listIterator.nextIndex();
        }

        @Override
        public int previousIndex() {
            return listIterator.previousIndex();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(Part part) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Part part) {
            throw new UnsupportedOperationException();
        }
    }
}
