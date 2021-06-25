package com.project.autotrade;

import android.util.Log;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;

import cz.msebera.android.httpclient.util.EntityUtils;

public class AutoTrade {

    private static final String TAG = "Main";

    public void autotrade(String coinNm, String currentPrice, String targetPrice, LocalDateTime now) throws IOException, NoSuchAlgorithmException, InterruptedException {

//        LocalDateTime startTime = LocalDateTime.now().with(LocalTime.of(9, 0, 0));
//        LocalDateTime endTime = startTime.plusDays(1).with(LocalTime.of(8, 59, 50));
        LocalDateTime startTime = LocalDateTime.now().with(LocalTime.of(0, 0, 0));
        LocalDateTime endTime = LocalDateTime.now().with(LocalTime.of(1, 47, 0));

        System.out.println(coinNm);
        System.out.println(now);
        System.out.println(currentPrice);
        System.out.println(targetPrice);

        try {
            if (startTime.isBefore(now) && endTime.isAfter(now)) {
                if (Integer.parseInt(targetPrice) < Integer.parseInt(currentPrice)) {
                    Log.d(TAG, "buy");

                    String strKrw = new GetJson().getBalance("KRW");
                    double krw = Double.parseDouble(strKrw);

                    if (krw > 5000)
                        buyMarketOrder("KRW-" + coinNm, krw * 0.9995);
                }
            }
            else {
                Log.d(TAG, "sell");

                String strCurrencyBalance = new GetJson().getBalance(coinNm);
                double currencyBalance = Double.parseDouble(strCurrencyBalance);

                if (currencyBalance > 0.00008)
                    sellMarketOrder("KRW-" + coinNm, currencyBalance * 0.9995);

                MainActivity.task.cancel(true);
            }
        } catch(NumberFormatException e){
            e.printStackTrace();
        } catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    public void buyMarketOrder(String coinNm, double price) throws
            IOException, NoSuchAlgorithmException {
        HashMap<String, String> params = new HashMap<>();
        params.put("market", coinNm);
        params.put("side", "bid"); // buy
        params.put("price", Double.toString(price));
        params.put("ord_type", "price");

        Client client = new Client();
        String data = EntityUtils.toString(client.postEntity(params));
        Log.d(TAG, data);
    }

    public void sellMarketOrder(String coinNm, double price) throws
            IOException, NoSuchAlgorithmException {
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
