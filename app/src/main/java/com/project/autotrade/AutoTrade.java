package com.project.autotrade;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

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

    public static double currentPrice;

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

    private void getOrderBooks(String coinNm) {

        //final String sCoinNm = coinNm;
        final String sCoinNm = "XRP";

        String url = "https://api.upbit.com/v1/orderbook?markets=KRW-" + sCoinNm;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        currentPrice = new GetJson().getCurrentPrice(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getMessage());
                    }
                }){
        };
        request.setShouldCache(false);
        requestQueue.add(request);
    } //getOrderBooks

    public void autotrade() throws IOException, NoSuchAlgorithmException {
        LocalDateTime now = LocalDateTime.now().withNano(0); // remove miliseconds ("HH:ss:mm")
        LocalDateTime startTime = LocalDateTime.now().with(LocalTime.of(9,0,0));
        LocalDateTime endTime = startTime.plusDays(1).with(LocalTime.of(8,59,50));

        if (!(startTime.isBefore(now) && endTime.isAfter(now))) {
            double targetPrice = getTargetPrice();
            //double currentPrice = getCurrentPrice();
            System.out.println(targetPrice);
            System.out.println(currentPrice);

            if (targetPrice < currentPrice) {
                //double krw = new getJson().getBalance("KRW");
                System.out.println("buy");
                double krw = 6000;
                if (krw > 5000) buyMarketOrder("KRW-BTC", krw * 0.9995);
            }
            else {
                System.out.println("sell");
                double currencyBalance = new GetJson().getBalance("KRW-BTC");
                if (currencyBalance > 0.00008) sellMarketOrder("KRW-BTC", currencyBalance * 0.9995);
            }
        }
    }

    public double getTargetPrice() {
        return tradePrice + (highPrice - lowPrice) * 0.1;
    }

    public double getCurrentPrice(double askPrice, double bidPrice) {
        return (askPrice + bidPrice) / 2;
    }

    public void buyMarketOrder(String coinNm, double price) throws IOException, NoSuchAlgorithmException {
        HashMap<String, String> params = new HashMap<>();
        params.put("market", coinNm);
        params.put("side", "bid"); // buy
        params.put("price", Double.toString(price));
        params.put("ord_type", "price");

        Client client = new Client();
        String data = EntityUtils.toString(client.postEntity(params));
        Log.d(TAG, data);
    }

    public void sellMarketOrder(String coinNm, double price) throws IOException, NoSuchAlgorithmException {
        HashMap<String, String> params = new HashMap<>();
        params.put("market", coinNm);
        params.put("side", "ask"); // sell
        params.put("volume", Double.toString(price));
        params.put("ord_type", "market");

        Client client = new Client();
        String data = EntityUtils.toString(client.postEntity(params));
        Log.d(TAG, data);
    }
}
