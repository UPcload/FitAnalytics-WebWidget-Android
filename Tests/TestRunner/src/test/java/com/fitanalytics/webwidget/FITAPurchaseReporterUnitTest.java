package com.fitanalytics.webwidget;


import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class FITAPurchaseReporterUnitTest {

    FITAPurchaseReporter reporter;
    private FITAPurchaseReport report;

    @Before
    public void setup(){
        reporter = new FITAPurchaseReporter();
        report = new FITAPurchaseReport("test-12345","12345", 0.001d, "EUR");

    }



    @Test
    public void testToString(){
        assertEquals("FITAReporter: REPORT_URL=https://collector.fitanalytics.com/purchases"+
                " REPORT_URL2=https://collector-de.fitanalytics.com/purchases",reporter.toString());
    }

    @Test
    public void testReport(){
        assertEquals("test-12345", report.getProductSerial());
        assertEquals("12345", report.getOrderId());
        assertEquals(new Double(0.001d), report.getPrice());
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
        assertEquals("0.001", reporter.processReport(report).get(FITAPurchaseReporter.KEYS.PRICE));
        assertEquals("EUR", reporter.processReport(report).get(FITAPurchaseReporter.KEYS.CURRENCY));
        assertEquals("12345", reporter.processReport(report).get(FITAPurchaseReporter.KEYS.ORDER_ID));
        long reporterepocheTimeInMs = Long.parseLong((String)reporter.processReport(report).get(FITAPurchaseReporter.KEYS.TIMESTAMP));
        long epocheTimeInMS = new Date().getTime();
        assertEquals(true, epocheTimeInMS>=reporterepocheTimeInMs);
        assertEquals(FITAPurchaseReporter.KEYS.EMBED_ANDROID, reporter.processReport(report).get(FITAPurchaseReporter.KEYS.HOSTNAME));
    }
    @Test
    public  void testSendReport(){
        reporter.setDryRun(true);
        report.setProductSerial(null);
        boolean result = reporter.sendReport(report);
        assertEquals(false, result);

        report.setProductSerial("test-54321");
        result = reporter.sendReport(report);
        assertEquals(true, result);

        report.setProductSerial("test54321");
        result = reporter.sendReport(report);
        assertEquals(true, result);


    }
}
