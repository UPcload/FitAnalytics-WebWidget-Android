package com.fitanalytics.webwidget;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.util.Log;

/**
 * Encapsulates all accepted options for FitAnalytics widget.
 */
public class WidgetOptions {
    // possible values for `metric` parameter
    public static String UNITS_IMPERIAL = "0";
    public static String UNITS_METRIC = "1";
    public static String UNITS_BRITISH = "2";

    private String mUserId = "";
    private String mShopSessionId = "";
    private String mLanguage = "";
    private String mShopCountry = "";
    private String mThumb = "";
    private String mMetric = "";
    private String[] mSizes = null;
    private ManufacturedSize[] mManufacturedSizes = null;

    private String mOpenEnabled = "";
    private String mCloseEnabled = "";
    private String mCartEnabled = "";
    private String mRecommendEnabled = "";
    private String mLoadErrorEnabled = "";

    /**
     * Set `userId` parameter. When the user is logged in, you can pass down the unique ID of the user in the system. Should NOT be based on contact info, like emails or phone numbers. User ID is generally used for improved user data persistence.
     * @param userId  The `userId` value
     * @return        The current instance for chaining
     */
    public WidgetOptions setUserId(String userId) {
        mUserId = userId;
        return this;
    }

    /**
     * Set `shopSessionId` parameter. this is a first-party client generated session (generally a cookie). Session ID is generally used by shopping apps to better track shopping journey (from opening app until reaching "thank you" page)
     * @param shopSessionId the Session ID value
     * @return the current instance for chaining
    */
    public WidgetOptions setShopSessionId(String shopSessionId) {
        mShopSessionId = shopSessionId;
        return this;
    }
    
    /**
     * Set the `language` parameter. This parameter manually forces the widget to be delivered in the specified 2-letter language (ISO 639-1), for example "en" for English, "de" for German, "fr" for French or “pt” for Portuguese.
     * @param language The `language` value
     * @return       The current instance for chaining
     */
    public WidgetOptions setLanguage(String language) {
        mLanguage = language;
        return this;
    }

    /**
     * Set the `shopCountry` parameter. If your shop is available in multiple countries, this parameter allows you to customize the Size Advisor experience to the current shop's country (e.g. display the relevant flag). Moreover, sometimes you want to offer the same products with more than one set of sizes, based on the language/country of your shop, or any other criteria. In that case, the country parameter will be used to select the correct sizing system of the product.
     * @param  shopCountry The `shopCountry` value
     * @return             The current instance for chaining
     */
    public WidgetOptions setShopCountry(String shopCountry) {
        mShopCountry = shopCountry;
        return this;
    }

    /** 
     * Set the `manufacturedSizes` parameter by array of ManufacturedSize objects. The `manufacturedSizes` parameter is optional and allows you to pass a list of sizes for that specific garment to the widget. This allows the size advisor to treat in-stock sizes different from out-of-stock sizes. For example, only in-stock can be added to cart through the size advisor. The listed sizes must be in the same format as the sizes shown on the product page.
     * @param  manufacturedSizes Array of ManfacturedSize objects.
     * @return                   The current instance for chaining
     */
    public WidgetOptions setManufacturedSizes(ManufacturedSize[] manufacturedSizes) {
        mManufacturedSizes = manufacturedSizes;
        return this;
    }

    /**
     * Set the `manufacturedSizes` parameter by List of ManufacturedSizes. Otherwise same as above.
     * @param  sizeList List of ManufacturedSizes
     * @return          The current instance for chaining   
     */
    public WidgetOptions setManufacturedSizes(List<ManufacturedSize> sizeList) {
        ManufacturedSize[] sizes = sizeList.toArray(new ManufacturedSize[sizeList.size()]);
        return setManufacturedSizes(sizes);
    }
    /** 
     * Set the `sizes` parameter as an array of strings. The parameter is simpler version of manufacturedSizes, except that it specifies only sizes that are available. Should be used only in cases when the size availability information isn't available.
     * @param  sizes The array of size codes as strings 
     * @return       The current instance for chaining
     */
    public WidgetOptions setSizes(String[] sizes) {
        mSizes = sizes;
        return this;
    }
    /**
     * Set the `sizes` parameter as a list of strings. Otherwise same as above.
     * @param  sizes The list of size codes as strings 
     * @return       The current instance for chaining
     */
    public WidgetOptions setSizes(List<String> sizeList) {
        String[] sizes = sizeList.toArray(new String[sizeList.size()]);
        return setSizes(sizes);
    }

    /**
     * Set the `thumb` parameter. Absolute URL to a product image thumbnail. Thumbnail image dimensions should be at least 300 x 300 pixels for optimal appearance. The optional thumb parameter allows you to override the thumbnail we have stored for your garment (if any) with a custom image. You need to pass an absolute URL for this and it should be an image that is at least 300 x 300 pixels.
     * @param  thumbUrl The complete publicly-accessible URL of the garment thumbnail.
     * @return       The current instance for chaining
     */
    public WidgetOptions setThumb(String thumbUrl) {
        mThumb = thumbUrl;
        return this;
    }

