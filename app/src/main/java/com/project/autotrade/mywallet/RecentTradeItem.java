package com.project.autotrade.mywallet;

public class RecentTradeItem {

    private String coinName;
    private String order;
    private String price;
    private String volume;
    private String createdAt;

    public RecentTradeItem(String coinName, String order, String price, String volume, String createdAt) {
        this.coinName = coinName;
        this.order = order;
        this.price = price;
        this.volume = volume;
        this.createdAt = createdAt;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
