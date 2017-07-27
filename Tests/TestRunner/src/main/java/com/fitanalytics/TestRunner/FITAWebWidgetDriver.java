package com.fitanalytics.TestRunner;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import org.jdeferred.android.AndroidDeferredObject;
import org.jdeferred.Deferred;
import org.jdeferred.Promise;

import android.webkit.WebView;
import android.webkit.ValueCallback;

import com.fitanalytics.webwidget.FITAWebWidget;
import com.fitanalytics.webwidget.FITAWebWidgetHandler;

import android.util.Log;

public class FITAWebWidgetDriver extends FITAWebWidget {
    private static String driverSource =
        "window.__driver = {"
        + "    $: FitAnalyticsWidget.jQuery,"
        + "    works: function () { return true },"
        + "    compileRefSelector: function (selector) {"
        + "        var parts = (selector || '').split(/s+/);"
        + "        parts.map(function (part) {"
        + "            return part.charAt(0) == '@' ? '[data-ref=\"' + part.replace(/^@/, '') + '\"]' : part"
        + "        }).join(' ')"
        + "    },"
        + "    getComponentByPath: function (path) {"
        + "        Array.isArray(path) || (path = path.split('.'));"
        + "        var out = window.__widgetManager.widget.screen;"
        + "        for (var i = 0, n = path.length; i < n; i++) {"
        + "            if (out == null) return;"
        + "            out = out.children;"
        + "            if (out == null) return;"
        + "            out = out[path[i]];"
        + "        }"
        + "        return out;"
        + "    },"
        + "    click: function (sel) { this.$(sel).click() },"
        + "    exists: function (sel) { this.$(sel).length > 0 },"
        + "    getInputValueByRef: function (ref) {"
        + "        return $(this.compileRefSelector(ref)).val()"
        + "    },"
        + "    setInputValueByRef: function (ref, value) {"
        + "        var ref = compileRefSelector(ref);"
        + "        this.click(ref).type(ref, value);"
        + "    },"
        + "    getComponentValue: function (path) {"
        + "        var comp = this.getComponentByPath(path);"
        + "        return comp.getValue();"
        + "    },"
        + "    setComponentValue: function (path, value) {"
        + "        var comp = this.getComponentByPath(path);"
        + "        return comp.setValue(value);"
        + "    },"
        + "    clickByRef: function (ref, value) { this.click(compileRefSelector(ref)) },"
        + "    existsByRef: function (ref, value) { return this.exists(compileRefSelector(ref)) },"
        + "    sendMessage: function (action, args) { window.__widgetManager.sendMessage({ action: action, arguments: args }); return true; },"

        + "    hasManager: function () { return !!window.__widgetManager },"
        + "    hasWidget: function () { return window.__widgetManager && !!window.__widgetManager.widget },"
        + "    isWidgetOpen: function () { return this.$(\"[data-ref='screen']\").length > 0 },"
        + "    getScreenName: function () {"
        + "        var screen = window.__widgetManager.widget.screen;"
        + "        return screen != null ? (screen.name + (screen.options && screen.options.step ? screen.options.step : '')) : null"
        + "    },"
        + "    getDisplayedRecSP: function () {"
        + "        var fp = this.$('#uclw_fit_prediction');"
        + "        return {"
        + "            size: this.$.trim(fp.find('h2').text()),"
        + "            size0: this.$.trim(fp.find('.uclw_bar_top .uclw_label').text()),"
        + "            size1: this.$.trim(fp.find('.uclw_bar_middle .uclw_label').text()),"
        + "        }"
        + "    },"
        + "};"
        + "window.__widgetManager && (window.__widgetManager.enableLogs = true);"
        + "true";

    public FITAWebWidgetDriver(WebView view, FITAWebWidgetHandler handler) {
        super(view, handler);
    }

    public Promise evaluateJavascriptAsync(String code) {
        final Deferred res = new AndroidDeferredObject();
        mView.evaluateJavascript(code, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) { res.resolve(s); }
        });
        return res.promise();
    }

    public Promise initializeDriver() { 
        return evaluateJavascriptAsync(driverSource);
    }

    public Promise driverCall(String name, JSONArray arguments) {
        String argsString = "[]";

        if (arguments != null) {
            argsString = arguments.toString();
        }

        String code = String.format("window.__driver['%s'].apply(__driver, %s)", name, argsString);

        return evaluateJavascriptAsync(code);
    }

    private JSONArray buildArgs(String... arguments) {
        JSONArray args = null;
        try {
            args = new JSONArray(arguments);
        } catch (JSONException e) { }
        return args;
    }

    public Promise sendCallbackMessage(String action, JSONArray arguments) {
        JSONArray args = new JSONArray();
        args.put(action);
        args.put(arguments);
        return driverCall("sendMessage", args);
    }

    public Promise testHasManager() {
        return driverCall("hasManager", null);
    }
    public Promise testHasWidget() {
        return driverCall("hasWidget", null);
    }
    public Promise testIsWidgetOpen() {
        return driverCall("isWidgetOpen", null);
    }
    public Promise testGetScreenName() {
        return driverCall("getScreenName", null);
    }
}