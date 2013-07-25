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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Locale.US;

public class PartFactory implements Iterable<Part>, Runnable {
    public static long MIN_REFRESH_INTERVAL = 10000;
    private static Logger logger = LoggerFactory.getLogger(PartFactory.class);
    private static Thread worker;
    private static ConcurrentLinkedQueue<Part> refreshQueue = new ConcurrentLinkedQueue<Part>();
    private static PartFactory partFactory;
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
        if (locale == US) {
            accept = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
            language = "en-US,en;q=0.8";
            userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.94 Safari/537.36";
        }
    }

    public static PartFactory getInstance() {
        if (partFactory == null) {
            partFactory = new PartFactory();
        }
        return partFactory;
    }

    public List<Part> getRefreshQueue() {
        List<Part> list = new ArrayList<Part>();
        for (Part part : refreshQueue) {
            list.add(part);
        }
        return Collections.unmodifiableList(list);
    }

    public String urlTextContent(URL url) throws IOException {
        urlRequests++;
        Element cacheElement = getCache("URL-contents").get(url);
        if (cacheElement == null) {
            StringBuilder response;
            try {
                networkRequests++;
                URLConnection connection = url.openConnection();
                connection.setRequestProperty("Accept", accept);
                connection.setRequestProperty("Accept-Language", language);
                connection.setRequestProperty("User-Agent", userAgent);
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                response = new StringBuilder();
                String inputLine;

                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
            }
            catch (Exception e) {
                cacheElement = new Element(url, e);
                getCache("URL-contents").put(cacheElement);
                throw new ProxyResolutionException(url.toString(), e);
            }
            String content = response.toString();
            cacheElement = new Element(url, content);
            getCache("URL-contents").put(cacheElement);
            logger.info("urlTextContent => ({}B) {}", content.length(), url);
            return content;
        } else {
            if (cacheElement.getObjectValue() instanceof IOException) {
                logger.info("throwing cached exception for {}", url);
                throw (IOException) cacheElement.getObjectValue();
            } else {
                logger.info("urlTextContent => (cached) {}", url);
                return cacheElement.getObjectValue().toString();
            }
        }
    }

    public String scrapeText(String value, Pattern start, Pattern end) {
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

    private Ehcache getCache(String name) {
        return CacheManager.getInstance().addCacheIfAbsent(name);
    }

    public Part createPart(URL url) {
        Element cacheElement = getCache("org.firepick.firebom.part.Part").get(url);
        Part part;
        if (cacheElement == null) {
            String host = url.getHost();
            part = createPartForHost(url, host);
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

    private Part createPartForHost(URL url, String host) {
        Part part;
        if ("www.shapeways.com".equalsIgnoreCase(host)) {
            part = new ShapewaysPart(this, url);
        } else if ("shpws.me".equalsIgnoreCase(host)) {
            part = new ShapewaysPart(this, url);
        } else if ("www.mcmaster.com".equalsIgnoreCase(host)) {
            part = new McMasterCarrPart(this, url);
        } else if ("github.com".equalsIgnoreCase(host)) {
            part = new GitHubPart(this, url);
        } else if ("us.misumi-ec.com".equalsIgnoreCase(host)) {
            part = new MisumiPart(this, url);
        } else if ("www.inventables.com".equalsIgnoreCase(host)) {
            part = new InventablesPart(this, url);
        } else if ("www.ponoko.com".equalsIgnoreCase(host)) {
            part = new PonokoPart(this, url);
        } else if ("www.amazon.com".equalsIgnoreCase(host)) {
            part = new AmazonPart(this, url);
        } else if ("trinitylabs.com".equalsIgnoreCase(host)) {
            part = new TrinityLabsPart(this, url);
        } else if ("mock".equalsIgnoreCase(host)) {
            part = new MockPart(this, url);
        } else {
            part = new HtmlPart(this, url);
        }
        return part;
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
        return (int) Math.round(packageCost/unitCost);
    }

    public long getUrlRequests() {
        return urlRequests;
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

    public long getNetworkRequests() {
        return networkRequests;
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
