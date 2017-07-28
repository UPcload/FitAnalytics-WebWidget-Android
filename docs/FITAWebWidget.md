## `FITAWebWidget` class reference

#### `public class FITAWebWidget`

#### `public boolean load()`

Begin loading the HTML widget container page.

 * **Returns:** `false` if the container is already being loaded

#### `public void create(String productSerial, WidgetOptions options)`

Create the widget with a product serial and widget options. Must be called after the `FITAWebWidgetHandler.onWebWidgetReady` callback.

 * **Parameters:**
   * `productSerial` — (nullable) The serial ID of the product to load at beginning. When `null` no product will be loaded.
   * `options` — Initial set of options for the widget as WidgetOptions object.

#### `public void create(String productSerial)`

Create the widget with a product serial. For details see above.

 * **Parameters:** `productSerial` — (nullable) The serial ID of the product to load at beginning. When `null` no product will be loaded.

#### `public void create(String productSerial, JSONObject options)`

Create the widget with a product serial and widget options as JSONObject. For details see above.

 * **Parameters:**
   * `productSerial` — (nullable) The serial ID of the product to load at beginning. When `null` no product will be loaded.
   * `options` — Initial set of options for the widget as JSONObject object.

#### `public void open(String productSerial, WidgetOptions options)`

Show the actual widget. When the widget finishes opening, it will call the `FITAWebWidgetHandler.onWebWidgetOpen` callback. Allows passing new product serial and/or widget options object.

 * **Parameters:**
   * `productSerial` — (nullable) The new product serial
   * `options` — Additional widget options as WidgetOptions

#### `public void open(String productSerial)`

Open the widget with a new product serial. For details see above.

 * **Parameters:** `productSerial` — (nullable) the new product serial

#### `public void open(String productSerial, JSONObject options)`

Open the widget with a new product serial and/or widget object (as JSONObejct). For details see above.

 * **Parameters:**
   * `productSerial` — (nullable) the new product serial
   * `options` — Additional widget options as JSONObject

#### `public void reconfigure(String productSerial, WidgetOptions options)`

Reconfigure the widget with the new product serial and/or widget options object. The new product serial triggers a loading of new product.

 * **Parameters:**
   * `productSerial` — (nullable) the new product serial
   * `options` — widget options as WidgetOptions

#### `public void reconfigure(String productSerial)`

Reconfigure the widget with a new product serial.

 * **Parameters:** `productSerial` — (nullable) the new product serial

#### `public void reconfigure(String productSerial, JSONObject options)`

Reconfigure the widget with the new product serial and/or widget options object (as JSONObject).

 * **Parameters:**
   * `productSerial` — (nullable) the new product serial
   * `options` — widget options as JSONObject

#### `public void close()`

Close the widget. Removes the widget markup and event handlers, potentially freeing up some memory.

#### `public void recommend(String productSerial, WidgetOptions options)`

Request a recommendation with productSerial and widget options. The recommended size will be returned as an argument to the `FITAWebWidgetHandler.onWebWidgetRecommend` callback.

 * **Parameters:**
   * `productSerial` — (nullable) The new product serial
   * `options` — Additional widget options

#### `public void recommend(String productSerial)`

Request a recommendation with productSerial.

 * **Parameters:**
   * `productSerial` — (nullable) The new product serial
   * `options` — Additional widget options

#### `public void recommend(String productSerial, JSONObject options)`

Request a recommendation with productSerial and widget options (as JSONObject).

 * **Parameters:**
   * `productSerial` — (nullable) The new product serial
   * `options` — Additional widget options as JSONObject

#### `private JSONArray createWidgetArguments(String productSerial, JSONObject options) throws JSONException`

Prepares the `options` argument that's expected by several widget methods by merging `productSerial` (if present) into the `options` dictionary (if present).

 * **Parameters:**
   * `productSerial` — (optional) Product serial
   * `options` — (optional) Additional widget options
 * **Returns:** Resulting merged JSONObject
