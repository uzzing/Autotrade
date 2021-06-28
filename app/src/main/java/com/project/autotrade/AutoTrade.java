package com.project.autotrade;

import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import cz.msebera.android.httpclient.util.EntityUtils;

public class AutoTrade {

    private static final String TAG = "Main";
    GetJson getJson = new GetJson();

    public void autoTradeOneMinute(LocalDateTime now) throws InterruptedException, NoSuchAlgorithmException, JSONException, IOException {
        System.out.println(GetJson.coinName);
        System.out.println(getJson.getCandleStartTime());
        //System.out.println(getJson.getFinalCoin());


        String date = getJson.getCandleStartTime();
        LocalDateTime sellTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME).plusMinutes(1);
        System.out.println(sellTime);

        if (now == sellTime) MainActivity.task2.cancel(true);

        // now == "candle_date_time_kst" + 1 --> sell
//        try {
//            // buy
//            Log.d(TAG, "buy");
//
//            String strKrw = new GetJson().getBalance("KRW");
//            double krw = Double.parseDouble(strKrw);
//
//            if (krw > 5000) {
//                double openingPrice = getJson.getFinalCoin();
//                double volumn = krw / openingPrice * 0.9;
//                System.out.println(openingPrice + " " + volumn);
//                buyOpeningPriceOrder(GetJson.coinName, openingPrice, volumn);
//            }
//
//            // sell
//            if (now == sellTime) {
//                Log.d(TAG, "sell");
//
//                String strCurrencyBalance = new GetJson().getBalance(GetJson.coinName);
//                double currencyBalance = Double.parseDouble(strCurrencyBalance);
//
//                if (currencyBalance > 0.00008)
//                    sellMarketOrder(GetJson.coinName, currencyBalance * 0.9995);
//
//                MainActivity.task2.cancel(true);
//            }
//
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }

    }

    public void autoTrade(String coinNm, String currentPrice, String targetPrice, LocalDateTime now) throws IOException, NoSuchAlgorithmException, InterruptedException {

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
            } else {
                Log.d(TAG, "sell");

                String strCurrencyBalance = new GetJson().getBalance(coinNm);
                double currencyBalance = Double.parseDouble(strCurrencyBalance);

                if (currencyBalance > 0.00008)
                    sellMarketOrder("KRW-" + coinNm, currencyBalance * 0.9995);

                MainActivity.task.cancel(true);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
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

    public void sellMarketOrder(String coinNm, double volume) throws IOException, NoSuchAlgorithmException {
        HashMap<String, String> params = new HashMap<>();
        params.put("market", coinNm);
        params.put("side", "ask"); // sell
        params.put("volume", Double.toString(volume));
        params.put("ord_type", "market");

        Client client = new Client();
        String data = EntityUtils.toString(client.postEntity(params));
        Log.d(TAG, data);
    }
}
