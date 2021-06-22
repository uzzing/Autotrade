package com.project.autotrade;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.RequestQueue;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;

import cz.msebera.android.httpclient.util.EntityUtils;

public class AutoTrade {
    private RequestQueue requestQueue;
    private static final String TAG = "Main";

    int lowPrice;
    int highPrice;
    int tradePrice;

    double askPrice;
    double bidPrice;

    AutoTrade() {};

    AutoTrade(String lowPrice, String highPrice, String tradePrice) {
        this.lowPrice = Integer.parseInt(lowPrice);
        this.highPrice = Integer.parseInt(highPrice);
        this.tradePrice = Integer.parseInt(tradePrice);
    }

    AutoTrade(double askPrice, double bidPrice) {
        this.askPrice = askPrice;
        this.bidPrice = bidPrice;
    }

    public void autotrade() throws IOException, NoSuchAlgorithmException {
        LocalDateTime now = LocalDateTime.now().withNano(0); // remove miliseconds ("HH:ss:mm")
        LocalDateTime startTime = LocalDateTime.now().with(LocalTime.of(9,0,0));
        LocalDateTime endTime = startTime.plusDays(1).with(LocalTime.of(8,59,50));

        if (startTime.isBefore(now) && endTime.isAfter(now)) {
            double targetPrice = getTargetPrice();
            double currentPrice = getCurrentPrice();

            if (targetPrice < currentPrice) {
                double krw = new getJson().getBalance("KRW");
                if (krw > 5000) buyMarketOrder("KRW-BTC", krw * 0.9995);
            }
            else {
                double btc = new getJson().getBalance("BTC");
                // if (btc > 0.00008)
            }
        }

    }

    private double getTargetPrice() {
        return tradePrice + (highPrice - lowPrice) * 0.3;
    }

    private double getCurrentPrice() {
        return (askPrice + bidPrice) / 2;
    }

    public void buyMarketOrder(String coinNm, double price) throws IOException, NoSuchAlgorithmException {
        HashMap<String, String> params = new HashMap<>();
        params.put("market", coinNm);
        params.put("side", "bid"); // buy
        params.put("price", "price");
        params.put("ord_type", "price");

        Client client = new Client();
        String data = EntityUtils.toString(client.postEntity(params));
        Log.d(TAG, data);
    }
}
