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
