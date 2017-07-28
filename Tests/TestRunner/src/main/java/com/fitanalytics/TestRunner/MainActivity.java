package com.fitanalytics.TestRunner;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.List;
import java.util.ArrayList;

import com.fitanalytics.webwidget.FITAWebWidgetHandler;
import com.fitanalytics.webwidget.FITAWebWidget;
import com.fitanalytics.webwidget.WidgetOptions;
import com.fitanalytics.webwidget.ManufacturedSize;


import org.jdeferred.android.AndroidDeferredObject;
import org.jdeferred.Deferred;
import org.jdeferred.Promise;

import android.util.Log;

public class MainActivity 
extends AppCompatActivity
implements FITAWebWidgetHandler {

    public FITAWebWidgetDriver mWidget;
    public WebView mWebView;
    private FITAWebWidgetHandler mHandler;

    private Deferred readyDeferred = null;
    private Deferred initDeferred = null;
    private Deferred productLoadDeferred = null;
    private Deferred openDeferred = null;
    private Deferred closeDeferred = null;
    private Deferred recommendDeferred = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //// Testing interface

    public Promise initializeWidget() {
        // set up the widget webview
        mWebView = (WebView) findViewById(R.id.widget_webview);
        final MainActivity self = this;
        final Deferred def = new AndroidDeferredObject();

        mWebView.post(new Runnable() {
            @Override
            public void run() {
                self.mWidget = new FITAWebWidgetDriver(mWebView, self);
                def.resolve(self.mWidget);
            }
        });
        return def.promise();
    }

    public FITAWebWidgetDriver getWidget() {
        return mWidget;
    }

    public Promise evaluateJavaScriptAsync(String code) {
        return mWidget.evaluateJavascriptAsync(code);
    }

    public Promise initializeDriver() {
        return mWidget.initializeDriver();
    }

    // for testing the messaging interface
    public Promise sendProductLoadMessage(String productSerial, JSONObject details) {
        productLoadDeferred = new AndroidDeferredObject();
        JSONArray args = new JSONArray();
        args.put(productSerial);
        args.put(details);
        mWidget.sendCallbackMessage("load", args);
        return productLoadDeferred.promise();
    }

    public Promise widgetLoad() {
        readyDeferred = new AndroidDeferredObject();
        mWidget.load();
        return readyDeferred.promise();
    }

    public Promise widgetCreate(String productSerial, WidgetOptions options) {
        productLoadDeferred = new AndroidDeferredObject();
        Promise promise = productLoadDeferred.promise();
        if (options != null) {
            mWidget.create(productSerial, options);
        } else if (productSerial != null) {
            mWidget.create(productSerial);
        } else {
            mWidget.create(null);
            productLoadDeferred.resolve(true); // resolve immediately as no product is loaded
        }
        return promise;
    }

    public Promise widgetOpen(String productSerial, WidgetOptions options) {
        openDeferred = new AndroidDeferredObject();
        mWidget.open(productSerial, options);
        return openDeferred.promise();
    }

    public Promise widgetReconfigure(String productSerial, WidgetOptions options) {
        productLoadDeferred = new AndroidDeferredObject();
        mWidget.reconfigure(productSerial, options);
        return productLoadDeferred.promise();
    }

    public Promise widgetClose() {
        closeDeferred = new AndroidDeferredObject();
        mWidget.close();
        return closeDeferred.promise();
    }

    public Promise widgetRecommend(String productSerial, WidgetOptions options) {
        recommendDeferred = new AndroidDeferredObject();
        mWidget.recommend(productSerial, options);
        return recommendDeferred.promise();
    }

    //// FITAWebWidgetHandler event callbacks ////

    private JSONObject buildArgs(String productSerial, String size, JSONObject details) {
        JSONObject args = new JSONObject();
        try {
            if (productSerial != null)
                args.put("productSerial", productSerial);
            if (size != null)
                args.put("size", size);
            if (details != null)
                args.put("details", details);
        } catch (JSONException e) {};
        return args;
    }

    public void onWebWidgetReady(FITAWebWidget widget) {
        Log.d("fitaWidget", "READY");
        if (readyDeferred != null) {
            readyDeferred.resolve(true);
            readyDeferred = null;
        }
    }

    public void onWebWidgetInit(FITAWebWidget widget) {
        Log.d("fitaWidget", "INIT");
        if (initDeferred != null) {
            initDeferred.resolve(true);
            initDeferred = null;
        }
    }

    public void onWebWidgetLoadError(FITAWebWidget widget, String message) {
        Log.d("fitaWidget", "LOAD ERROR " + message);
        if (productLoadDeferred != null) {
            JSONObject details = new JSONObject();
            try {
                details.put("message", message);
            } catch (JSONException e) {};
            productLoadDeferred.reject(buildArgs(null, null, details));
            productLoadDeferred = null;
        }
    }

    public void onWebWidgetProductLoad(FITAWebWidget widget, String productId, JSONObject details) {
        Log.d("fitaWidget", "LOAD " + productId + ", " + (details == null ? "null" : details.toString()));
        if (productLoadDeferred != null) {
            productLoadDeferred.resolve(buildArgs(productId, null, details));
            productLoadDeferred = null;
        }
    }

    public void onWebWidgetProductLoadError(FITAWebWidget widget, String productId, JSONObject details) {
        Log.d("fitaWidget", "LOAD ERROR " + productId + ", " + (details == null ? "null" : details.toString()));
        if (productLoadDeferred != null) {
            productLoadDeferred.reject(buildArgs(productId, null, details));
            productLoadDeferred = null;
        }
    }

    public void onWebWidgetOpen(FITAWebWidget widget, String productId) {
        Log.d("fitaWidget", "OPEN " + productId);
        if (openDeferred != null) {
            openDeferred.resolve(buildArgs(productId, null, null));
            openDeferred = null;
        }
    }

    public void onWebWidgetClose(FITAWebWidget widget, String productId, String size, JSONObject details) {
        Log.d("fitaWidget", "CLOSE " + productId + ", " + size + ", " + (details == null ? "null" : details.toString()));
        if (closeDeferred != null) {
            closeDeferred.resolve(buildArgs(productId, size, details));
            closeDeferred = null;
        }
    }

    public void onWebWidgetAddToCart(FITAWebWidget widget, String productId, String size, JSONObject details) {
        Log.d("fitaWidget", "CART " + productId + ", " + size + ", " + (details == null ? "null" : details.toString()));
        if (closeDeferred != null) {
            closeDeferred.resolve(buildArgs(productId, size, details));
            closeDeferred = null;
        }
    }

    public void onWebWidgetRecommend(FITAWebWidget widget, String productId, String size, JSONObject details) {
        Log.d("fitaWidget", "RECOMMEND " + productId + ", " + size + ", " + (details == null ? "null" : details.toString()));
        if (recommendDeferred != null) {
            recommendDeferred.resolve(buildArgs(productId, size, details));
            recommendDeferred = null;
        }
    }
}
