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

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Locale.US;

public class PartFactory {
    private static PartFactory partFactory;
    private String accept;
    private String language;
    private String userAgent;
    private long validationMillis;

    protected PartFactory() {
        this(Locale.getDefault());
    }

    protected PartFactory(Locale locale) {
        setValidationMillis(5000);
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

    public String urlTextContent(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("Accept", accept);
        connection.setRequestProperty("Accept-Language", language);
        connection.setRequestProperty("User-Agent", userAgent);
        InputStreamReader isr = new InputStreamReader(connection.getInputStream());
        BufferedReader br = new BufferedReader(isr);
        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = br.readLine()) != null) {
            response.append(inputLine);
        }
        br.close();

        return response.toString();
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

    private Ehcache getCache() {
        return CacheManager.getInstance().addCacheIfAbsent("FireBOM");
    }
    public Part createPart(URL url) {
        Element cacheElement = getCache().get(url);
        if (cacheElement != null) {
            return (Part) cacheElement.getObjectValue();
        }
        String host = url.getHost();
        Part part = createPartForHost(url, host);

        cacheElement = new Element(url, part);
        getCache().put(cacheElement);

        return part;
    }

    private Part createPartForHost(URL url, String host)  {
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
        } else {
            part = new HtmlPart(this, url);
        }
        return part;
    }

    public long getValidationMillis() {
        return validationMillis;
    }

    public void setValidationMillis(long validationMillis) {
        this.validationMillis = validationMillis;
    }
}
