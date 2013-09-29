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
import net.sf.ehcache.config.CacheConfiguration;
import org.firepick.firebom.RefreshableProxyTester;
import org.firepick.firebom.bom.BOM;
import org.firepick.firebom.bom.BOMRow;
import org.firepick.firebom.exception.ProxyResolutionException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import static org.junit.Assert.assertEquals;

public class PartFactoryTest {
    private static PartFactory partFactory;

    @BeforeClass
    public static void setup() {
        partFactory = PartFactory.getInstance();
    }

    @Test
    public void testGoodPart() throws Exception {
        CacheManager.getInstance().clearAll();
        URL x50k = new URL("https://github.com/firepick1/FirePick/wiki/X50K");
        Part x50lPart = partFactory.createPart(x50k);
        new RefreshableProxyTester().testRefreshSuccess(x50lPart);
        assertEquals(null, x50lPart.getRefreshException());

        Part d7ihPart = partFactory.createPart(new URL("https://github.com/firepick1/FirePick/wiki/D7IH"));
        new RefreshableProxyTester().testRefreshSuccess(d7ihPart);
        assertEquals(null, d7ihPart.getRefreshException());
    }

    @Test
    public void testTemporarilyBadPart() throws Exception {
        URL url = new URL("https://github.com/firepick1/FirePick/wiki/D7IH");
        Part d7ihPart = new GitHubPart(PartFactory.getInstance(), url, new CachedUrlResolver());
        ProxyResolutionException dummyException = new ProxyResolutionException("dummy");

        // simulate a bad connection
        d7ihPart.setRefreshException(dummyException);
        assertEquals(dummyException, d7ihPart.getRefreshException());
        Thread.sleep(d7ihPart.getRefreshInterval());
        assert (!d7ihPart.isFresh());

        d7ihPart.refreshAll();
        assertEquals(partFactory.getMinRefreshInterval(), d7ihPart.getRefreshInterval());
        assertEquals(null, d7ihPart.getRefreshException());
        assert (d7ihPart.isResolved());
        assert (d7ihPart.isFresh());
    }

    @Test
    public void testBadPart() throws Exception {
        Part part = partFactory.createPart(new URL("https://github.com/badurl"));
        new RefreshableProxyTester().testRefreshFailure(part);
        Exception e = part.getRefreshException();
        assert (e instanceof ProxyResolutionException);
    }

    @Test
    public void testInventables() throws Exception {
        Part part = new PartTester(partFactory, "https://www.inventables.com/technologies/makerslide#sample_33124")
                .testId("25142-04").testPackageCost(32.84, 0).testPackageUnits(1)
                .testTitle("MAKERSLIDE 1800mm").getPart();
        BOM bom = new BOM(new URL("https://www.inventables.com/technologies/makerslide#sample_33124"));
        bom.resolve(0);
        BOMRow bomRow = (BOMRow) bom.iterator().next();
        assertEquals(32.84d, bomRow.getUnitCost(), .005d);
        new PartTester(partFactory, "https://www.inventables.com/technologies/ball-bearings")
                .testId("25196-01").testPackageCost(1.5, 0).testPackageUnits(1).testUnitCost(1.5);
    }

    @Test
    public void testShapeways() throws Exception {
// Strange how this doesn't work... SSL is supported
        new PartTester(partFactory, "https://github.com/firepick1/FirePick/wiki/C8S1")
                .testId("C8S1").testPackageCost(4.745, 0).testPackageUnits(1).testUnitCost(4.745).testVendor("www.shapeways.com");
        new PartTester(partFactory, "http://shpws.me/ph25")
                .testId("C8S1").testPackageCost(9.49, 0).testPackageUnits(1).testUnitCost(9.49).testVendor("www.shapeways.com");
        new PartTester(partFactory, "http://www.shapeways.com/model/1363207/c8s1.html")
                .testId("C8S1").testPackageCost(9.49, 0).testPackageUnits(1).testUnitCost(9.49).testVendor("www.shapeways.com");
        new PartTester(partFactory, "http://shpws.me/nekC")
                .testId("DL55").testPackageCost(4.28, 0).testPackageUnits(1).testUnitCost(4.28).testVendor("www.shapeways.com");
        new PartTester(partFactory, "http://www.shapeways.com/model/898050/dl55.html?li=productBox-search")
                .testId("DL55").testPackageCost(4.28, 0).testPackageUnits(1).testUnitCost(4.28).testVendor("www.shapeways.com");
        new PartTester(partFactory, "http://www.shapeways.com/model/898050/dl55.html")
                .testId("DL55").testPackageCost(4.28, 0).testPackageUnits(1).testUnitCost(4.28);
    }

