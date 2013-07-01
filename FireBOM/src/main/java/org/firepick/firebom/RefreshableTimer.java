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

import java.io.Serializable;

/**
 * An exponentially averaged timer for implementing refreshable proxies.
 * Timer adapts the expected refresh interval based on past refresh() and sample() calls.
 */
public class RefreshableTimer implements IRefreshableProxy, Serializable {
    private long minRefreshInterval;
    private long lastRefreshMillis;
    private long lastSampleMillis;
    private double sensitivity;
    private boolean isResolved;
    private long samplesSinceRefresh;
    private long sampleInterval;

    public RefreshableTimer() {
        this(0.8d);
    }

    public RefreshableTimer(double sensitivity) {
        if (sensitivity < 0 || 1 < sensitivity) {
            throw new IllegalArgumentException("sensitivity must be between [0..1]");
        }
        this.sensitivity = sensitivity;
        this.lastRefreshMillis = System.currentTimeMillis();
        this.lastSampleMillis = lastRefreshMillis;
    }

    public void refresh() {
        lastRefreshMillis = System.currentTimeMillis();
        samplesSinceRefresh = 0;
        isResolved = true;
    }

    public void sample() {
        samplesSinceRefresh++;
        long nowMillis = System.currentTimeMillis();
        long msElapsed = nowMillis - lastSampleMillis;
        if (isResolved()) {
            sampleInterval =(long)(getSensitivity() * msElapsed + (1 - getSensitivity()) * sampleInterval);
        } else {
            sampleInterval = Math.max(1, msElapsed);
        }
        lastSampleMillis = nowMillis;
    }

    public boolean isFresh() {
        long refreshInterval = getRefreshInterval();
        long ageDiff = refreshInterval - getAge();
        return isResolved() && ageDiff >= 0;
    }

    public double getSensitivity() {
        return sensitivity;
    }

    public long getSamplesSinceRefresh() {
        return samplesSinceRefresh;
    }

    public boolean isResolved() {
        return isResolved;
    }

    protected RefreshableTimer setResolved(boolean value) {
        isResolved = value;
        return this;
    }

    public long getAge() {
        return System.currentTimeMillis() - lastRefreshMillis;
    }

    public long getRefreshInterval() {
        Long value  = getSampleInterval();
        return Math.max(getMinRefreshInterval(), value);
    }

    public long getSampleInterval() {
        return sampleInterval;
    }

    public long getMinRefreshInterval() {
        return minRefreshInterval;
    }

    public RefreshableTimer setMinRefreshInterval(long minRefreshInterval) {
        this.minRefreshInterval = minRefreshInterval;
        return this;
    }
}
