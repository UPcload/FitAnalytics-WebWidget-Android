package com.fitanalytics.webwidget;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebSettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class FITAPurchaseReporter extends AsyncTask<FITAPurchaseReport, Integer, Long> {


    public static final String FITA_WIDGET = "fitaWidget";
    private final static String REPORT_URL1 = "https://collector.fitanalytics.com/purchases";
    private final static String REPORT_URL2 = "https://collector-de.fitanalytics.com/purchases";
    /***
     * Some CDN block USER_AGENT based on java, so we need to create our own.
     */
    public static String USER_AGENT = "Mozilla/5.0 (Linux; U; Android 2.2) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1-FitAnalytics v0.0.1";
    private boolean isDryRun = false;
    private boolean isTestEnv = false;


    public FITAPurchaseReporter() {
    }

    public FITAPurchaseReporter(Context context) {
        try {
            USER_AGENT = WebSettings.getDefaultUserAgent(context);
        } catch (Exception e) {
            Log.d(FITA_WIDGET, "Could not set the USER AGENT from the context provided", e);
        }
    }

    @Override
    protected Long doInBackground(FITAPurchaseReport... fitaPurchaseReports) {
        int count = fitaPurchaseReports.length;
        long totalSize = 0;

        for (int i = 0; i < count; i++) {
            totalSize += (sendReport(fitaPurchaseReports[i])?1:0);
            publishProgress((int) ((i / (float) count) * 100));

            if (isCancelled()) break;
        }
        return totalSize;
    }


    public boolean isDryRun() {
        return isDryRun;
    }

    public void setDryRun(boolean dryRun) {
        isDryRun = dryRun;
    }

    public void setTestEnv(boolean testEnv) {
        isTestEnv = testEnv;
    }


    /**
     * sends the report to the purchase collector REPORT_URL1 and REPORT_URL2
     * @param report for each purchase
     * @return true if successful false otherwise
     */

    boolean sendReport(FITAPurchaseReport report){
        Map<String, String> reportAs = processReport (report);
        boolean result = false;

        if (reportAs.size()==0){
            return result;
        }
        if (!isTestEnv) {
            result = sendRequest(REPORT_URL1, reportAs);
            sendRequest(REPORT_URL2, reportAs);
        } else {
            result = sendRequest(REPORT_URL2, reportAs);
        }

        return (result);
    }

    /**
     * fires a report to the collector endpoint
     * silently accepts
     * @param uri
     * @param reportAsDictionary
     * @return
     */
    private boolean sendRequest(String uri, Map<String, String> reportAsDictionary) {
        URL url = null;
        int responseCode = 0;

        boolean isSuccess = false;
        try {
            String query = getQuery(reportAsDictionary);
            uri += "?" + query;
            url = new URL(uri);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            try {
                urlConnection.setRequestMethod("GET");
                // Cloudflare rejects User-Agent "Java/xxx"
                urlConnection.setRequestProperty("User-Agent", USER_AGENT);
                responseCode = urlConnection.getResponseCode();
                if (isTestEnv) {
                    boolean isPurchaseRecorded = getResponse(urlConnection);
                    if (isPurchaseRecorded)
                    {
                        Log.d(FITA_WIDGET, "Purchase is successfully sent , productSerial is " +reportAsDictionary.get("productSerial"));
                    }
                }

            } finally {
                urlConnection.disconnect();
                isSuccess = (responseCode == 200);
            }
        } catch (MalformedURLException e) {
            Log.e(FITA_WIDGET, "Mailformed collector url");
        } catch (IOException e) {
            Log.d(FITA_WIDGET, "Could not send the report with productSerial: " +reportAsDictionary.get("productSerial"));
        }
        return isSuccess;
    }


    /**
     * The collector responds with a "purchase was collected" if the purchase was succesfully recorded
     */
    private boolean getResponse(HttpsURLConnection urlConnection) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(urlConnection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return (response.toString().equals("purchase was collected"));
    }


    /**
     * helper method to create a query
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    private String getQuery(Map<String,String> params) throws UnsupportedEncodingException {
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
     *
     * TODO : make this JAVA_8  compatible
     */
    Map<String,String> processReport (FITAPurchaseReport report) {
        Map<String, String> dict = new Hashtable<String, String>();
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
            Double price = report.getPrice();
            putIfNotNull(KEYS.PRICE,price==null?null:price.toString(), dict);

            putIfNotNull(KEYS.SHOP_COUNTRY,report.getShopCountry(), dict);
            putIfNotNull(KEYS.SHOP_LANGUAGE,report.getShopLanguage(), dict);
            putIfNotNull(KEYS.SHOP_SIZING_SYSTEM,report.getShopSizingSystem(), dict);

            String connect_sid =report.getSid();

            try {
                String cookies = CookieManager.getInstance().getCookie(FITAWebWidget.widgetContainerURL);
                connect_sid = getCookieValue(cookies).get("connect.sid");
            } catch (NullPointerException ex) {
                // we are not in an web env.
            }finally {
                putIfNotNull(KEYS.SID,connect_sid, dict);
            }

            putIfNotNull(KEYS.SIZE_REGION,report.getSizeRegion(), dict);

            dict.put(KEYS.SENDER_TYPE,"callback");
            dict.put(KEYS.HOSTNAME, KEYS.EMBED_ANDROID);
            long epocheTimeInMS = new Date().getTime();
            dict.put(KEYS.TIME_STAMP, ""+epocheTimeInMS);
            // TODO JAVA 8+
            //long unixTimestamp = Instant.now().getEpochSecond();

        }
        return dict;
    }


    Map<String, String> getCookieValue(String cookieHeader) {
            Map<String, String> result = new HashMap<String, String>();
            String[] cookies = cookieHeader.split( "; " );
            for ( int i = 0; i < cookies.length; i++ ) {
                String cookie = cookies[ i ];
                String key = cookie.split( "=" )[ 0 ];

                String value = cookie.substring( cookie.indexOf( '=' ) + 1, cookie.length() );
                result.put( key, value );
            }
            return result;
        }


    private void putIfNotNull(String key, String value, Map<String, String> dict) {
        if (value!=null){
            dict.put(key,value);
        }
    }


    @Override
    public String toString(){
            StringBuilder result = new StringBuilder();
            String NL = System.getProperty("line.separator");

            result.append(this.getClass().getName());
            result.append(" Object {");
            result.append(NL);

            //determine fields declared in this class only (no fields of superclass)
            Field[] fields = this.getClass().getDeclaredFields();

            //print field names paired with their values
            for (Field field : fields) {
                result.append("  ");
                try {
                    result.append(field.getName());
                    result.append(": ");
                    //requires access to private field:
                    result.append(field.get(this));
                }
                catch (IllegalAccessException ex) {
                    // do nothing
                }
                result.append(NL);
            }
            result.append("}");

            return result.toString();
        }


    /**
     * provides easy access to possible values to report
     */
    public class KEYS {
        public static final String PRODUCT_ID = "productId";
        public static final String VARIANT_ID = "variantId";
        public static final String HOSTNAME = "hostname";
        public static final String ORDER_ID = "orderId";
        public static final String USER_ID = "userId";
        public static final String PURCHASED_SIZE = "purchasedSize";
        public static final String SHOP = "shop";
        public static final String CURRENCY = "currency";
        public static final String EAN = "ean";
        public static final String FUNNEL = "funnel";
        public static final String PRICE = "price";
        public static final String SHOP_COUNTRY = "shopCountry";
        public static final String SHOP_LANGUAGE = "shopLanguage";
        public static final String SHOP_SIZING_SYSTEM = "shopSizingSystem";
        public static final String SID = "sid";
        public static final String SIZE_REGION = "sizeRegion";
        public static final String SENDER_TYPE = "senderType";
        public static final String TIME_STAMP = "timeStamp";

        static final String EMBED_ANDROID = "embed-android";
    }
}
