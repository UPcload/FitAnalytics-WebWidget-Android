package com.fitanalytics.webwidget;


import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FITAPurchaseReporterUnitTest {

    FITAPurchaseReporter reporter;
    private FITAPurchaseReport report;
    private FITAPurchaseReport reportMinimal;
    private FITAPurchaseReport reportFull;


    @Before
    public void setup(){
        reporter = new FITAPurchaseReporter();

        report = new FITAPurchaseReport("test-12345","12345", "XXL-oversize", 0.01d, "EUR");

        reportMinimal = new FITAPurchaseReport("12345-minimal","1234minimal");

        reportFull = new FITAPurchaseReport("12345full","1234minimal");
        reportFull.setPurchasedSize("XL");
        reportFull.setEan("1234567890123");
        reportFull.setFunnel("ab-funnel");
        reportFull.setShop("anothershop");
        reportFull.setShopArticleCode("1222-shoparticlecode");
        reportFull.setShopCountry("DE");
        reportFull.setShopLanguage("de");
        reportFull.setShopSizingSystem("shopSizingSystem-region-europe");
        reportFull.setSid("shop-sessionid-1234");
        reportFull.setSizeRegion("sizeRegion");

        reporter.setTestEnv(true);

    }




    @Test
    public void testToString(){
        assertEquals("com.fitanalytics.webwidget.FITAPurchaseReporter Object {\n" +
                "  FITA_WIDGET: fitaWidget\n" +
                "  REPORT_URL1: https://collector.fitanalytics.com/purchases\n" +
                "  REPORT_URL2: https://collector-de.fitanalytics.com/purchases\n" +
                "  USER_AGENT: Mozilla/5.0 (Linux; U; Android 2.2) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1-FitAnalytics v0.0.1\n" +
                "  isDryRun: false\n" +
                "  isTestEnv: true\n" +
                "}",reporter.toString());
    }


    @Test
    public void testGetCookie(){
        String connect_sid = reporter.getCookieValue("__cfduid=d548b30461fe3b3c3cc709ea30a02c98d1546606472; connect.sid=s%3AqFuFsWjiRozo_BUnOPpJgPmJ3pSOr3OH.DSjQsAcIpNl2kjc4M9n4BVHOJTiV8sg0CuVqMwGy85s").get("connect.sid");
        assertEquals("s%3AqFuFsWjiRozo_BUnOPpJgPmJ3pSOr3OH.DSjQsAcIpNl2kjc4M9n4BVHOJTiV8sg0CuVqMwGy85s",connect_sid);
    }
    @Test
    public void testReport(){
        assertEquals("test-12345", report.getProductSerial());
        assertEquals("12345", report.getOrderId());
        assertEquals(new Double(0.01d), report.getPrice());
        assertEquals("EUR", report.getCurrency());

        assertEquals("12345-minimal", reportMinimal.getProductSerial());
        assertEquals("1234minimal", reportMinimal.getOrderId());
        assertNull(reportMinimal.getPrice());
        assertNull(reportMinimal.getCurrency());


    }


    @Test
    public void testProcessReport() {

        reporter.setDryRun(true);
        assertTrue(reporter.isDryRun());

        report.setProductSerial("");
        assertTrue(reporter.processReport(report).isEmpty());

        report.setProductSerial("test-12345");
        assertFalse(reporter.processReport(report).isEmpty());
        // we are in dry-run
        assertEquals("test-test-12345", reporter.processReport(report).get("productId"));

        reporter.setDryRun(false);
        assertEquals("test-12345", reporter.processReport(report).get("productId"));
        assertEquals("0.01", reporter.processReport(report).get(FITAPurchaseReporter.KEYS.PRICE));
        assertEquals("EUR", reporter.processReport(report).get(FITAPurchaseReporter.KEYS.CURRENCY));
        assertEquals("12345", reporter.processReport(report).get(FITAPurchaseReporter.KEYS.ORDER_ID));
        assertEquals("XXL-oversize", reporter.processReport(report).get(FITAPurchaseReporter.KEYS.PURCHASED_SIZE));


        long reporterepocheTimeInMs = Long.parseLong((String)reporter.processReport(report).get(FITAPurchaseReporter.KEYS.TIME_STAMP));
        long epocheTimeInMS = new Date().getTime();
        assertEquals(true, epocheTimeInMS>=reporterepocheTimeInMs);
        assertEquals(FITAPurchaseReporter.KEYS.EMBED_ANDROID, reporter.processReport(report).get(FITAPurchaseReporter.KEYS.HOSTNAME));


    }

}
