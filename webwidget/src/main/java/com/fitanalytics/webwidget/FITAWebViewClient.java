package com.fitanalytics.webwidget;

import android.net.Uri;
import android.content.Intent;

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
                host.endsWith("widget.fitanalytics.com")
            )) {
                // internal links are opened inside the webview
                return false;
            } else {
                // external links are opened in external app (e.g. browser)
                view.getContext().startActivity(
                    new Intent(Intent.ACTION_VIEW, Uri.parse(url))
                );
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
