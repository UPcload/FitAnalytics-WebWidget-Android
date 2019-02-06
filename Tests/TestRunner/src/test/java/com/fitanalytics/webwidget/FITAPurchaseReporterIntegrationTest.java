package com.fitanalytics.webwidget;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FITAPurchaseReporterIntegrationTest{

    private FITAPurchaseReporter reporter;
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
    public  void testSendReport(){
        reporter.setDryRun(true);
        report.setProductSerial(null);
        boolean result = reporter.sendReport(report);
        assertEquals("Expect to not collect an empty", false, result);

        reporter.setDryRun(false);

        report.setProductSerial("test-54321");
        result = reporter.sendReport(report);
        assertEquals( "Expect to collect a conform productSerial (including shop-prefix)", true, result);

        report.setProductSerial("test54321");
        result = reporter.sendReport(report);
        assertEquals("Expect to collect a non conform productSerial (missing shop-prefix)",true, result);

        report.setPurchasedSize("XL/test");


        report.setProductSerial("test-23232323232323");
        result = reporter.sendReport(report);
        assertEquals("Expect to collect a double prefixed productSerial", true, result);


        reporter.setDryRun(false);
        result = reporter.sendReport(reportMinimal);
        assertEquals("Expect to collect a mininmal report, with conform productSerial", true, result);

        reporter.setDryRun(false);
        result = reporter.sendReport(reportFull);
        assertEquals("Expect to collect a full report, with conform productSerial",true, result);

    }

    @Test
    public void testReporterAsyncTask(){
        assertEquals("Expect to send a single (1) reports asynchronously",reporter.doInBackground(reportMinimal), new Long(1));
        assertEquals("Expect to send multiple (3) reports asynchronously",reporter.doInBackground(report,reportMinimal,reportFull), new Long(3));
    }
}
