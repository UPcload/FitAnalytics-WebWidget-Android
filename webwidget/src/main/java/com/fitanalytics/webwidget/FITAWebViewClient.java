package com.fitanalytics.webwidget;

import android.net.Uri;

import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FITAWebViewClient extends WebViewClient {
    protected FITAWebWidget mWidget;

    public FITAWebViewClient (FITAWebWidget widget) {
        mWidget = widget;
    }

    // only allow requests to *.fitanalytics.com domains
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();

        if (scheme.equals("http") || scheme.equals("https")) {
            String host = uri.getHost();
            if (host != null && (
                host.endsWith(".fitanalytics.com")
            )) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onReceivedError (WebView view, int errorCode, String description, String failingUrl) {
        if (failingUrl.equals(FITAWebWidget.widgetContainerURL)) {
            mWidget.onContainerLoadError(description);
        }
    }
}
