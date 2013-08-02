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

import javax.net.ssl.*;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Locale.US;

public class CachedUrlResolver {
    private static Logger logger = LoggerFactory.getLogger(CachedUrlResolver.class);

    private String accept;
    private String language;
    private String userAgent;
    private String cookies;
    private String basicAuth;
    private long urlRequests;
    private long networkRequests;

    static {
        trustAll();
    }

    protected CachedUrlResolver() {
        this(Locale.getDefault());
    }

    protected CachedUrlResolver(Locale locale) {
        if (locale == US) {
            accept = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
            language = "en-US,en;q=0.8";
            userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36";
        }
    }

    private static void trustAll() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {}
            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
        } };
        final SSLContext sc;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    public String get(URL url) throws IOException {
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
                if (cookies != null) {
                    connection.setRequestProperty("Cookie", cookies);
                }
                if (basicAuth != null) {
                    connection.setRequestProperty("Authentication", basicAuth);
                }
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

    public long getUrlRequests() {
        return urlRequests;
    }

    public long getNetworkRequests() {
        return networkRequests;
    }

    public String getCookies() {
        return cookies;
    }

    public CachedUrlResolver setCookies(String cookies) {
        this.cookies = cookies;
        return this;
    }

    public String getAccept() {
        return accept;
    }

    public CachedUrlResolver setAccept(String accept) {
        this.accept = accept;
        return this;
    }

    public String getLanguage() {
        return language;
    }

    public CachedUrlResolver setLanguage(String language) {
        this.language = language;
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public CachedUrlResolver setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public String getBasicAuth() {
        return basicAuth;
    }

    public CachedUrlResolver setBasicAuth(String user, String password) {
        String credentials = user + ":" + password;
        String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(credentials.getBytes());
        this.basicAuth = basicAuth;
        return this;
    }
}
