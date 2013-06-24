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
    private double msExpected;
    private long msLastRefresh;
    private double sensitivity;
    private long samplesSinceRefresh;

    public RefreshableTimer() {
        this(0.8d);
    }

    public RefreshableTimer(double sensitivity) {
        if (sensitivity < 0 || 1 < sensitivity) {
            throw new IllegalArgumentException("sensitivity must be between [0..1]");
        }
        this.sensitivity = sensitivity;
        this.msLastRefresh = System.currentTimeMillis();
        this.msExpected = 0;
    }

    public long getExpectedRefreshMillis() {
        if (isFresh()) {
            return (long) msExpected;
        }
        long now = System.currentTimeMillis();
        return (long) (double) computeExpected(now);
    }

    public void refresh() {
        long now = System.currentTimeMillis();
        msExpected = computeExpected(now);
        msLastRefresh = now;
        samplesSinceRefresh = 0;
    }

    public void sample() {
        samplesSinceRefresh++;
    }

    private Double computeExpected(long msNow) {
        long msElapsed = msNow - msLastRefresh;
        double result = getSensitivity() * msElapsed + (1 - getSensitivity()) * msExpected;
        return result;
    }

    public boolean isFresh() {
        if (msExpected == 0) {
            return false;
        }
        if (samplesSinceRefresh == 0) {
            return true;
        }
        long msElapsed = System.currentTimeMillis() - msLastRefresh;

        return msElapsed <= msExpected;
    }

    public double getSensitivity() {
        return sensitivity;
    }

    public long getSamplesSinceRefresh() {
        return samplesSinceRefresh;
    }
}
