package com.project.autotrade;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public void autoTradeOneMinute() throws InterruptedException, NoSuchAlgorithmException, JSONException, IOException {
        System.out.println(GetJson.coinName);
        System.out.println(getJson.getCandleStartTime());
        //System.out.println(getJson.getFinalCoin());

        String date = getJson.getCandleStartTime();
        LocalDateTime sellTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME).plusMinutes(1);
        String uuid = "";
        HashMap<String, Object> buyData = null;

        // now == "candle_date_time_kst" + 1 --> sell
        try {
            // buy
            Log.d(TAG, "buy");

            String strKrw = new GetJson().getBalance("KRW");
            double krw = Double.parseDouble(strKrw);

            if (krw > 5000) {
                // 시장가로 구매
                String buy_data = buyMarketOrder(GetJson.coinName, krw * 0.9995);
                buyData = getBuyData(buy_data);

                // 시가로 구매
//                double openingPrice = getJson.getFinalCoin().get("openingPrice");
//                double volumn = krw / openingPrice * 0.9;
//                System.out.println(openingPrice + " " + volumn);
//
//                String buy_data = buyOpeningPriceOrder(GetJson.coinName, openingPrice, volumn);
//                buyData = getBuyData(buy_data); // hashmap
            }

            while (true) {
                LocalDateTime now = LocalDateTime.now().withNano(0);
                System.out.println("now : " + now); // unfix
                System.out.println("selltime : " + sellTime); // fix

                // sell condition 1: tradePrice == buyPrice * 0.9995
                try {
                    String strCurrencyBalance = new GetJson().getBalance(GetJson.coinName.substring(4));
                    double currencyBalance = Double.parseDouble(strCurrencyBalance);

                    double buyPrice = krw / currencyBalance;
                    double tradePrice = getJson.getFinalCoin().get("tradePrice"); // should modify

                    System.out.println("tradePrice : " + tradePrice);
                    System.out.println("buyPrice : " + buyPrice);

                    if (tradePrice == buyPrice * 0.995) {
                        sellMarketOrder(GetJson.coinName, currencyBalance * 0.9995);
                        break;
                    }

                } catch (NullPointerException e) {
                    System.out.println("currencyBalance is null");
                    continue;
                }


                // sell condition 2 : now == sellTime
                if (now.equals(sellTime)) {

                    System.out.println(GetJson.coinName);
                    Log.d(TAG, "sell");

                    String strCurrencyBalance = new GetJson().getBalance(GetJson.coinName.substring(4));

                    if (strCurrencyBalance == null) {
                        deleteOrder(uuid);
                        break;
                    }
                    else {
                        double currencyBalance = Double.parseDouble(strCurrencyBalance);
                        if (currencyBalance > 0.00008)
                            sellMarketOrder(GetJson.coinName, currencyBalance * 0.9995);
                        break;
                    }
                }
                Thread.sleep(1000);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
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
        } catch (NumberFormatException | JSONException e) {
            e.printStackTrace();
        }
    }

    public String buyMarketOrder(String coinNm, double price) throws IOException, NoSuchAlgorithmException, JSONException {
        HashMap<String, String> params = new HashMap<>();
        params.put("market", coinNm);
        params.put("side", "bid"); // buy
        params.put("price", Double.toString(price));
        params.put("ord_type", "price");

        Client client = new Client();
        String data = EntityUtils.toString(client.postEntity(params));

        Log.d(TAG, data);
        return data;
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

    public String buyOpeningPriceOrder(String coinNm, double price, double volume) throws IOException, NoSuchAlgorithmException, JSONException, InterruptedException {
        HashMap<String, String> params = new HashMap<>();
        params.put("market", coinNm);
        params.put("side", "bid"); // buy
        params.put("volume", Double.toString(volume));
        params.put("price", Double.toString(price));
        params.put("ord_type", "limit");

        Client client = new Client();
        String data = EntityUtils.toString(client.postEntity(params));

        Log.d(TAG, data);

        return data;
    }

    public HashMap<String, Object> getBuyData(String data) throws InterruptedException, JSONException {
        Thread.sleep(30);
        JSONObject jsonObject = new JSONObject(data);
        String uuid = jsonObject.get("uuid").toString();
        String buy_price = jsonObject.get("price").toString();
        Double buyPrice = Double.parseDouble(buy_price);

        HashMap<String, Object> map = new HashMap<>();
        map.put("uuid", uuid);
        map.put("buyPrice", buyPrice);

        return map;
    }

    public void deleteOrder(String uuid) throws IOException, NoSuchAlgorithmException {
        HashMap<String, String> params = new HashMap<>();
        params.put("uuid", uuid);

        Client client = new Client();
        String data = EntityUtils.toString(client.deleteOrder(params));
        Log.d(TAG, data);
    }

}
