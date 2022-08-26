package com.fitanalytics.webwidget;

public class FITAPurchaseReport {

    // mandatory field
    private String productSerial;
    // mandatory field
    private String orderId;
    // mandatory field
    private Double price;
    // mandatory field
    private String currency;
    private String userId;
    private String purchasedSize;
    private String shopArticleCode;
    private String sizeRegion;
    private String shop;
    private String shopCountry;
    private String shopLanguage;
    private String shopSizingSystem;
    private String ean;
    private String funnel;

    public String getProductSerial() {
        return productSerial;
    }

    public void setProductSerial(String productSerial) {
        this.productSerial = productSerial;
    }

    private String sid;

    /**
     * A FITAPurchaseReport represents an order about a purchased item identified by the productSerial and the orderId
     * Use @see FITAPurchaseReporter.sendReport(FITAPurchaseReport report) to report to the Fit Analytics endpoints
     * @param productSerial
     * @param orderId
     */
    public FITAPurchaseReport(String productSerial, String orderId) {
        this(productSerial, orderId, null, null, null);
    }


    /**
     * A FITAPurchaseReport represents an order about a purchased item identified by the following parameters
     * @param productSerial
     * @param orderId
     * @param purchasedSize
     */
    public FITAPurchaseReport(String productSerial, String orderId, String purchasedSize) {
        this(productSerial, orderId, purchasedSize, null, null);
    }
        /**
         * A FITAPurchaseReport represents an order about a purchased item identified by the productSerial and the orderId
         * Use @see FITAPurchaseReporter.sendReport(FITAPurchaseReport report) to report to the Fit Analytics endpoints
         * @param productSerial
         * @param orderId
         * @param price
         * @param currency
         */
    public FITAPurchaseReport(String productSerial, String orderId, String purchasedSize , Double price, String currency) {
        this.currency = currency;
        this.price = price;
        this.purchasedSize = purchasedSize;
        this.orderId = orderId;
        this.productSerial = productSerial;
    }


    public String getOrderId() {
        return orderId;
    }

    public Double getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPurchasedSize() {
        return purchasedSize;
    }

    public void setPurchasedSize(String purchasedSize) {
        this.purchasedSize = purchasedSize;
    }

    public String getShopArticleCode() {
        return shopArticleCode;
    }

    public void setShopArticleCode(String shopArticleCode) {
        this.shopArticleCode = shopArticleCode;
    }

    public String getSizeRegion() {
        return sizeRegion;
    }

    public void setSizeRegion(String sizeRegion) {
        this.sizeRegion = sizeRegion;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getShopCountry() {
        return shopCountry;
    }

    public void setShopCountry(String shopCountry) {
        this.shopCountry = shopCountry;
    }

    public String getShopLanguage() {
        return shopLanguage;
    }

    public void setShopLanguage(String shopLanguage) {
        this.shopLanguage = shopLanguage;
    }

    public String getShopSizingSystem() {
        return shopSizingSystem;
    }

    public void setShopSizingSystem(String shopSizingSystem) {
        this.shopSizingSystem = shopSizingSystem;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getFunnel() {
        return funnel;
    }

    public void setFunnel(String funnel) {
        this.funnel = funnel;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    // TODO: shouldn't we create another prop for SSID?
}
