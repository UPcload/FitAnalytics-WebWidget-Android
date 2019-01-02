package com.fitanalytics.webwidget;

public class FITAPurchaseReport {

    // mandatory field
    private String productSerial;
    // mandatory field
    private String orderId;
    private Double price;
    private String currency;

    private String userId;
    private String purchasedSize;

    private String shopArticleCode;

    private String sizeRegion;
    private String shop;
    private String shopCountry;
    /***
     * your shop has language-specific versions, you can specify the language in which the purchase was made
     * (which helps identify the user's sizing system)
     *
     */
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
    // TODO why is this per report? it will be set anyway to "embed-android" in FitPurchasereporter.
    private String hostname;

    public FITAPurchaseReport(String productSerial, String orderId, Double price, String currency) {
        this.currency = currency;
        this.price = price;
        this.orderId = orderId;
        this.productSerial = productSerial;
    }


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
