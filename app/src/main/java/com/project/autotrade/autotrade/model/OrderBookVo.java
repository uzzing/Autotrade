package com.project.autotrade.autotrade.model;

public class OrderBookVo {
    String price; //호가
    String size; //잔량

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}