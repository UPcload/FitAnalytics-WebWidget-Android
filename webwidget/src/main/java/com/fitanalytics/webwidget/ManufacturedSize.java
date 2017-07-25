package com.fitanalytics.webwidget;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that wraps together the code of the size and its availability status.
 * Manufactured sizes are supposed to mean ALL sizes the product is manufactured in,
 * as opposed to only available sizes (which means only sizes that are currently available).
 */
public class ManufacturedSize {
    protected String mSize = "";
    protected boolean mAvailable = false;

    public ManufacturedSize() {}

    public ManufacturedSize(String size, boolean available) {
        mSize = size;
        mAvailable = available;
    }
    /**
     * Set the size code of the manufactured size.
     * @param  size Size code string
     * @return      Instance reference for chaining.
     */
    public ManufacturedSize setSize(String size) {
        mSize = size;
        return this;
    }

    public ManufacturedSize setAvailable(boolean available) {
        mAvailable = available;
        return this;
    }

    /** 
     * Converts the instance into a single JSONObject. E.g. { "S": true }
     * @return [description]
     * @throws JSONException
     */
    public JSONObject toJSON() throws JSONException {
        JSONObject out = new JSONObject();
        out.put(mSize, mAvailable);
        return out;
    }
}