    @Test
    public void testDigiKey() throws Exception {
        new PartTester(partFactory, "http://www.digikey.com/product-detail/en/PS1024ALRED/EG2025-ND/81539")
                .testId("PS1024ALRED").testPackageCost(1.35, 0.5).testPackageUnits(1).testVendor("www.digikey.com")
                .testTitle("SWITCH PUSH SPST-NO 3A 125V");
        new PartTester(partFactory, "http://www.digikey.com/product-search/en?x=3&y=16&lang=en&site=us&KeyWords=PS1024ALRED")
                .testId("PS1024ALRED").testPackageCost(1.35, 0.5).testPackageUnits(1).testVendor("www.digikey.com")
                .testTitle("SWITCH PUSH SPST-NO 3A 125V");
        new PartTester(partFactory, "http://www.digikey.com/product-detail/en/MCE4WT-A2-0000-000M01/MCE4WT-A2-0000-000M01DKR-ND/1985944")
                .testId("MCE4WT-A2-0000-000M01").testPackageCost(16.59, 0.5).testPackageUnits(1).testVendor("www.digikey.com")
                .testTitle("LED COOL WHITE 430 LUMEN SMD");
    }

    @Test
    public void testAmazon() throws Exception {
        // Amazon prices fluctuate wildly, so expect the following to fail.
        // Just update the prices and commit to GitHub

        new PartTester(partFactory, "http://www.amazon.com/Studio-Pro-4-Inch-Copper-Foil/dp/B0044SAWLQ")
                .testId("B0044SAWLQ").testPackageCost(10.98, 1).testPackageUnits(1)
                .testTitle("Studio Pro 1/4-Inch Copper Foil").getPart();
        new PartTester(partFactory, "http://www.amazon.com/Bearing-Shielded-Miniature-Bearings-VXB/dp/B002BBFC2C")
                .testId("B002BBFC2C").testPackageCost(28, 3).testPackageUnits(1).testVendor("www.amazon.com")
                .testTitle("20 Bearing 625ZZ 5x16x5 Shielded Miniature Ball Bearings VXB Brand");
        new PartTester(partFactory, "http://www.amazon.com/Maxell-Cell-Pack-Battery-723443/dp/B002PY7P4I/ref=sr_1_1?ie=UTF8&qid=1373161758&sr=8-1&keywords=aa+batteries")
                .testId("B002PY7P4I").testPackageCost(14.00, 2.50).testPackageUnits(48)
            //    .testId("B002PY7P4I").testPackageCost(12.16, 2).testPackageUnits(48)
                .testTitle("Maxell LR6 AA Cell 48 Pack Box Battery (723443)").getPart();
        new PartTester(partFactory, "http://www.amazon.com/dp/B000A0PYQK/")
                .testId("B000A0PYQK").testPackageCost(18.0, 2).testPackageUnits(1)
                .testTitle("Tetra Whisper Air Pumps (Non-UL)").getPart();
    }

    @Test
    public void testEstimateQuantity() {
        assertEquals(48, PartFactory.estimateQuantity(12.24d, 0.26));
    }

