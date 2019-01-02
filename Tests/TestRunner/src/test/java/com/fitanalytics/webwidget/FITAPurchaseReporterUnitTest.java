package com.fitanalytics.webwidget;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FITAPurchaseReporterUniTest {

    FITAPurchaseReporter reporter;
    private FITAPurchaseReport report;

    @Before
    public void setup(){
        reporter = new FITAPurchaseReporter();
        report = new FITAPurchaseReport("test-12345","12345", 0.01d, "EUR");

    }



    @Test
    public void testToString(){
        assertEquals("FITAReporter: https://collector.fitanalytics.com/purchases",reporter.toString());
    }

    @Test
    public void testReport(){
        assertEquals("test-12345", report.getProductSerial());
        assertEquals("12345", report.getOrderId());
        assertEquals(new Double(0.01d), report.getPrice());
        assertEquals("EUR", report.getCurrency());

    }
    @Test
    public void testProcessReport() {

        reporter.setDryRun(true);
        assertEquals(true, reporter.isDryRun());

        report.setProductSerial("");
        assertEquals(true, reporter.processReport(report).isEmpty());

        report.setProductSerial("test-12345");
        assertEquals(false, reporter.processReport(report).isEmpty());


        assertEquals("test-test-12345", reporter.processReport(report).get("productId"));

        reporter.setDryRun(false);
        assertEquals("test-12345", reporter.processReport(report).get("productId"));






    }
}
