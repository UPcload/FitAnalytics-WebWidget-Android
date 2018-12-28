# FitAnalytics WebWidget Integration in Android (Java)

## Overview

The WebWidget SDK allows integrating the Fit Analytics Size Advisor widget into your own Android app.

As a first step, we suggest that you familiarize yourself with the Fit Analytics web-based Size Advisor service by:  
1. Reading through the Fit Analytics website and trying out a sample product - https://www.fitanalytics.com/  
2. Reading through the Fit Analytics web developers guide - http://developers.fitanalytics.com/documentation  

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
public class MainActivity 
extends AppCompatActivity
implements FITAWebWidgetHandler {
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

```objc
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

Create a widget instance inside the container page (optionally) initialize it with `productSerial` and `options`. Options can be `null` or a dictionary of various options arguments. Important supported options are listed [here](#configurable-widget-options).

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

`manufacturedSizes` .. a dictionary of all manfactured sizes for the current product, including their in/out-of-stock status

`userId` .. the shop's user ID, in case the user is logged in

`shopCountry` .. the ISO code of the shop's country (e.g. US, DE, FR, GB, etc.)

`language` .. the language mutation of the shop (e.g. en, de, fr, es, it, etc.)

For the complete list of available widget options and their description, please see http://developers.fitanalytics.com/documentation#list-callbacks-parameters

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