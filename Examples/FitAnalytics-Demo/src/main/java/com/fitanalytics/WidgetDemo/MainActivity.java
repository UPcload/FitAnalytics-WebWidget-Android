package com.fitanalytics.WidgetDemo;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import com.fitanalytics.webwidget.FITAWebWidgetHandler;
import com.fitanalytics.webwidget.FITAWebWidget;

import android.util.Log;

public class MainActivity 
extends AppCompatActivity
implements FITAWebWidgetHandler {

    private WebView mWebView;
    private FITAWebWidget mWidget;
    private FITAWebWidgetHandler mHandler;

    private EditText mEditProductId;
    private Button mSubmitProductId;
    private TextView mResultText;

    private String currentProductId = "";

    protected void onCreate(Bundle savedInstanceState) {
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
            JSONObject config = new JSONObject();
            mWidget.open(currentProductId, config);

            setMessage("Opening ...");
        }
        else if (id == R.id.action_close) {
            mWidget.close();
        }
        else if (id == R.id.action_recommend) {
            JSONObject config = new JSONObject();
            mWidget.recommend(currentProductId, config);

            setMessage("Recommending...");
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSubmitProductIdClick(View button) {
        String productId = mEditProductId.getText().toString();
        Log.d("fitaWidget", "current Product Id: " + productId);
        currentProductId = productId;

        JSONObject config = new JSONObject();
        mWidget.reconfigure(currentProductId, config);
        setMessage("Loading...");
    }

    //////////////////////////////////

    private String getUserSid() {
        // NOOP
        return null;
    }

    private void setUserSid(String sid) {
        // NOOP
    }

    private void onRecommendedSize(String size) {
        String text = "Size: " + (size == null ? "" : size);
        setMessage(text);
    }

    private void setMessage(String msg) {
        mResultText.setText(msg);
    }

    //// FITAWebWidgetHandler event callbacks ////

    public void onWebWidgetReady(FITAWebWidget widget) {
        Log.d("fitaWidget", "READY");
        try {
            JSONObject config = new JSONObject()
                .put("language", "en")
                .put("shopCountry", "US");

            // if we're storing the session locally (e.g. in user's profile)
            // we can supply it to the widget directly, instead of relying on cookies
            if (getUserSid() != null) {
                config.put("sid", getUserSid());
            }

            widget.create(null, config);
            setMessage("Ready");
        } catch (JSONException e) {/* noop */}
    }

    public void onWebWidgetInit(FITAWebWidget widget) {
        Log.d("fitaWidget", "INIT");
        setMessage("Initialized");
    }

    public void onWebWidgetLoadError(FITAWebWidget widget, String details) {
        Log.d("fitaWidget", "LOAD ERROR " + details);
        setMessage("Load error");
    }

    public void onWebWidgetProductLoad(FITAWebWidget widget, String productId, JSONObject details) {
        Log.d("fitaWidget", "LOAD " + productId + ", " + (details == null ? "null" : details.toString()));
        setMessage("Loaded");
    }

    public void onWebWidgetProductLoadError(FITAWebWidget widget, String productId, JSONObject details) {
        Log.d("fitaWidget", "LOAD ERROR " + productId + ", " + (details == null ? "null" : details.toString()));
        setMessage("Load error");
    }

    public void onWebWidgetOpen(FITAWebWidget widget, String productId) {
        Log.d("fitaWidget", "OPEN " + productId);
    }

    public void onWebWidgetClose(FITAWebWidget widget, String productId, String size, JSONObject details) {
        Log.d("fitaWidget", "CLOSE " + productId + ", " + size + ", " + (details == null ? "null" : details.toString()));
        onRecommendedSize(size);
    }

    public void onWebWidgetAddToCart(FITAWebWidget widget, String productId, String size, JSONObject details) {
        Log.d("fitaWidget", "CART " + productId + ", " + size + ", " + (details == null ? "null" : details.toString()));
        onRecommendedSize(size);
    }

    public void onWebWidgetRecommend(FITAWebWidget widget, String productId, String size, JSONObject details) {
        Log.d("fitaWidget", "RECOMMEND " + productId + ", " + size + ", " + (details == null ? "null" : details.toString()));
        onRecommendedSize(size);
    }
}