    @Test
    public void testMock() throws Exception {
        String part1Url = "http://mock?id:abc&cost:1.23&units:4&title:hello&vendor:mockVendor";
        MockPart part1 = (MockPart) new PartTester(partFactory, part1Url)
                .testId("abc").testPackageCost(1.23, 0).testPackageUnits(4).testTitle("hello").testVendor("mockVendor")
                .getPart();
        String encode1 = URLEncoder.encode(part1Url, "utf-8");
        assertEquals("http%3A%2F%2Fmock%3Fid%3Aabc%26cost%3A1.23%26units%3A4%26title%3Ahello%26vendor%3AmockVendor", encode1);
        assertEquals(part1Url, URLDecoder.decode(encode1, "utf-8"));

        // normal refresh() is ignored if too frequent
        int refreshFromRemoteCount = part1.getRefreshFromRemoteCount();
        part1.refresh();
        assertEquals(refreshFromRemoteCount, part1.getRefreshFromRemoteCount());

        // refresh() with previous error is not ignored
        part1.setRefreshException(new ProxyResolutionException("test"));
        part1.refresh();
        assertEquals(refreshFromRemoteCount + 1, part1.getRefreshFromRemoteCount());

        Part part2 = new PartTester(partFactory, "http://mock?id:def&cost:2.34&units:1&title:there")
                .testId("def").testPackageCost(2.34, 0).testPackageUnits(1).testTitle("there").testVendor("mock").getPart();
        String encode2 = URLEncoder.encode("http://mock?id:def&cost:2.34&units:1&title:there", "utf-8");

        Part part3 = new PartTester(partFactory, "http://mock?id:ghi&cost:4.56")
                .testId("ghi").testPackageCost(4.56, 0).testPackageUnits(1).testTitle("ghi").getPart();
        String encode3 = URLEncoder.encode("http://mock?id:ghi&cost:4.56", "utf-8");

        Part partS1 = new PartTester(partFactory, "http://mock?id:abc-source&source:" + encode1)
                .testId("abc-source").testPackageCost(1.23 / 4, 0).testPackageUnits(1).testTitle("hello").testVendor("mockVendor").getPart();
        assertEquals(part1, partS1.getSourcePart());

        String urlZZZ0 = "https://github.com/firepick1/FirePick/wiki/ZZZ0";
        String encodeZZZ0 = URLEncoder.encode(urlZZZ0, "utf-8");
        Part partZZZ0 = new PartTester(partFactory, urlZZZ0)
                .testId("ZZZ0").testPackageUnits(1).testPackageCost(0, 0).testTitle("Nonexistent part").getPart();
//        Part partSZZZ0 = new PartTester(partFactory, "http://mock?id:zzz0-source&source:" + encodeZZZ0)
//                .testId("zzz0-source").testPackageCost(0, 0).testPackageUnits(1).testTitle("Nonexistent part").testVendor("github.com").getPart();

        Part partR1R2 = new PartTester(partFactory, "http://mock?id:r1r2&require:" + encode1 + ":2&require:" + encode2)
                .testId("r1r2")
                .testRequiredParts(2)
                .testRequiredPart(0, "abc", 2, 1.23 / 4d)
                .testRequiredPart(1, "def", 1, 2.34)
                .testPackageCost(2.955, 0).getPart();
        assertEquals(null, partR1R2.getSourcePart());

        Part partS3R1R2 = new PartTester(partFactory, "http://mock?id:s3r1r2&source:" + encode3 + "&require:" + encode1 + ":2&require:" + encode2)
                .testId("s3r1r2")
                .testSourceCost(4.56)
                .testRequiredParts(2)
                .testRequiredPart(0, "abc", 2, 1.23 / 4d)
                .testRequiredPart(1, "def", 1, 2.34)
                .testPackageCost(7.515, 0).getPart();
        assertEquals(part3, partS3R1R2.getSourcePart());

    }

    @Test
    public void testPonoko() throws Exception {
        new PartTester(partFactory, "https://github.com/firepick1/FirePick/wiki/A3B1")
                .testId("A3B1").testPackageCost(2.2524, .5d).testPackageUnits(1).testSourceCost(1)
                .testRequiredParts(4).testProject("FirePick").testVendor("www.ponoko.com");
        new PartTester(partFactory, "http://www.ponoko.com/design-your-own/products/a3b1-10268")
                .testId("a3b1-10268").testPackageCost(12.00, 0).testPackageUnits(1).testUnitCost(12).testUnitCost(12)
                .testVendor("www.ponoko.com");
    }

    @Test
    public void testMcMasterCarr() throws Exception {
        new PartTester(partFactory, "http://www.mcmaster.com/#5544t222/=nrwpi4#2")
                .testId("5544T222").testPackageCost(4.28, 0).testPackageUnits(1).testVendor("www.mcmaster.com");
        new PartTester(partFactory, "http://www.mcmaster.com/#5544t222")
                .testId("5544T222").testPackageCost(2.21, 0).testPackageUnits(1);
        new PartTester(partFactory, "http://www.mcmaster.com/#91290A115")
                .testId("91290A115").testPackageCost(6.39, 0).testPackageUnits(100).testUnitCost(.0639).testProject("www.mcmaster.com");
        new PartTester(partFactory, "http://www.mcmaster.com/#5544t222/=nrwpi4")
                .testId("5544T222").testPackageCost(2.21, 0).testPackageUnits(1);
        new PartTester(partFactory, "http://www.mcmaster.com/#57485K63")
                .testId("57485K63").testPackageCost(1.55, 0).testPackageUnits(1).testUnitCost(1.55).testProject("www.mcmaster.com");
        new PartTester(partFactory, "http://www.mcmaster.com/#95601A295")
                .testId("95601A295").testPackageCost(2.27, 0).testPackageUnits(100).testUnitCost(0.0227).testProject("www.mcmaster.com");
    }

