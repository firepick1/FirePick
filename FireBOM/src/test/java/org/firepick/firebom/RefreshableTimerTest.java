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

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class RefreshableTimerTest {
    @Test
    public void testRefreshableTimer() throws InterruptedException {
        RefreshableTimer timer = new RefreshableTimer();

        // At construction, the proxy is not fresh until it is refreshed.
        assert (!timer.isFresh());
        assertEquals(0, timer.getSamplesSinceRefresh());
        assertEquals(0, timer.getExpectedRefreshMillis());

        // Sampling does not affect refresh interval
        Thread.sleep(10);
        assertEquals(8, timer.getExpectedRefreshMillis());
        timer.sample();
        assertEquals(8, timer.getExpectedRefreshMillis());
        assertEquals(1, timer.getSamplesSinceRefresh());

        // refreshing starts a new refresh interval
        Thread.sleep(10);
        assert (!timer.isFresh());
        timer.refresh();
        assert (timer.isFresh());
        assertEquals(0, timer.getSamplesSinceRefresh());
        assertEquals(16, timer.getExpectedRefreshMillis());
        timer.sample();
        assertEquals(1, timer.getSamplesSinceRefresh());
        assert (timer.isFresh());
        Thread.sleep(100);

        // second refresh updates refresh interval
        timer.refresh();
        assert (timer.isFresh());
        assertEquals(0, timer.getSamplesSinceRefresh());
        assertEquals(83d, (double) timer.getExpectedRefreshMillis(), 1);
        Thread.sleep(70);
        assert (timer.isFresh());
        Thread.sleep(70);
        // viewing makes things stale
        assert (timer.isFresh());
        timer.sample();
        assertEquals(1, timer.getSamplesSinceRefresh());
        assert (!timer.isFresh());

        // exponential average refresh intervals
        timer.refresh();
        assert (timer.isFresh());
        assertEquals(128d, (double) timer.getExpectedRefreshMillis(), 1);
        Thread.sleep(70);
        timer.sample();
        assert (timer.isFresh());
        Thread.sleep(70);
        assert (!timer.isFresh());
        assertEquals(0.8d, timer.getSensitivity(), 0);
    }

    @Test
    public void testRefreshInterval() throws InterruptedException {
        RefreshableTimer timer = new RefreshableTimer();
        Random random = new Random(99);
        long msStart = System.currentTimeMillis();
        int refreshInterval = 10;
        int sampleInterval = 30;
        long msRefresh = refreshInterval;
        long msSample = random.nextInt(sampleInterval);

        for (int i = 0; i < 300; i++) {
            System.out.print(timer.isFresh() ? "." : "?");
            System.out.print(timer.getSamplesSinceRefresh());
            System.out.print(" ");
            System.out.print(timer.getExpectedRefreshMillis());
            long msElapsed = System.currentTimeMillis() - msStart;
            if (msElapsed > msRefresh) {
                msRefresh += refreshInterval;
                if (!timer.isFresh()) {
                    timer.refresh();
                    System.out.print("=>refresh");
                }
            }
            if (msElapsed > msSample) {
                timer.sample();
                System.out.print("=>sample");
                msSample += random.nextInt(sampleInterval);
            }
            System.out.println();
            Thread.sleep(1);
        }
        // refresh interval converges to a little less than average sample interval
        assertEquals((double) sampleInterval, (double) timer.getExpectedRefreshMillis(), sampleInterval/10.0d);
    }
}
