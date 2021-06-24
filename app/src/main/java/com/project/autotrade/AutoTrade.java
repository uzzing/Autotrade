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

    private static final String TAG = "Main";

    public void autotrade() throws IOException, NoSuchAlgorithmException {

        LocalDateTime now = LocalDateTime.now().withNano(0); // remove miliseconds ("HH:ss:mm")
        LocalDateTime startTime = LocalDateTime.now().with(LocalTime.of(9, 0, 0));
        LocalDateTime endTime = startTime.plusDays(1).with(LocalTime.of(8, 59, 50));

        if (!(startTime.isBefore(now) && endTime.isAfter(now))) {
            String targetPrice = GetJson.targetPrice;
            String currentPrice = GetJson.currentPrice;

            try {
                // test
                if (targetPrice == null && currentPrice == null) {
                    System.out.println("종료합니다");
                }

                if (Integer.parseInt(targetPrice) < Integer.parseInt(currentPrice)) {
                    Log.d(TAG, "buy");
                    //double krw = new getJson().getBalance("KRW");
                    //double krw = 6000;
                    //if (krw > 5000) buyMarketOrder("KRW-BTC", krw * 0.9995);
                } else {
                    Log.d(TAG, "sell");
                    double currencyBalance = new GetJson().getBalance("KRW-BTC");
                    if (currencyBalance > 0.00008)
                        sellMarketOrder("KRW-BTC", currencyBalance * 0.9995);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
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
