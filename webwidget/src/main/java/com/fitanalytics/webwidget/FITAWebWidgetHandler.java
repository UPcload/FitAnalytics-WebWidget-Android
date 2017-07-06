package com.fitanalytics.webwidget;

import org.json.JSONObject;

public interface FITAWebWidgetHandler {
    /**
     * This method will be called when the widget container inside the WebView has successfully loaded
     * and is ready to accept commands.
     * @param widget The widget controller instance
     */
    public void onWebWidgetReady(FITAWebWidget widget);

    /**
     * This method will be called when widget instance inside the WebView has been successfully created.
     * @param widget The widget controller instance
     */
    public void onWebWidgetInit(FITAWebWidget widget);

    /**
     * This method will be called when widget inside the WebView has failed to load or initialize for some reason.
     * @param widget The widget controller instance
     */
    public void onWebWidgetLoadError(FITAWebWidget widget, String description);

    /**
     * This method will be called when widget successfully loaded the product info.
     * It means the product is supported and the widget should be able to provide
     * a size recommendation for it.
     * @param widget The widget controller instance
     * @param productId The ID of the product
     * @param details The details object
     */
    public void onWebWidgetProductLoad(FITAWebWidget widget, String productId, JSONObject details);

    /**
     * This method will be called when widget failed to load the product info or the product is not supported.
     * @param widget The widget controller instance
     * @param productId The ID of the product
     * @param details The details object
     */
    public void onWebWidgetProductLoadError(FITAWebWidget widget, String productId, JSONObject details);

    /**
     * This method will be called when widget has successfully opened after the `open` method call.
     * @param widget The widget controller instance
     * @param productId The ID of the product
     */
    public void onWebWidgetOpen(FITAWebWidget widget, String productId);

    /**
     * This method will be called when user of the widget has specifically requested closing of the widget by clicking on the close button.
     * @param widget The widget controller instance
     * @param productId The ID of the product
     * @param size The recommended size of the product, if there was a recommendation. `null` if there wasn't any recommendation.
     * @param details The details object.
     */
    public void onWebWidgetClose(FITAWebWidget widget, String productId, String size, JSONObject details);

    /**
     * This method will be called when user of the widget has specifically clicked on the add-to-cart inside the widget.
     * @param widget The widget controller instance
     * @param productId The ID of the product
     * @param size The size of the product that should be added to cart.
     * @param details The details object.
     */
    public void onWebWidgetAddToCart (FITAWebWidget widget, String productId, String size, JSONObject details);

    /**
     * This method will be called after the `FITAWebWidget.recommend` call, when the widget has received and processed the size recommendation.
     * @param productId The ID of the product
     * @param size The recommended size of the product.
     * @param details The details object.
     */
    public void onWebWidgetRecommend(FITAWebWidget widget, String productId, String size, JSONObject details);
}
