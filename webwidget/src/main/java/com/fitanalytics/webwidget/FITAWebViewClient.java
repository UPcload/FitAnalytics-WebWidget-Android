package com.fitanalytics.webwidget;

import android.net.Uri;

import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FITAWebViewClient extends WebViewClient {
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
}
