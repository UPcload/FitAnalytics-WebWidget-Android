package com.fitanalytics.webwidget;

import android.webkit.CookieManager;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.net.ssl.HttpsURLConnection;


public class FITAPurchaseReporter {


    private final static String REPORT_URL1 = "https://collector.fitanalytics.com/purchases";
    private final static String REPORT_URL2 = "https://collector-de.fitanalytics.com/purchases";
    private static final String USER_AGENT = "Mozilla/5.0 (Linux; U; Android 2.2) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1-testRunner";
    private CookieManager cookieManager ;

    public boolean isDryRun() {
        return isDryRun;
    }

    public void setDryRun(boolean dryRun) {
        isDryRun = dryRun;
    }

    private boolean isDryRun = false;

    public FITAPurchaseReporter() {
        // TODO cookieManager & access to cokkies from webview
    }


    /**
     * sends the report to the purchase collector REPORT_URL1 and REPORT_URL2
     * @param report for each purchase
     * @return true if successful false otherwise
     */

    public boolean sendReport(FITAPurchaseReport report){
        Dictionary reportAs = processReport (report);
        boolean result = false;

        if (reportAs.size()==0){
            return result;
        }
        result = sendRequest(REPORT_URL1, reportAs);
        boolean result2=  sendRequest(REPORT_URL2, reportAs);
        return (result && result2);
    }



    private boolean sendRequest(String uri, Dictionary reportAsDic ) {
        URL url = null;
        boolean isSuccess = false;
        try {
            url = new URL(uri);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            try {
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                // TODO get a correct agent String for an android client without using the webviewcontext
                // using:  WebSettings.getDefaultUserAgent(context);
                // Cloudflare rejects USer-Agent "Java/xxx"
                urlConnection.setRequestProperty("User-Agent", USER_AGENT);

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                String query = getQuery((Hashtable<String, String>) reportAsDic);
                writer.write(query);
                writer.flush();
                writer.close();

                os.close();
                urlConnection.connect();
            } finally {
                int responseCode = urlConnection.getResponseCode();
                urlConnection.disconnect();
                return (responseCode == 200);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }



    private String getQuery(Hashtable<String,String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for ( String key : params.keySet())
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(params.get(key), "UTF-8"));
        }

        return result.toString();
    }

    /**
     * TODO : make this JAVA_8  compatible
     */
    Dictionary<String,String> processReport (FITAPurchaseReport report) {
        Hashtable<String, String> dict = new Hashtable<String, String>();
        String productSerial = report.getProductSerial();

        if (productSerial != null && !productSerial.equals("")) {

            // prepend the product serial by "test-" in order to avoid skewing metrics by dry runs
            if (isDryRun) {
                productSerial = "test-" + productSerial;

            }
            // productSerial is sent as "productId"
            dict.put(KEYS.PRODUCT_ID,productSerial);

            putIfNotNull(KEYS.ORDER_ID,report.getOrderId(), dict);

            // map shoparticlecode to variantId
            putIfNotNull(KEYS.VARIANT_ID,report.getShopArticleCode(), dict);
            putIfNotNull(KEYS.USER_ID,report.getUserId(), dict);
            putIfNotNull(KEYS.SHOP,report.getShop(), dict);
            putIfNotNull(KEYS.PURCHASED_SIZE,report.getPurchasedSize(), dict);
            putIfNotNull(KEYS.CURRENCY,report.getCurrency(), dict);
            putIfNotNull(KEYS.EAN,report.getEan(), dict);
            putIfNotNull(KEYS.FUNNEL,report.getFunnel(), dict);
            putIfNotNull(KEYS.HOSTNAME,report.getHostname(), dict);

            putIfNotNull(KEYS.PRICE,report.getPrice().toString(), dict);

            putIfNotNull(KEYS.SHOP_COUNTRY,report.getShopCountry(), dict);
            putIfNotNull(KEYS.SHOP_LANGUAGE,report.getShopLanguage(), dict);
            putIfNotNull(KEYS.SHOP_SIZING_SYSTEM,report.getShopSizingSystem(), dict);


           //TODO sessionid
            putIfNotNull(KEYS.SID,report.getSid(), dict);

            putIfNotNull(KEYS.SIZE_REGION,report.getSizeRegion(), dict);

            dict.put(KEYS.SENDER,"callback");
            dict.put(KEYS.HOSTNAME, KEYS.EMBED_ANDROID);
            long epocheTimeInMS = new Date().getTime();
            dict.put(KEYS.TIMESTAMP, ""+epocheTimeInMS);
            // TODO JAVA 8+
            //long unixTimestamp = Instant.now().getEpochSecond();

        }

        /**

         *     NSString *sid = nil;
         *     if (report.sid != nil)
         *         sid = report.sid;
         *     else
         *         sid = [_defaults objectForKey:kSidKey];
         *
         *     if (sid != nil && [sid isKindOfClass:[NSString class]])
         *         [dict setValue:sid forKey:@"sid"];
         *
         */
        return dict;
    }

    private void putIfNotNull(String key, String value, Hashtable<String, String> dict) {
        if (value!=null){
            dict.put(key,value);
        }
    }


    @Override
    public String toString(){
        return "FITAReporter: REPORT_URL="+REPORT_URL1 +" REPORT_URL2=" + REPORT_URL2 ;
    }


    class KEYS {
        static final String PRODUCT_ID = "productId";
        static final String VARIANT_ID = "variantId";
        static final String HOSTNAME = "hostname";
        static final String ORDER_ID = "orderId";
        static final String USER_ID = "userId";
        static final String PURCHASED_SIZE = "purchasesSize";
        static final String SHOP = "shop";
        static final String CURRENCY = "currency";
        static final String EAN = "ean";
        static final String FUNNEL = "funnel";
        static final String PRICE = "price";
        static final String SHOP_COUNTRY = "shopCountry";
        static final String SHOP_LANGUAGE = "shopLanguage";
        static final String SHOP_SIZING_SYSTEM = "shopSizingSystem";
        static final String SID = "sid";
        static final String SIZE_REGION = "sizeRegion";
        static final String SENDER = "sender";
        static final String TIMESTAMP = "timestamp";
        static final String EMBED_ANDROID = "embed-android";
    }
}