    /**
     * Set the `metric` parameter. Usually, we are able to determine whether imperial or metric system should be used in the widget based on geographic location. In cases when we are not able to make this determination, the measurement system is selected according to the browser’s language. This selection can be incorrect at times, so if you want to pre-select either the metric or the imperial unit system, you can pass this parameter with a value of `WidgetOptions.UNITS_METRIC` for the metric units system or a value of `WidgetOptions.UNITS_IMPERIAL` for the imperial units system. Passing the metric parameter with a value of `WidgetOptions.UNITS_BRITISH`, will set the widget to use the imperial system, but with stones instead of pounds as weight units.
     * @param  metric The `metric` value
     * @return       The current instance for chaining
     */
    public WidgetOptions setMetric(String metric) {
        mMetric = metric;
        return this;
    }


    /**
     * Enable or disable the widget add-to-cart callback (`onWebWidgetAddToCart`) and integration. When the add-to-cart integration is enabled, the widget will show an "Add size to cart" button instead, "Return to store" button on the results screen. When the user clicks the button, the `onWebWidgetAddToCart` handler method will be called with the selected size. This option will be set only on the `FITAWebWidget.create()` call, otherwise it's ignored. The default state is disabled.
     * @param  state The "add-to-cart" callback state.
     * @return       The current instance for chaining
     */
    public WidgetOptions setCartEnabled(Boolean state) {
        mCartEnabled = state ? "1" : "0";
        return this;
    }

    /**
     * Enable or disable the widget open callback (`onWebWidgetOpen`). This option will be set only on the `FITAWebWidget.create()` call, otherwise it's ignored. The default state is enabled.
     * @param  state The new "open" callback state.
     * @return       The current instance for chaining
     */
    public WidgetOptions setOpenEnabled(Boolean state) {
        mOpenEnabled = state ? "1" : "0";
        return this;
    }

    /**
     * Enable or disable the widget close callback (`onWebWidgetClose`). This option will be set only on the `FITAWebWidget.create()` call, otherwise it's ignored. The default state is enabled.
     * @param  state The "open" callback state.
     * @return       The current instance for chaining
     */
    public WidgetOptions setCloseEnabled(Boolean state) {
        mCloseEnabled = state ? "1" : "0";
        return this;
    }

    /**
     * Enable or disable the widget recommed callback (`onWebWidgetRecommend`). This option will be set only on the `FITAWebWidget.create()` call, otherwise it's ignored. The default state is enabled.
     * @param  state The "recommed" callback state.
     * @return       The current instance for chaining
     */
    public WidgetOptions setRecommendEnabled(Boolean state) {
        mRecommendEnabled = state ? "1" : "0";
        return this;
    }

    /**
     * Enable or disable the widget product load error callback (`onWebWidgetProductLoadError`). This option will be set only on the `FITAWebWidget.create()` call, otherwise it's ignored. The default state is enabled.
     * @param  state The "load error" callback state.
     * @return       The current instance for chaining
     */
    public WidgetOptions setLoadErrorEnabled(Boolean state) {
        mLoadErrorEnabled = state ? "1" : "0";
        return this;
    }

    /**
     * Convert the WidgetOptions content to JSONObject representation, which is accepted by the widget.
     * @return The JSONObject result.
     */
    public JSONObject toJSON() {
        JSONObject out = new JSONObject();
        try {
            if (mUserId != "") out.put("userId", mUserId);
            if (mShopSessionId != "") out.put("shopSessionId", mShopSessionId);
            if (mLanguage != "") out.put("language", mLanguage);
            if (mShopCountry != "") out.put("shopCountry", mShopCountry);
            if (mSizes != null) {
                out.put("sizes", new JSONArray(mSizes));
            }
            if (mManufacturedSizes != null) {
                JSONObject ms = new JSONObject();
                for (int i = 0; i < mManufacturedSizes.length; i++) {
                    ms.put(mManufacturedSizes[i].mSize, mManufacturedSizes[i].mAvailable);
                }
                out.put("manufacturedSizes", ms);
            }
            if (mThumb != "") out.put("thumb", mThumb);
            if (mMetric != "") out.put("metric", mMetric);

            if (mOpenEnabled != "") out.put("open", mOpenEnabled == "1");
            if (mCloseEnabled != "") out.put("close", mCloseEnabled == "1");
            if (mCartEnabled != "") out.put("cart", mCartEnabled == "1");
            if (mRecommendEnabled != "") out.put("recommend", mRecommendEnabled == "1");
            if (mLoadErrorEnabled != "") out.put("error", mLoadErrorEnabled == "1");

        } catch (JSONException e) {
            Log.e("fitaWidget", "Can't build options");
            return new JSONObject();
        }
        return out;
    }
}
