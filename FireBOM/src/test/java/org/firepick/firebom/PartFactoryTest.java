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
import net.sf.ehcache.config.CacheConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.assertEquals;

public class PartFactoryTest {
    private static PartFactory partFactory;

    @BeforeClass
    public static void setup() {
        partFactory = PartFactory.getInstance();
    }

    @Test
    public void testInventables() throws Exception {
        new PartTester(partFactory, "https://www.inventables.com/technologies/ball-bearings")
                .testId("25196-01").testPackageCost(1.5).testPackageUnits(1).testUnitCost(1.5);
    }

    @Test
    public void testShapeways() throws Exception {
        new PartTester(partFactory, "http://shpws.me/nekC")
                .testId("DL55").testPackageCost(4.28).testPackageUnits(1).testUnitCost(4.28);
        new PartTester(partFactory, "http://www.shapeways.com/model/898050/dl55.html?li=productBox-search")
                .testId("DL55").testPackageCost(4.28).testPackageUnits(1).testUnitCost(4.28);
        new PartTester(partFactory, "http://www.shapeways.com/model/898050/dl55.html")
                .testId("DL55").testPackageCost(4.28).testPackageUnits(1).testUnitCost(4.28);
    }

    @Test
    public void testMcMasterCarr() throws Exception {
        new PartTester(partFactory, "http://www.mcmaster.com/#91290A115")
                .testId("91290A115").testPackageCost(6.39).testPackageUnits(100).testUnitCost(.0639).testProject("www.mcmaster.com");
        new PartTester(partFactory, "http://www.mcmaster.com/#57485K63")
                .testId("57485K63").testPackageCost(1.55).testPackageUnits(1).testUnitCost(1.55).testProject("www.mcmaster.com");
        new PartTester(partFactory, "http://www.mcmaster.com/#95601A295")
                .testId("95601A295").testPackageCost(2.27).testPackageUnits(100).testUnitCost(0.0227).testProject("www.mcmaster.com");
    }

    @Test
    public void testCacheExpiration() throws Exception {
        Ehcache cache = CacheManager.getInstance().getEhcache("org.firepick.firebom.Part");
        CacheConfiguration configuration = cache.getCacheConfiguration();
        long idleTime = configuration.getTimeToIdleSeconds();
        long liveTime = configuration.getTimeToLiveSeconds();
        configuration.setTimeToIdleSeconds(1);
        configuration.setTimeToLiveSeconds(1);
        URL url = new URL("http://www.mcmaster.com/#91290A115");
        Part part1 = PartFactory.getInstance().createPart(url);
        Part part2 = PartFactory.getInstance().createPart(url);
        assertEquals(part1, part2);

        Thread.sleep(2000);

        Part part3 = PartFactory.getInstance().createPart(url);
        assert(part1 != part3);
        configuration.setTimeToIdleSeconds(idleTime);
        configuration.setTimeToLiveSeconds(liveTime);
    }

    @Test
    public void testGitHub() throws Exception {
        PartTester tester = new PartTester(partFactory, "https://github.com/firepick1/FirePick/wiki/D7IH");
        tester.testId("D7IH");
        tester.testRequiredParts(5);
        tester.testUnitCost(11.6698).testPackageCost(11.6698).testPackageUnits(1);
        tester.testRequiredPart(0, "DB16", 1, 1.50)
                .testRequiredPart(1, "F525", 1, 0.11)
                .testRequiredPart(2, "F510", 1, 0.0793)
                .testRequiredPart(3, "F50N", 1, 0.0173)
                .testRequiredPart(4, "X50K", 1, 0.1932)
                .testProject("FirePick")
                .getPart()
        ;
        new PartTester(partFactory, "https://github.com/firepick1/FirePick/wiki/X523")
                .testId("X523").testPackageCost(.63).testPackageUnits(1).testUnitCost(.63).testRequiredParts(0).testProject("FirePick");
        new PartTester(partFactory, "https://github.com/firepick1/FirePick/wiki/F3WF")
                .testId("F3WF").testUnitCost(0.0227).testPackageCost(0.0227).testPackageUnits(1);
    }

    @Test
    public void testMisumi() throws Exception {
        new PartTester(partFactory, "http://us.misumi-ec.com/vona2/detail/110300437260/?KWSearch=HBLFSNF5&catalogType=00000034567")
                .testId("HBLFSNF5").testPackageCost(.63).testPackageUnits(1).testUnitCost(.63);
        new PartTester(partFactory, "http://us.misumi-ec.com/vona2/result/?Keyword=HBLFSN5")
                .testId("HBLFSN5").testPackageCost(.75).testPackageUnits(1).testUnitCost(.75);
        new PartTester(partFactory, "http://us.misumi-ec.com/vona2/detail/110302246940/?PNSearch=HNKK5-5&HissuCode=HNKK5-5")
                .testId("HNKK5-5").testPackageCost(19.32).testPackageUnits(100).testUnitCost(0.1932);
        new PartTester(partFactory, "http://us.misumi-ec.com/vona2/result/?Keyword=HFSF5-2040-379")
                .testId("HFSF5-2040-379").testPackageCost(3.79).testPackageUnits(1).testUnitCost(3.79);
    }

}
