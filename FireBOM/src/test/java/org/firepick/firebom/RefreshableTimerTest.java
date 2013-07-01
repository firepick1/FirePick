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

import org.firepick.firebom.exception.ProxyResolutionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RefreshableTimerTest {

    @Test
    public void testRefreshableTimer() throws InterruptedException {
        new RefreshableProxyTester().testRefreshSuccess(new RefreshableTimer());
        new RefreshableProxyTester().testRefreshFailure(new MockTimer());

        RefreshableTimer timer = new RefreshableTimer();

        // At construction, the proxy is not fresh and not resolved
        assertEquals(0, timer.getRefreshInterval());
        assertEquals(0, timer.getSamplesSinceRefresh());
        assertEquals(0, timer.getAge());
        assertEquals(0, timer.getRefreshInterval());

        // default sensitivity has a half-life of about 6 refreshes
        assertEquals(0.8d, timer.getSensitivity(), 0);

        // Sampling is counted and affects sampling/refresh intervals
        Thread.sleep(10);
        timer.sample();
        assertEquals(1, timer.getSamplesSinceRefresh());
        assertEquals(10, timer.getSampleInterval());
        assertEquals(10, timer.getRefreshInterval());

        // Refresh does not affect sampling interval
        timer.refresh();
        assertEquals(0, timer.getSamplesSinceRefresh());
        assertEquals(10, timer.getRefreshInterval());
        assertEquals(10, timer.getSampleInterval());

        // Sampling affects refresh/sampling intervals and, therefore, freshness
        Thread.sleep(50);
        timer.sample();
        assertEquals(1, timer.getSamplesSinceRefresh());
        assertEquals(42, timer.getRefreshInterval());
    }

    @Test
    public void testMinRefreshInterval() {
        RefreshableTimer timer = new RefreshableTimer();

        assertEquals(0, timer.getMinRefreshInterval());
        timer.setMinRefreshInterval(100);
        assertEquals(100, timer.getMinRefreshInterval());
        assertEquals(100, timer.getRefreshInterval());

        timer.refresh();
        assert (timer.isFresh());
        assertEquals(100, timer.getMinRefreshInterval());
        assertEquals(100, timer.getRefreshInterval());
    }

    public class MockTimer extends RefreshableTimer {
        @Override
        public void refresh() {
            super.refresh();
            throw new ProxyResolutionException("test");
        }

        @Override
        public long getMinRefreshInterval() {
            return 10000;
        }
    }


}