    @Test
    public void testCacheExpiration() throws Exception {
        Ehcache cache = CacheManager.getInstance().getEhcache("org.firepick.firebom.part.Part");
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
        assert (part1 != part3);
        configuration.setTimeToIdleSeconds(idleTime);
        configuration.setTimeToLiveSeconds(liveTime);
    }

    @Test
    public void testSparkfun() throws Exception {
        PartTester tester = new PartTester(partFactory, "https://www.sparkfun.com/products/11868");
        tester.testId("11868").testPackageUnits(1).testPackageCost(29.95, .5).testVendor("www.sparkfun.com");
    }

    @Test
    public void testAdafruit() throws Exception {
        PartTester tester = new PartTester(partFactory, "http://www.adafruit.com/products/1367")
                .testVendor("www.adafruit.com").testId("1367").testPackageUnits(1).testPackageCost(29.95, .5);
    }

    @Test
    public void testGitHub() throws Exception {
        new PartTester(partFactory, "https://github.com/firepick1/FirePick/wiki/DX17")
                .testId("DX17");
        new PartTester(partFactory, "https://github.com/firepick1/FirePick/wiki/FIREPICK.1")
                .testId("FIREPICK.1");
        new PartTester(partFactory, "https://github.com/firepick1/FirePick/wiki/ET06")
                .testId("ET06").testPackageCost(0.003, .001d).testSourcePackageUnits(1d/3292).testProject("FirePick");
        PartTester tester = new PartTester(partFactory, "https://github.com/firepick1/FirePick/wiki/D7IH");
        tester.testId("D7IH");
        tester.testVendor("www.shapeways.com");
        tester.testRequiredParts(5);
        tester.testRequiredPart(0, "DB16", 1, 1.2475)
                .testRequiredPart(1, "F525", 1, 0.11)
                .testRequiredPart(2, "F510", 1, 0.0793)
                .testRequiredPart(3, "F50N", 2, 0.0173)
                .testRequiredPart(4, "X50K", 1, 0.1932)
                .testProject("FirePick")
                .getPart();
        tester.testPackageCost(11.42, .5).testPackageUnits(1);
        new PartTester(partFactory, "https://github.com/firepick1/FirePick/wiki/X523")
                .testId("X523").testPackageCost(1.175, 0).testPackageUnits(1).testUnitCost(1.175).testRequiredParts(2).testProject("FirePick");
        new PartTester(partFactory, "https://github.com/firepick1/FirePick/wiki/F3WF")
                .testId("F3WF").testUnitCost(0.0227).testPackageCost(0.0227, 0).testPackageUnits(1);
    }

    @Test
    public void testMisumi() throws Exception {
        new PartTester(partFactory, "http://us.misumi-ec.com/vona2/detail/110300437260/?KWSearch=HBLFSNF5&catalogType=00000034567")
                .testId("HBLFSNF5").testPackageCost(.63, 0).testPackageUnits(1).testUnitCost(.63);
        new PartTester(partFactory, "http://us.misumi-ec.com/vona2/result/?Keyword=HBLFSN5")
                .testId("HBLFSN5").testPackageCost(.75, 0).testPackageUnits(1).testUnitCost(.75);
        new PartTester(partFactory, "http://us.misumi-ec.com/vona2/detail/110302246940/?PNSearch=HNKK5-5&HissuCode=HNKK5-5")
                .testId("HNKK5-5").testPackageCost(19.32, 0).testPackageUnits(100).testUnitCost(0.1932);
        new PartTester(partFactory, "http://us.misumi-ec.com/vona2/result/?Keyword=HFSF5-2040-379")
                .testId("HFSF5-2040-379").testPackageCost(3.79, 0).testPackageUnits(1).testUnitCost(3.79);
    }

    @Test
    public void testSynthetos() throws Exception {
        new PartTester(partFactory, "https://synthetos.myshopify.com/products/tinyg")
                .testId("tinyg").testPackageCost(129.99, 2).testPackageUnits(1).testVendor("www.synthetos.com");
    }
    @Test
    public void testTrinityLabs() throws Exception {
        new PartTester(partFactory, "http://trinitylabs.com/products/gt2-pulley-20-tooth")
                .testId("254155135").testPackageCost(7.50, .5).testPackageUnits(1).testVendor("trinitylabs.com");
    }

    @Test
    public void testUnsupportedVendor() throws Exception {
        new PartTester(partFactory, "http://google.com")
                .testId("UNSUPPORTED").testPackageCost(0,0).testPackageUnits(1).testVendor("google.com")
                .testTitle("Unsupported FireBOM vendor http://bit.ly/16jPAOr");
    }

}
