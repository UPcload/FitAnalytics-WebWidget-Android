package com.fitanalytics.WidgetDemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fitanalytics.webwidget.FITAPurchaseReport;
import com.fitanalytics.webwidget.FITAPurchaseReporter;
import com.fitanalytics.webwidget.FITAWebWidget;
import com.fitanalytics.webwidget.FITAWebWidgetHandler;
import com.fitanalytics.webwidget.ManufacturedSize;
import com.fitanalytics.webwidget.WidgetOptions;
import com.fitanalytics.WidgetDemo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity  extends AppCompatActivity implements FITAWebWidgetHandler {

    private WebView mWebView;
    private FITAWebWidget mWidget;

    private FITAPurchaseReport report;
    private FITAPurchaseReporter reporter;

    private EditText mEditProductId;
    private Button mSubmitProductId;
    private TextView mResultText;

    private String currentProductId = "";

    protected void onCreate(Bundle savedInstanceState) {
        WebView.setWebContentsDebuggingEnabled(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set up demo controls
        mEditProductId = (EditText) findViewById(R.id.editProductId);
        if (currentProductId != null) {
            Editable text = mEditProductId.getText();
            text.clear();
            text.append(currentProductId);
        }

        mSubmitProductId = (Button) findViewById(R.id.submitProductId);
        mResultText = (TextView) findViewById(R.id.resultText);

        // set up the widget webview
        mWebView = (WebView) findViewById(R.id.widget_webview);


        mWidget = new FITAWebWidget(mWebView, this);
        mWidget.load();


    }

    private void createReportingTask() {
        reporter = new FITAPurchaseReporter(this);
        /****
         * This is a test env, remove this when
         */
        reporter.setDryRun(true);
        reporter.setTestEnv(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_open) {
            currentProductId = mEditProductId.getText().toString();
            if (currentProductId.equals("")){
                // to avoid manual typing
                currentProductId = "widgetpreview-upper-m";
            }
            mWidget.open(currentProductId);

            setMessage("Opening ...");
        }
        else if (id == R.id.action_close) {
            mWidget.close();
        }
        else if (id == R.id.action_recommend) {
            mWidget.recommend(currentProductId);

            setMessage("Recommending...");
        }

        else if (id == R.id.action_purchase) {

            String productId = mEditProductId.getText().toString();
            if (productId.equals("")) {
                setMessage("Info: please enter a productID ");
            }
            else {
                // setup the reporterAsycTask
                createReportingTask();
                setMessage("Purchasing ... " + productId);
                onAddToCartAndPurchase(productId);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSubmitProductIdClick(View button) {
        String productId = mEditProductId.getText().toString();
        Log.d("fitaWidget", "current Product Id: " + productId);
        currentProductId = productId;

        mWidget.reconfigure(currentProductId);
        setMessage("loading widget... "+productId );
    }

    //////////////////////////////////

    private String getUserId() {
        // NOOP
        return null;
    }

    private List<ManufacturedSize> getManufacturedSizes() {
        List<ManufacturedSize> list = new ArrayList<ManufacturedSize>();
        list.add(new ManufacturedSize("S", false));
        list.add(new ManufacturedSize("M", true));
        list.add(new ManufacturedSize("L", true));
        list.add(new ManufacturedSize("XL", false));
        return list;
    }

    private void onRecommendedSize(String size) {
        String text = "Size: " + (size == null ? "" : size);
        setMessage(text);
    }


    private void onAddToCart(String productId) {
        Toast.makeText(this, "Product  " + productId + " added to shopping cart", Toast.LENGTH_SHORT).show();

    }

    private void onAddToCartAndPurchase(String productId) {

        Toast.makeText(this, "Product  " + productId + " was ordered", Toast.LENGTH_SHORT).show();


        FITAPurchaseReport report = createReport(productId);


        AsyncTask task  = reporter.execute(report);
        Toast.makeText(this, "Purchase for " + productId + " was fired", Toast.LENGTH_SHORT).show();
    }

    private void setMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        mResultText.setText(msg);
    }


   //// fake orderID, create report
   private FITAPurchaseReport createReport(String productId) {
        String orderId = "test-purchase-from-android-orderid";
        report = new FITAPurchaseReport(productId, orderId);
        return report;
    }


    //// FITAWebWidgetHandler event callbacks ////

    @Override
    public void onWebWidgetReady(FITAWebWidget widget) {
        Log.d("fitaWidget", "READY");

        /***
         * A widget in en_US
         * with stones and lb and ft and in
         * and a add dto cart enabled
         */
        WidgetOptions options = new WidgetOptions()
            .setLanguage("en")
            .setShopCountry("US")
            .setShopSessionId("0a1b2c3d")
            .setMetric(WidgetOptions.UNITS_BRITISH)
            .setCartEnabled(true);


        // (optional) add the userId for logged-in users
        if (getUserId() != null) {
             options.setUserId(getUserId());
        }

        options.setManufacturedSizes(getManufacturedSizes());

        widget.create(null, options);
        setMessage("Ready");
    }

    @Override
    public void onWebWidgetInit(FITAWebWidget widget) {
        Log.d("fitaWidget", "INIT");
        setMessage("Initialized");
    }

    @Override
    public void onWebWidgetLoadError(FITAWebWidget widget, String details) {
        Log.d("fitaWidget", "LOAD ERROR " + details);
        setMessage("Load error");
    }

    @Override
    public void onWebWidgetProductLoad(FITAWebWidget widget, String productId, JSONObject details) {
        Log.d("fitaWidget", "LOAD " + productId + ", " + (details == null ? "null" : details.toString()));
        setMessage("Loaded");
    }

    @Override
    public void onWebWidgetProductLoadError(FITAWebWidget widget, String productId, JSONObject details) {
        Log.d("fitaWidget", "LOAD ERROR " + productId + ", " + (details == null ? "null" : details.toString()));
        setMessage("Load error");
    }

    @Override
    public void onWebWidgetOpen(FITAWebWidget widget, String productId) {
        Log.d("fitaWidget", "OPEN " + productId);
    }

    @Override
    public void onWebWidgetClose(FITAWebWidget widget, String productId, String size, JSONObject details) {
        Log.d("fitaWidget", "CLOSE " + productId + ", " + size + ", " + (details == null ? "null" : details.toString()));
        onRecommendedSize(size);
    }

    @Override
    public void onWebWidgetAddToCart(FITAWebWidget widget, String productId, String size, JSONObject details) {
        Log.d("fitaWidget", "ADD_TO_CART " + productId + ", " + size + ", " + (details == null ? "null" : details.toString()));
        setMessage("added to cart : " + productId);
    }

    public void onWebWidgetRecommend(FITAWebWidget widget, String productId, String size, JSONObject details) {
        Log.d("fitaWidget", "RECOMMEND " + productId + ", " + size + ", " + (details == null ? "null" : details.toString()));
        onRecommendedSize(size);
    }


}
