# `ManufacturedSize` Documentation

## `public class ManufacturedSize`

Class that wraps together the code of the size and its availability status. Manufactured sizes are supposed to mean ALL sizes the product is manufactured in, as opposed to only available sizes (which means only sizes that are currently available).

## `public ManufacturedSize setSize(String size)`

Set the size code of the manufactured size.

 * **Parameters:** `size` — Size code string
 * **Returns:** Instance reference for chaining.

## `public JSONObject toJSON() throws JSONException`

Converts the instance into a single JSONObject. E.g. { "S": true }

 * **Returns:** [description]
 * **Exceptions:** `JSONException` — 
