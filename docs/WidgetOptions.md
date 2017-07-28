## `WidgetOptions` class reference

#### `public class WidgetOptions`

Encapsulates all accepted options for FitAnalytics widget.

#### `public WidgetOptions setUserId(String userId)`

Set `userId` parameter. When the user is logged in, you can pass down the unique ID of the user in the system. Should NOT be based on contact info, like emails or phone numbers. User ID is generally used for improved user data persistence.

 * **Parameters:** `userId` — The `userId` value
 * **Returns:** The current instance for chaining

#### `public WidgetOptions setLanguage(String language)`

Set the `language` parameter. This parameter manually forces the widget to be delivered in the specified 2-letter language (ISO 639-1), for example "en" for English, "de" for German, "fr" for French or “pt” for Portuguese.

 * **Parameters:** `language` — The `language` value
 * **Returns:** The current instance for chaining

#### `public WidgetOptions setShopCountry(String shopCountry)`

Set the `shopCountry` parameter. If your shop is available in multiple countries, this parameter allows you to customize the Size Advisor experience to the current shop's country (e.g. display the relevant flag). Moreover, sometimes you want to offer the same products with more than one set of sizes, based on the language/country of your shop, or any other criteria. In that case, the country parameter will be used to select the correct sizing system of the product.

 * **Parameters:** `shopCountry` — The `shopCountry` value
 * **Returns:** The current instance for chaining

#### `public WidgetOptions setManufacturedSizes(ManufacturedSize[] manufacturedSizes)`

Set the `manufacturedSizes` parameter by array of ManufacturedSize objects. The `manufacturedSizes` parameter is optional and allows you to pass a list of sizes for that specific garment to the widget. This allows the size advisor to treat in-stock sizes different from out-of-stock sizes. For example, only in-stock can be added to cart through the size advisor. The listed sizes must be in the same format as the sizes shown on the product page.

 * **Parameters:** `manufacturedSizes` — Array of ManfacturedSize objects.
 * **Returns:** The current instance for chaining

#### `public WidgetOptions setManufacturedSizes(List<ManufacturedSize> sizeList)`

Set the `manufacturedSizes` parameter by List of ManufacturedSizes. Otherwise same as above.

 * **Parameters:** `sizeList` — List of ManufacturedSizes
 * **Returns:** The current instance for chaining

#### `public WidgetOptions setSizes(String[] sizes)`

Set the `sizes` parameter as array of string. The parameter is simpler version of manufacturedSizes, except that is specifies only sizes that are available. Should be used only in cases when the size availability information isn't available.

 * **Parameters:** `sizes` — The array of size codes as strings
 * **Returns:** The current instance for chaining

#### `public WidgetOptions setSizes(List<String> sizeList)`

Set the `sizes` parameter as list of string. Otherwise same as above.

 * **Parameters:** `sizes` — The list of size codes as strings
 * **Returns:** The current instance for chaining

#### `public WidgetOptions setThumb(String thumbUrl)`

Set the `thumb` parameter. Absolute URL to a product image thumbnail. Thumbnail image dimensions should be at least 300 x 300 pixels for optimal appearance. The optional thumb parameter allows you to override the thumbnail we have stored for your garment (if any) with a custom image. You need to pass an absolute URL for this and it should be an image that is at least 300 x 300 pixels.

 * **Parameters:** `thumbUrl` — The complete publicly-accessible URL of the garment thumbnail.
 * **Returns:** The current instance for chaining

#### `public WidgetOptions setMetric(String metric)`

Set the `metric` parameter. Usually, we are able to determine whether imperial or metric system should be used in the widget based on geographic location. In cases when we are not able to make this determination, the measurement system is selected according to the browser’s language. This selection can be incorrect at times, so if you want to pre-select either the metric or the imperial unit system, you can pass this parameter with a value of `WidgetOptions.UNITS_METRIC` for the metric units system or a value of `WidgetOptions.UNITS_IMPERIAL` for the imperial units system. Passing the metric parameter with a value of `WidgetOptions.UNITS_BRITISH`, will set the widget to use the imperial system, but with stones instead of pounds as weight units.

 * **Parameters:** `metric` — The `metric` value
 * **Returns:** The current instance for chaining

#### `public WidgetOptions setCartEnabled(Boolean state)`

Enable or disable the widget add-to-cart callback (`onWebWidgetAddToCart`) and integration. When the add-to-cart integration is enabled, the widget will show an "Add size to cart" button instead, "Return to store" button on the results screen. When the user clicks the button, the `onWebWidgetAddToCart` handler method will be called with the selected size. This option will be set only on the `FITAWebWidget.create()` call, otherwise it's ignored. The default state is disabled.

 * **Parameters:** `state` — The "add-to-cart" callback state.
 * **Returns:** The current instance for chaining

#### `public WidgetOptions setOpenEnabled(Boolean state)`

Enable or disable the widget open callback (`onWebWidgetOpen`). This option will be set only on the `FITAWebWidget.create()` call, otherwise it's ignored. The default state is enabled.

 * **Parameters:** `state` — The new "open" callback state.
 * **Returns:** The current instance for chaining

#### `public WidgetOptions setCloseEnabled(Boolean state)`

Enable or disable the widget close callback (`onWebWidgetClose`). This option will be set only on the `FITAWebWidget.create()` call, otherwise it's ignored. The default state is enabled.

 * **Parameters:** `state` — The "open" callback state.
 * **Returns:** The current instance for chaining

#### `public WidgetOptions setRecommendEnabled(Boolean state)`

Enable or disable the widget recommed callback (`onWebWidgetRecommend`). This option will be set only on the `FITAWebWidget.create()` call, otherwise it's ignored. The default state is enabled.

 * **Parameters:** `state` — The "recommed" callback state.
 * **Returns:** The current instance for chaining

#### `public WidgetOptions setLoadErrorEnabled(Boolean state)`

Enable or disable the widget product load error callback (`onWebWidgetProductLoadError`). This option will be set only on the `FITAWebWidget.create()` call, otherwise it's ignored. The default state is enabled.

 * **Parameters:** `state` — The "load error" callback state.
 * **Returns:** The current instance for chaining

#### `public JSONObject toJSON()`

Convert the WidgetOptions content to JSONObject representation, which is accepted by the widget.

 * **Returns:** The JSONObject result.
