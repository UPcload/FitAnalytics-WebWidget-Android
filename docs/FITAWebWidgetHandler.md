## `FITAWebWidgetHandler` class reference

#### `public interface FITAWebWidgetHandler`

The interface for callback handlers that's accepted by the FITAWebWidget class constructor.

#### `public void onWebWidgetReady(FITAWebWidget widget)`

This method will be called when the widget container inside the WebView has successfully loaded and is ready to accept commands.

 * **Parameters:** `widget` — The widget controller instance

#### `public void onWebWidgetInit(FITAWebWidget widget)`

This method will be called when the widget instance inside the WebView is successfully created.

 * **Parameters:** `widget` — The widget controller instance

#### `public void onWebWidgetLoadError(FITAWebWidget widget, String description)`

This method will be called when the widget inside the WebView fails to load or initialize for some reason.

 * **Parameters:**
   * `widget` — The widget controller instance
   * `description` — Description of error, e.g. "net::ERR_TUNNEL_CONNECTION_FAILED"

#### `public void onWebWidgetProductLoad(FITAWebWidget widget, String productId, JSONObject details)`

This method will be called when the widget successfully loads the product info. It means the product is supported and the widget should be able to provide a size recommendation for it.

 * **Parameters:**
   * `widget` — The widget controller instance
   * `productId` — The serial ID of the product
   * `details` — The details object

#### `public void onWebWidgetProductLoadError(FITAWebWidget widget, String productId, JSONObject details)`

This method will be called when the widget failed to load the product info or the product is not supported.

 * **Parameters:**
   * `widget` — The widget controller instance
   * `productId` — The serial ID of the product
   * `details` — The details object

#### `public void onWebWidgetOpen(FITAWebWidget widget, String productId)`

This method will be called when the widget has successfully opened after the `open` method call.

 * **Parameters:**
   * `widget` — The widget controller instance
   * `productId` — The serial ID of the product

#### `public void onWebWidgetClose(FITAWebWidget widget, String productId, String size, JSONObject details)`

This method will be called when the user of the widget specifically requests closing of the widget by clicking on the close button.

 * **Parameters:**
   * `widget` — The widget controller instance
   * `productId` — The serial ID of the product
   * `size` — The recommended size of the product, if there was a recommendation. `null` if there wasn't any recommendation.
   * `details` — The details object.

#### `public void onWebWidgetAddToCart (FITAWebWidget widget, String productId, String size, JSONObject details)`

This method will be called when the user of the widget specifically clicks on the add-to-cart inside the widget.

 * **Parameters:**
   * `widget` — The widget controller instance
   * `productId` — The serial ID of the product
   * `size` — The size of the product that should be added to cart.
   * `details` — The details object.

#### `public void onWebWidgetRecommend(FITAWebWidget widget, String productId, String size, JSONObject details)`

This method will be called after the `FITAWebWidget.recommend` call, when the widget has received and processed the size recommendation.

 * **Parameters:**
   * `productId` — The serial ID of the product
   * `size` — The recommended size of the product.
   * `details` — The details object.
