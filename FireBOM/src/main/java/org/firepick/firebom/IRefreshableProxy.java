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

public interface IRefreshableProxy {

    /**
     * Synchronize proxy with remote resource
     */
    void refresh();

    /**
     * A newly constructed proxy is not fresh until it is refreshed.
     * Freshness lasts until a refresh timeout. Unsampled proxies stay fresh
     * forever.
     * @return true if proxy has been recently refreshed or never sampled
     */
    boolean isFresh();

    /**
     * Use the information provided by the proxy. Frequently sampled proxies should be
     * refreshed more often than rarely sampled proxies. Sampling a proxy affects its
     * freshness as well as the refresh interval.
     */
    void sample();

    /**
     * The expected refresh interval can be adaptive--if a resource is sampled frequently,
     * it needs to be refreshed frequently. Rarely sampled resources should be sampled less frequently.
     * @return expected refresh interval in milliseconds
     */
    long getExpectedRefreshMillis();
}
