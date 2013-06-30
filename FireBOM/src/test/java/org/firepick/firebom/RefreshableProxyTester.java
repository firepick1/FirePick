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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RefreshableProxyTester {
    private long ageIncrement = 100;

    public long getAgeIncrement() {
        return ageIncrement;
    }

    public RefreshableProxyTester setAgeIncrement(long ageIncrement) {
        this.ageIncrement = ageIncrement;
        return this;
    }

    public RefreshableProxyTester testRefreshSuccess(IRefreshableProxy proxy) {
        testInitialProxyState(proxy);

        // Sampling has no effect on freshness
        proxy.sample();
        testInitialProxyState(proxy);

        try {
            proxy.refresh();
        }
        catch (Exception e) {
            fail(e.getMessage());
        }

        assert (proxy.isFresh());
        assert (proxy.isResolved());
        // Sampling has no effect on freshness
        proxy.sample();
        assert (proxy.isFresh());
        assert (proxy.isResolved());
        testProxyAge(proxy);

        return this;
    }

    public RefreshableProxyTester testRefreshFailure(IRefreshableProxy proxy) {
        // Initial proxy state
        testInitialProxyState(proxy);

        // Sampling has no effect on freshness
        proxy.sample();
        testInitialProxyState(proxy);

        ProxyResolutionException proxyResolutionException = null;
        try {
            proxy.refresh();
            fail("Expected refresh failure");
        }
        catch (Exception e) {
            assert (e instanceof ProxyResolutionException);
        }

        assert (!proxy.isFresh());
        assert (!proxy.isResolved());

        // Sampling has no effect on freshness
        proxy.sample();
        assert (!proxy.isFresh());
        assert (!proxy.isResolved());

        // proxy ages
        testProxyAge(proxy);
        return this;
    }

    private void testInitialProxyState(IRefreshableProxy proxy) {
        // Initial proxy state
        assert (!proxy.isFresh());
        assert (!proxy.isResolved());
    }

    private void testProxyAge(IRefreshableProxy proxy) {
        // proxy ages
        long ageBefore = proxy.getAge();
        try {
            Thread.sleep(getAgeIncrement());
        }
        catch (InterruptedException e) {
            fail(e.getMessage());
        }

        double expectedAgeChange =  (double) ageIncrement;
        double actualAgeChange = (double) proxy.getAge() - ageBefore;
        assertEquals(expectedAgeChange, actualAgeChange, ageIncrement/5);
    }


}
