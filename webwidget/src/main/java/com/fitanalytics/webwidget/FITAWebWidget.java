package com.fitanalytics.webwidget;

import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

public class FITAWebWidget {
    public static String widgetContainerURL = "https://widget.fitanalytics.com/widget/app-embed.html";

    protected WebView mView;
    protected FITAWebViewClient mClient;
    protected FITAWebWidgetHandler mHandler;
    protected boolean isLoading;

    public FITAWebWidget(WebView view, FITAWebWidgetHandler handler) {
        mView = view;
        mHandler = handler;
        initializeWebView(mView, mHandler);
    }

    //
    // Public API methods
    //

    /**
     * Begin loading the HTML widget container page.
     * @return `false` if the container is already being loaded
     */
    public boolean load() {
        if (!isLoading) {
            isLoading = true;
            mView.loadUrl(FITAWebWidget.widgetContainerURL);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Create the widget with a product serial and widget options. Must be called after the `FITAWebWidgetHandler.onWebWidgetReady` callback.
     * @param productSerial (nullable) The serial ID of the product to load at beginning. When `null` no product will be loaded.
     * @param options       Initial set of options for the widget as WidgetOptions object.
     */
    public void create(String productSerial, WidgetOptions options) {
        create(productSerial, options.toJSON());
    }
    /**
     * Create the widget with a product serial. For details see above.
     * @param productSerial (nullable) The serial ID of the product to load at beginning. When `null` no product will be loaded.
     */
    public void create(String productSerial) {
        create(productSerial, (JSONObject) null);
    }
    /**
     * Create the widget with a product serial and widget options as JSONObject. For details see above.
     * @param productSerial (nullable) The serial ID of the product to load at beginning. When `null` no product will be loaded.
     * @param options       Initial set of options for the widget as JSONObject object.
     */
    public void create(String productSerial, JSONObject options) {
        try {
            createAndSendAction("init", createWidgetArguments(productSerial, options));
        } catch (JSONException e) {
            Log.e("fitaWidget", "Error sending message");
        }
    }

    /**
     * Show the actual widget. When the widget finishes opening, it will call the
     * `FITAWebWidgetHandler.onWebWidgetOpen` callback.
     * Allows passing new product serial and/or widget options object.
     * @param productSerial (nullable) The new product serial
     * @param options       Additional widget options as WidgetOptions
     */
    public void open(String productSerial, WidgetOptions options) {
        open(productSerial, options.toJSON());
    }
    /**
     * Open the widget with a new product serial. For details see above.
     * @param productSerial (nullable) the new product serial
     */
    public void open(String productSerial) {
        open(productSerial, (JSONObject) null);
    }
    /**
     * Open the widget with a new product serial and/or widget object (as JSONObejct). For details see above.
     * @param productSerial (nullable) the new product serial
     * @param options       Additional widget options as JSONObject
     */
    public void open(String productSerial, JSONObject options) {
        try {
            createAndSendAction("open", createWidgetArguments(productSerial, options));
        } catch (JSONException e) {
            Log.e("fitaWidget", "Error sending message");
        }
    }

    /**
     * Reconfigure the widget with the new product serial and/or widget options object. The new product serial triggers a loading of new product.
     * @param productSerial (nullable) the new product serial
     * @param options       widget options as WidgetOptions
     */
    public void reconfigure(String productSerial, WidgetOptions options) {
        reconfigure(productSerial, options.toJSON());
    }
    /**
     * Reconfigure the widget with a new product serial.
     * @param productSerial (nullable) the new product serial
     */
    public void reconfigure(String productSerial) {
        reconfigure(productSerial, (JSONObject) null);
    }
    /**
     * Reconfigure the widget with the new product serial and/or widget options object (as JSONObject).
     * @param productSerial (nullable) the new product serial
     * @param options       widget options as JSONObject
     */
    public void reconfigure(String productSerial, JSONObject options) {
        try {
            createAndSendAction("reconfigure", createWidgetArguments(productSerial, options));
        } catch (JSONException e) {
            Log.e("fitaWidget", "Error sending message");
        }
    }

    /**
     * Close the widget. Removes the widget markup and event handlers, potentially 
     * freeing up some memory.
     */
    public void close() {
        try {
            createAndSendAction("close", new JSONArray());
        } catch (Exception e) {
            Log.e("fitaWidget", "Error sending message");
        }
    }

    /**
     * Request a recommendation with productSerial and widget options. The recommended size will be returned as an argument to the `FITAWebWidgetHandler.onWebWidgetRecommend` callback.
     * @param productSerial (nullable) The new product serial
     * @param options       Additional widget options
     */
    public void recommend(String productSerial, WidgetOptions options) {
        recommend(productSerial, options.toJSON());
    }
    /**
     * Request a recommendation with productSerial. 
     * @param productSerial (nullable) The new product serial
     */
    public void recommend(String productSerial) {
        recommend(productSerial, (JSONObject) null);
    }
    /**
     * Request a recommendation with productSerial and widget options (as JSONObject).
     * @param productSerial (nullable) The new product serial
     * @param options       Additional widget options as JSONObject
     */
    public void recommend(String productSerial, JSONObject options) {
        try {
            createAndSendAction("getRecommendation", createWidgetArguments(productSerial, options));
        } catch (JSONException e) {
            Log.e("fitaWidget", "Error sending message");
        }
    }

    //
    // Private methods
    //

    private void initializeWebView(WebView webView, FITAWebWidgetHandler handler) {
        // enable cookies
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }

        // enable Javascript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        // enable debugging
        // TODO: make this build-time configurable
        WebView.setWebContentsDebuggingEnabled(true);

        // create and register the custom WebViewClient
        final FITAWebViewClient client = new FITAWebViewClient(this);
        webView.setWebViewClient(client);
        String cookie = CookieManager.getInstance().getCookie(FITAWebWidget.widgetContainerURL);

        // create the message interface
        webView.addJavascriptInterface(this, "fitaMessageInterface");
    }

    JSONObject extendJSON(JSONObject dst, JSONObject src) 
    throws JSONException {
        Iterator it = src.keys();
        while (it.hasNext()) {
            String key = (String)it.next();
            dst.put(key, src.get(key));
        }
        return dst;
    }

    JSONObject mergeJSON(JSONObject src1, JSONObject src2)
    throws JSONException {
        return extendJSON(extendJSON(new JSONObject(), src1), src2);
    }

    /**
     * Prepares the `options` argument that's expected by several widget methods
     * by merging `productSerial` (if present) into the `options` dictionary (if present).  
     * @param productSerial (optional) Product serial
     * @param options (optional) Additional widget options
     * @return Resulting merged JSONObject
     */
    private JSONArray createWidgetArguments(String productSerial, JSONObject options)
    throws JSONException {
        JSONObject argument;
        JSONObject defaultArgument = new JSONObject();
        defaultArgument.put("open", true);
        defaultArgument.put("hostname", "android");

        if (productSerial != null) {
            JSONObject productArgument = new JSONObject();
            defaultArgument.put("productSerial", productSerial);
            if (options != null) {
                argument = mergeJSON(options, productArgument);
            } else {
                argument = productArgument;
            }
        } else {
            argument = options != null ? options : new JSONObject();
        }

        argument = mergeJSON(defaultArgument, argument);

        return (new JSONArray().put(0, argument));
    }

    // JS-to-Android bridge messaging utilities

    @JavascriptInterface
    public void receiveMessage(final String message) {
        mView.post(new Runnable() {
            public void run() {
                processMessage(message);
            }
        });
    }

    // controller method that decodes and dispatches incoming widget event message
    // to corresponding event handlers
    public void processMessage(String encodedMessage) {
        JSONObject message = decodeMessage(encodedMessage);
        JSONArray arguments = null;

        try {
            String action = message.getString("action");

            if (!message.isNull("arguments")) {
                arguments = message.getJSONArray("arguments");
            }

            if (action != null) {
                if (action.equals("__ready")) {
                    mHandler.onWebWidgetReady(this);
                } else if (action.equals("__init")) {
                    mHandler.onWebWidgetInit(this);
                } else if (action.equals("load") && arguments != null) {
                    String productId = arguments.optString(0);
                    JSONObject details = arguments.optJSONObject(1);
                    mHandler.onWebWidgetProductLoad(this, productId, details);
                } else if (action.equals("error") && arguments != null) {
                    String productId = arguments.optString(0);
                    JSONObject details = arguments.optJSONObject(1);
                    mHandler.onWebWidgetProductLoadError(this, productId, details);
                } else if (action.equals("open") && arguments != null) {
                    String productId = arguments.optString(0);
                    mHandler.onWebWidgetOpen(this, productId);
                } else if (action.equals("close") && arguments != null) {
                    String productId = arguments.optString(0);
                    String size = arguments.optString(1);
                    JSONObject details = arguments.optJSONObject(2);
                    mHandler.onWebWidgetClose(this, productId, size, details);
                } else if (action.equals("cart") && arguments != null) {
                    String productId = arguments.optString(0);
                    String size = arguments.optString(1);
                    JSONObject details = arguments.optJSONObject(2);
                    mHandler.onWebWidgetAddToCart(this, productId, size, details);
                } else if (action.equals("recommend") && arguments != null) {
                    String productId = arguments.optString(0);
                    String size = arguments.optString(1);
                    JSONObject details = arguments.optJSONObject(2);
                    mHandler.onWebWidgetRecommend(this, productId, size, details);
                }
            }
        } catch (JSONException error) {
            Log.e("fitaWidget", "invalid receiveMessage: " + error.toString());
        }
    }

    public void createAndSendAction(String action, JSONArray arguments) {
        JSONObject message = new JSONObject();

        try {
            message.put("action", action);
            message.putOpt("arguments", arguments);
        }
        catch (JSONException err) {
            Log.e("fitaWidget", "Invalid message");
        }

        sendMessage(message);
    }

    protected void sendMessage(JSONObject message) {
        String encodedMessage = encodeMessage(message);
        String code = "window.__widgetManager.receiveMessage(\"" + encodedMessage + "\")";

        evaluateJavascript(code);
    }

    private void evaluateJavascript(String code) {
        mView.evaluateJavascript(code, null);
    }

    private JSONObject decodeMessage(String encodedMessage) {
        String text;
        JSONObject message = null;

        byte[] data = Base64.decode(encodedMessage, Base64.DEFAULT);

        try {
            text = new String(data, "UTF-8");
            message = new JSONObject(text);
        }
        catch (UnsupportedEncodingException error) {
            Log.e("fitaWidget", "Invalid message encoding");
        }
        catch (JSONException error) {
            Log.e("fitaWidget", "Invalid message format");
        }

        return message;
    }

    private String encodeMessage(JSONObject message) {
        String text = message.toString();
        String encodedText;

        try {
            byte[] data = text.getBytes("UTF-8");
            encodedText = Base64.encodeToString(data, Base64.NO_WRAP);
        }
        catch (UnsupportedEncodingException error) {
            return null;
        }

        return encodedText;
    }

    public void onContainerLoadError(String description) {
        mHandler.onWebWidgetLoadError(this, description);
    }
}
