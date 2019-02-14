package com.fitanalytics.webwidget;


import org.junit.Before;
import org.junit.Test;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FITAPurchaseReporterUnitTest {

    private FITAPurchaseReporter reporter;
    private FITAPurchaseReport report;
    private FITAPurchaseReport reportMinimal;


    @Before
    public void setup(){
        reporter = new FITAPurchaseReporter();

        report = new FITAPurchaseReport("test-12345","12345", "XXL-oversize", 0.01d, "EUR");

        reportMinimal = new FITAPurchaseReport("12345-minimal","1234minimal");



        reporter.setTestEnv(true);

    }




    @Test
    public void testToString(){
        assertEquals("com.fitanalytics.webwidget.FITAPurchaseReporter Object {\n" +
                "  FITA_WIDGET: fitaWidget\n" +
                "  REPORT_URL1: https://collector.fitanalytics.com/purchases\n" +
                "  REPORT_URL2: https://collector-de.fitanalytics.com/purchases\n" +
                "  ENCODED_SESSION_PREFIX: s%3A\n"+
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
    public void testParseCookie(){

        String cookie_value = "s%3AqFuFsWjiRozo_BUnOPpJgPmJ3pSOr3OH.DSjQsAcIpNl2kjc4M9n4BVHOJTiV8sg0CuVqMwGy85s";
        String expected = "qFuFsWjiRozo_BUnOPpJgPmJ3pSOr3OH";
        String actual = reporter.parseSignedCookie(cookie_value);
        assertEquals(expected,actual);

        cookie_value = "s%3AqFuFsWjiRozo_.gPmJ3pSOr3OHDSjQsAcIpNl2kjc4M....lkalksjdsdas.qasdasd";
        expected = "qFuFsWjiRozo_";
        actual = reporter.parseSignedCookie(cookie_value);
        assertEquals(expected,actual);

        cookie_value = "s%3AqFuFsWjiRozo_BUnOPpJgPmJ3pSOr3OHDSjQsAcIpNl2kjc4M9n4BVHOJTiV8sg0CuVqMwGy85s";
        expected = "s%3AqFuFsWjiRozo_BUnOPpJgPmJ3pSOr3OHDSjQsAcIpNl2kjc4M9n4BVHOJTiV8sg0CuVqMwGy85s";
        actual = reporter.parseSignedCookie(cookie_value);
        assertEquals(expected,actual);

        cookie_value = null;
        expected = null;
        actual = reporter.parseSignedCookie(cookie_value);
        assertEquals(expected,actual);

        cookie_value = "";
        expected = "";
        actual = reporter.parseSignedCookie(cookie_value);
        assertEquals(expected,actual);



        cookie_value ="s%3AdY3jXbQ-_AVO5MGmCwU2jHX-lTlAdGkW.jIJn9ERoOo3KTaSnKv3E3lZKCBqyMLOvcbZbaaQaGOw";
        expected = "dY3jXbQ-_AVO5MGmCwU2jHX-lTlAdGkW";
        actual = reporter.parseSignedCookie(cookie_value);
        assertEquals(expected,actual);
    }


    @Test
    public void getCookieValueForShop() {
        String cookie_header = "__cfduid=d548b30461fe3b3c3cc709ea30a02c98d1546606472; connect.sid=s%3AqFuFsWjiRozo_BUnOPpJgPmJ3pSOr3OH.DSjQsAcIpNl2kjc4M9n4BVHOJTiV8sg0CuVqMwGy85s";
        String expected = "qFuFsWjiRozo_BUnOPpJgPmJ3pSOr3OH";
        String actual = reporter.getCookieValueForShop(cookie_header, "test");
        assertEquals(expected,actual);

        cookie_header = "__cfduid=d548b30461fe3b3c3cc709ea30a02c98d1546606472; connect.sid.testshop=s%3AqFuFsWjiRozo_BUnOPpJgPmJ3pSOr3OH.DSjQsAcIpNl2kjc4M9n4BVHOJTiV8sg0CuVqMwGy85s";
        expected = "qFuFsWjiRozo_BUnOPpJgPmJ3pSOr3OH";
        actual = reporter.getCookieValueForShop(cookie_header, "testshop");
        assertEquals(expected,actual);

        cookie_header = "";
        expected = "";
        actual = reporter.getCookieValueForShop(cookie_header, "testshop");
        assertEquals(expected,actual);



    }


    @Test
    public void testgetShopFromReport(){
        FITAPurchaseReport report = new FITAPurchaseReport("testshop-123","2334");
        String shop = reporter.getShopFromReport(report);
        assertEquals("testshop", shop);

        report.setShop("anothershop");
        shop = reporter.getShopFromReport(report);
        assertEquals("anothershop", shop);

        report.setShop(null);
        report.setProductSerial(null);
        shop = reporter.getShopFromReport(report);
        assertEquals("", shop);



    }

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testSessionHandlingCookieNull() {
        String cookieHeader =null;
        String shop_prefix = "zara";
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Cookie not present");
        String cookieForShop = reporter.getCookieValueForShop(cookieHeader, shop_prefix);
    }


    @Test
    public void testSessionHandling() {
        String cookieHeader ="__cfduid=d548b30461fe3b3c3cc709ea30a02c98d1546606472; connect.sid.zara=s%3AqFuFsWjiRozo_BUnOPpJgPmJ3pSOr3OH.DSjQsAcIpNl2kjc4M9n4BVHOJTiV8sg0CuVqMwGy85s";
        String shop_prefix = "zara";
        String cookieForShop = reporter.getCookieValueForShop(cookieHeader, shop_prefix);
        String signed_cookie = "s%3AqFuFsWjiRozo_BUnOPpJgPmJ3pSOr3OH.DSjQsAcIpNl2kjc4M9n4BVHOJTiV8sg0CuVqMwGy85s";
        assertEquals(signed_cookie, cookieForShop);

        cookieHeader ="__cfduid=d548b30461fe3b3c3cc709ea30a02c98d1546606472; connect.sid=s%3AqFuFsWjiRozo_BUnOPpJgPmJ3pSOr3OH.DSjQsAcIpNl2kjc4M9n4BVHOJTiV8sg0CuVqMwGy85s";
        shop_prefix = "zara";
        cookieForShop = reporter.getCookieValueForShop(cookieHeader, shop_prefix);
        signed_cookie = "s%3AqFuFsWjiRozo_BUnOPpJgPmJ3pSOr3OH.DSjQsAcIpNl2kjc4M9n4BVHOJTiV8sg0CuVqMwGy85s";
        assertEquals(signed_cookie, cookieForShop);

        cookieHeader ="__cfduid=d548b30461fe3b3c3cc709ea30a02c98d1546606472; connect.sid.othershop=s%3AqFuFsWjiRozo_BUnOPpJgPmJ3pSOr3OH.DSjQsAcIpNl2kjc4M9n4BVHOJTiV8sg0CuVqMwGy85s";
        shop_prefix = "zara";
        cookieForShop = reporter.getCookieValueForShop(cookieHeader, shop_prefix);
        signed_cookie = "";
        assertEquals(signed_cookie, cookieForShop);


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
