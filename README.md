# FitAnalytics WebWidget Integration in Android (Java)

## Overview

The WebWidget SDK allows integrating the Fit Analytics Size Advisor widget into your own Android app.

As a first step, we suggest that you familiarize yourself with the Fit Analytics web-based Size Advisor service by:
1. Reading through the Fit Analytics website and trying out a sample product - https://www.fitanalytics.com/  

The integration method currently supported by this SDK is based on loading HTML/JS-based widget code in a separate WebView instance and establishing communication between the host app and the embedded web widget.  

The SDK introduces a layer that imitates a web-based (JavaScript) integration of the Fit Analytics widget by:  
1. Exporting the **FITAWebWidget** class, which serves as a main widget controller.   
2. Creating and initializing the widget in a provided web view instance.  
3. Exposing several methods that allow controlling the widget.  
4. Defining the **FITAWebWidgetHandler** interface, which allows registering various callbacks (by implementing them as interface methods). These callbacks are invoked by the widget controller through various events (e.g. when a user closes the widget, when the widget displays a recommendation, etc).  

---

## Installation and building the sample project

**Prerequisities:** 

1. Android SDK platform https://developer.android.com/studio/index.html#downloads
2. Gradle build tool https://gradle.org/install/
3. [API-Level 19+](https://developer.android.com/reference/android/os/Build.VERSION_CODES.html#KITKAT),  `Build.VERSION_CODES.KITKAT`

**Step 1.** Clone this repository: 

```
$ git clone https://github.com/UPcload/FitAnalytics-WebWidget-Android.git
$ cd FitAnalytics-WebWidget-Android
```

**Step 2.** Build the example APK

```
$ cd Examples/FitAnalytics-Demo
$ gradle build
```

**Step 3.** Deploy to running emulator or a connected device and run the app.

```
$ gradle appStart
```

---

## Integration Procedure

We're presuming a simple app with the single main MainActivity class.

Import the **FitAWebWidget** and the **FITAWebWidgetHandler** packages into your **MainActivity** class
 
```java
import com.fitanalytics.webwidget.FITAWebWidgetHandler;
import com.fitanalytics.webwidget.FITAWebWidget;
```

The MainActivity class implements the **FITAWebWidgetHandler** interface, so that all widget callbacks are implemented directly on it.

```java
public class MainActivity  extends AppCompatActivity implements FITAWebWidgetHandler {
  ...
    }
```

Add a property for storing the reference to the widget.

```java
private FITAWebWidget mWidget;
```

Add a property for the WebView reference. This is the WebView instance that will contain the widget container HTML page.

```java
private WebView mWebView;
```

Initialize the widget controller with the WebView instance and assign self as the callback handler.

```java
mWidget = new FITAWebWidget(mWebView, this);
```

## Classes

* [FITAWebWidget](docs/FITAWebWidget.md) .. The main class that facilitates communication with the actual widget inside the WebView container

* [FITAWebWidgetHandler](docs/FITAWebWidgetHandler.md) .. The interface widget callback handlers that's accepted by the FITAWebWidget class constructor

* [WidgetOptions](docs/WidgetOptions.md) .. A helper class that encapsulates all accepted options for the FitAnalytics widget.

* [ManufacturedSize](docs/ManufacturedSize.md) .. A helper class that encapsulates the code of the size and its availability status for the `manufacturedSizes` widget option.

## FITAWebWidget Methods

**`boolean load()`**

Begin loading the HTML widget container page. If the loading fails, the **onWebWidgetLoadError** callback will be called. Otherwise, once the container page has finished loading, the **onWebWidgetReady** callback is called.

```java
widget.load();
```

&nbsp;

**`void create(String productSerial, JSONObject options)`**

Create a widget instance inside the container page (optionally) initialize it with `productSerial` and `options`. `productSerial`  are composed of your shop-prefix, a dash, and the productId. Example: `happyshop-123556`.  Options can be `null` or a dictionary of various options arguments. Important supported options are listed [here](#configurable-widget-options).

This method should be called only after the **onWebWidgetReady** callback has been called (or inside the callback) and will trigger the **onWebWidgetInit** callback. If the `productSerial` has been specified, the **onWebWidgetProductLoad** will be also called, once the product info has been loaded. 

```java
// options: { "sizes": [ "S", "XL" ] }

widget.create("example-123456", (new JSONObject())
  .put("sizes", (new JSONArray())
    .put(0, "S")
    .put("1", "XL")
  )
);
```

&nbsp;

**`void open(String productSerial, JSONObject options)`**

Show the actual widget. Optionally configure the widget with new `productSerial` and/or `options`. It may trigger loading additional resources over network, and will show the widget only after all assets have been loaded. When the opening is finished, the **onWebWidgetOpen** callback will be called on the callback handler.

```java
widget.open("example-123456", (new JSONObject())
  .put("sizes", (new JSONArray())
    .put(0, "S")
    .put("1", "XL")
  )
);
```

&nbsp;

**`void close()`**

Close the widget and remove the widget markup. Will trigger the **onWebWidgetClose** callback when it finishes.

```java
widget.close();
```

&nbsp;

**`void recommend(String productSerial, JSONObject options)`**

Request a recommendation. Optionally configure the widget with new productSerial and/or options. The recommended size and additional details will be returned as arguments to the **onWebWidgetRecommend** callback.

```java
widget.recommend("example-123456", null);
```

&nbsp;

**`void reconfigure(String productSerial, JSONObject options)`**

Configure the widget with the new productSerial and/or widget options. If the `productSerial` argument is non-nil and is different from the last provided product serial, this will trigger a request for the product information. After the new product info is loaded, the **onWebWidgetProductLoad** will be called. If the product serial is invalid or the product isn't supported by Fit Analytics, the **onWebWidgetProductLoadError** will be called.

```java
widget.reconfigure("example-123456", null);

// OR

widget.reconfigure(null, (new JSONObject())
  .put("sizes", (new JSONArray())
    .put(0, "S")
    .put("1", "XL")
  )
);
```

## FITAWebWidgetHandler Callbacks

```java
public void onWebWidgetReady(FITAWebWidget widget);
```

This method will be called when the widget container inside the WebView successfully loads and is ready to accept commands.

&nbsp;

```java
public void onWebWidgetInit(FITAWebWidget widget);
```

This method will be called when the widget instance inside the WebView is successfully created.

&nbsp;

```java
public void onWebWidgetLoadError(FITAWebWidget widget, String description);
```

This method will be called when the widget inside the WebView fails to load or initialize for some reason.

&nbsp;

```java
public void onWebWidgetProductLoad(FITAWebWidget widget, String productId, JSONObject details);
```

This method will be called when the widget has successfully loaded the product info. A successful product load means that the product is supported by FitAnalytics and the widget should be able to provide a size recommendation for it.

&nbsp;

```java
public void onWebWidgetProductLoadError(FITAWebWidget widget, String productId, JSONObject details);
```

This method will be called when the widget failed to load the product info or the product is not supported.

&nbsp;

```java
public void onWebWidgetOpen(FITAWebWidget widget, String productId);
```

This method will be called when the widget has successfully opened after the `open` method call.

## Configurable widget options

```java
interface FitAnalyticsWidgetOptions {
    /**
     *  (Shop Session ID) .. a first-party client generated session ID (can be a cookie): we use it to track purchases and keep our data more consistent (we **do NOT** use it to track or identify users)
     */
    String shopSessionId;
    /**
     * The shop prefix, this is a value that we set internally so we can identify your shop with the product.
     */
    String shopPrefix;
    /**
     * The product serial number, which is used to identify the product in the Fit Analytics database.
     * If `shopPrefix` is not set, we are going to infer the shop prefix based on the product serial number prefix. E.G. `shopprefix-abcd1234`
     */
    String productSerial;
    /**
     * Product thumbnail image URL.
     */
    String thumb;
    /**
     * All the sizes of your product, each size should be a key in the object, and the value should be a boolean indicating if the size is available or not.
     * They keys should match with the keys in the products' feed.
     * E.G. { M: true, L: false } means that the product is available in size M but not in size L.
     */
    Map<String, Boolean> manufacturedSizes;
    /**
     * In stock sizes for the current product.
     * E.G. [{ value: "M", isAvailable: true }, { value: "L", isAvailable: false }]
     */
    List<Size> sizes;
    /**
     * The user identifier based on the shop's user id, for example in case the user is logged in.
     */
    String userId;
    /**
     * ISO 639-1
     * E.G. "en"
     */
    String language;
    /**
     * ISO 3166-1
     * E.G. "GB"
     */
    String shopCountry;
    /**
     * Metric system
     * 0: imperial
     * 1: metric
     * 2: british
     * If it is not set it will be inferred from the shop country.
     */
    int metric;
    void close(String productSerial, String size);
    void error(String productSerial);
    void cart(String productSerial, String size);
    void recommend(String productSerial, String size);
    void load(String productSerial);
    String userAge;
    /**
     * m: man
     * w: women
     */
    String userGender;
    /**
     * Even if the `metric` property is set to a different numerical system, the user's weight and height should be in kilograms and centimeters respectively.
     */
    String userWeight;
    String userHeight;

    /**
     * Women bra measurements
     * The values described bellow can be obtained from the user's profile after they have filled in their bra measurements.
     * It is a large subset of possible bra measurements to be described, usually you feed the widget with the measurements available from the profile
     */
    String userBraBust;
    String userBraCup;
    String userBraSystem;
}

class Size {
    String value;
    boolean isAvailable;
}
```

---
 
## Purchase reporting
 
 Providing purchase data not only provides you with insights but also continually enhances the value Fit Finder and our personalized recommendations add to your e-commerce platform. Therefore, we highly recommend you to set up purchase reporting on your end.
 
 The reporting is done by sending a simple HTTP request via https.
 
 The usual report is a collection of attributes such as the order ID, the product serial for each purchased item, purchased size, price, currency, etc.
 
 The most common attributes are:
 
```java
 public interface FitAnalyticsPurchaseOptions {
    /**
     *  (Shop Session ID) .. a first-party client generated session ID (can be a cookie): we use it to track purchases and keep our data more consistent (we **do NOT** use it to track or identify users)
     * (value **MUST** conform with the one passed in the PDP for the same shopping session)
     */
    String shopSessionId;
    /**
     * The product serial number, which is used to identify the product in the Fit Analytics database.
     * If `shopPrefix` is not set, we are going to infer the shop prefix based on the product serial number prefix. E.G. `shopprefix-abcd1234`
     */
    String productSerial;
    /**
     * (optional) the size-specific identifier
     */
    String shopArticleCode;
    /**
     * Acts as a size code identifier that we can use when gathering data per size
     */
    String ean;
    /**
     * Shops' internal order identifier.
     */
    String orderId;
    /**
     * It should match the size that is available in the product's feed.
     */
    String purchasedSize;
    /**
     * The user identifier based on the shop's user id, for example in case the user is logged in.
     */
    String userId;
    /**
     * ISO 639-1
     * E.G. "en"
     */
    String language;
    /**
     * ISO 3166-1
     * E.G. "GB"
     */
    String shopCountry;
    double price;
    int quantity;
    /**
     * E.G. "EUR" | "USD" | "GBP"
     */
    String currency;
}
```

 
### Usage
 
The **FITAPurchaseReporter** implements   **AsyncTask<FITAPurchaseReport, Integer, Long>**
in order to call it asynchronously in the background with the method  **new FITAPurchaseReporter().execute(FITAPurchaseReport... fitaPurchaseReports);**.
See the Android SDK Documentation for [AsyncTask ](https://developer.android.com/reference/android/os/AsyncTask).
 

Import the **FITAPurchaseReport** and the **FITAPurchaseReporter** classes fom com.fitanalytics.com.webwidget package in your UI
   
  
 ```
import com.fitanalytics.webwidget.FITAPurchaseReport;
import com.fitanalytics.webwidget.FITAPurchaseReporter;
```

 Create a new instance of the purchase reporter (**FITAPurchaseReporter**).
 
 ```java
 FITAPurchaseReporter reporter = new FITAPurchaseReporter();
 ```
 
 For each line item in the customer's order, create a new instance of **FITAPurchaseReport**.
 For your convenience, the constructor is overloaded. In order to have the best results in prediction use as many as possible information of the ordered items. 
 
 Fit Anayltics needs at least the **String productSerial** and **String orderId**. We highly recommend to use **FITAPurchaseReport(String productSerial, String orderId, String purchasedSize , Double price, String currency)**
 to get the best results.
   
 ```java
 FITAPurchaseReport report = FITAPurchaseReport(String productSerial, String orderId, String purchasedSize , Double price, String currency);

 // add additional attributes, such as shopCountry, lanugage etc. here:
 report.setShopCountry("DE");
 report.setShopLanguage("de");
 report.setShopSessionId("0a1b2c3d");
 ```
 
Send the reports via reporter with **reporter.execute(FITAPurchaseReport... fitaPurchaseReports)**. You can not re-use the reporter. Once you have called **execute(FITAPurchaseReport... fitaPurchaseReports)**. 
Calling the **AsyncTask** method **reporter.execute(FITAPurchaseReport... fitaPurchaseReports)** will fire the report(s) in the background in a single thread. 
Consult the [AsyncTask Documenation](https://developer.android.com/reference/android/os/AsyncTask) for more information.



## Common pitfalls

### Opening Fit Finder in background
In some implementations, Fit Finder is opened in the background for every PDP in hidden mode. In those implementations, clicking the size help link unhides Fit Finder.
Such an implementation should be avoided as it is inefficient for several reasons:
1. It runs unnecessary Javascript code in the background for users who are not interested in size help, which means extra app resources are consumed for no reason.
2. It sends additional requests to Fit Analytics servers resulting in wasted bandwidth, which is especially precious on mobile devices.

Fortunately, the Fit Analytics API already provides support for efficient resources usage together with optimal loading time. Your code should indeed load the widget in the background, but not open it.

A quick summary of the recommended process is below:
1. There needs to be only a **single** instance of the widget that runs in the background.
2. You can use the FITAWebWidget::reconfigure(productSerial,...) method to trigger widget reconfiguration when a new product is viewed by the user.
3. Once you know that the product is supported, you can request the immediate recommendation (PDP-embedded recommendation) via FITAWebWidget::recommend(...) method.
4. When a user navigates to another product you can reconfigure the widget with the new product info and call the FITAWebWidget::recommend(...) again.
5. If the user wants to open the widget you can simply call FITAWebWidget::open(...) method and wait for FITAWebWidgetHandler::onWebWidgetOpen(...) callback.

A summary of the recommended steps can be found in this wiki:
https://github.com/UPcload/FitAnalytics-WebWidget-Android/wiki/Common-integration-process-notes

