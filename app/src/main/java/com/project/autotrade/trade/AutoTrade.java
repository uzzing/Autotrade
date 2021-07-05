package com.project.autotrade.trade;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import cz.msebera.android.httpclient.util.EntityUtils;

public class AutoTrade {

    private static final String TAG = "Main";
    GetJson getJson = new GetJson();

    public void autoTradeFiveMinute() throws InterruptedException, NoSuchAlgorithmException, JSONException, IOException {

        String date = getJson.getCandleStartTime(5);
        LocalDateTime sellTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME).plusMinutes(5);
        String uuid = "";
        HashMap<String, Object> buyData = null;

        // print
        System.out.println("which coin?? " + GetJson.coinName);
        System.out.println("candle start time: " + date);

        try {
                // buy
                Log.d(TAG, "buy");

                String strKrw = new GetJson().getBalance("KRW");
                double krw = Double.parseDouble(strKrw);
                System.out.println("the balance : " + krw);

                if (krw > 5000) {
                    // 시장가로 구매
                    String buy_data = buyMarketOrder(GetJson.coinName, krw * 0.9995);
                    buyData = getBuyData(buy_data);
                }

            // for selling earlier
            ArrayList<Double> priceList = new ArrayList<>();

            // sell
            while (true) {
                // get date time
                LocalDateTime now = LocalDateTime.now().withNano(0);
                System.out.println("now : " + now); // unfix
                System.out.println("selltime : " + sellTime); // fix

                // get Balance
                String strCurrencyBalance = new GetJson().getBalance(GetJson.coinName.substring(4));
                double currencyBalance = Double.parseDouble(strCurrencyBalance);
                // get trade price
                String strTradePrice = new GetJson().getTradePrice(GetJson.coinName);
                double tradePrice = Double.parseDouble(strTradePrice);

                // sell condition 1: tradePrice <= buyPrice * 0.995
                try {
                    double buyPrice = krw / currencyBalance;

                    System.out.println("tradePrice : " + tradePrice);
                    System.out.println("buyPrice : " + buyPrice);

                    if (tradePrice <= buyPrice * 0.99) { // tradePrice is parameter
                        sellMarketOrder(GetJson.coinName, currencyBalance * 0.9995);
                        System.out.println("손절 bye bye");
                        Thread.sleep(1000); // take a break
                    }
                } catch (NullPointerException e) {
                    System.out.println("currencyBalance is null");
                }

                // sell condition 2 : sellTime - 30second < now < sellTime (30-59 seconds)
                // 가장 높은 값을 찾아내고 다시 그 값이 다시 되면 팔기
                if (now.isAfter(sellTime.minusSeconds(30)) && now.isBefore(sellTime)) {

                    System.out.println(sellTime.minusSeconds(30));

                    priceList.add(tradePrice); // add data every while loop

                    System.out.println("before sorting" + priceList);

                    double maxPrice = 0.0;
                    if (priceList.size() == 15) { // tradePrice in 30 ~ 40 seconds
                        Collections.sort(priceList, Collections.reverseOrder());

                        maxPrice = priceList.get(0);
                        System.out.println("after sorting" + priceList);
                    }
                    System.out.println("maxPrice : " + maxPrice);

                    if (maxPrice == tradePrice) { // 40 ~ 59 seconds
                        sellMarketOrder(GetJson.coinName, currencyBalance * 0.9995);
                        // and don't break
                    }
                }

                // sell condition 3 : now >= sellTime
                try {
                    if (now.equals(sellTime) || now.isAfter(sellTime)) {

                        System.out.println(GetJson.coinName);
                        Log.d(TAG, "sell");

                        if (currencyBalance > 0.00008)
                            sellMarketOrder(GetJson.coinName, currencyBalance * 0.9995);

                        Thread.sleep(1000); // take one second
                        break;
                    }
                } catch (NullPointerException e) {
                    break;
                    /* if the balance is null because currency sold when it was condition2,
                   the balance become null and come here */
                }
                Thread.sleep(1000); // take one second -> plue one second to now
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    } // autoTradeFiveMinutes

    public void autoTradeOneMinute() throws InterruptedException, NoSuchAlgorithmException, JSONException, IOException {
        System.out.println(GetJson.coinName);
        System.out.println(getJson.getCandleStartTime(1));

        String date = getJson.getCandleStartTime(1);
        LocalDateTime sellTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME).plusMinutes(1);
        String uuid = "";
        HashMap<String, Object> buyData = null;

        try {
            // buy
            Log.d(TAG, "buy");

            String strKrw = new GetJson().getBalance("KRW");
            double krw = Double.parseDouble(strKrw);
            System.out.println("현재 현금 잔고 : " + krw);

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

            // sell
            while (true) {
                LocalDateTime now = LocalDateTime.now().withNano(0);
                System.out.println("now : " + now); // unfix
                System.out.println("selltime : " + sellTime); // fix

                // condition 1: tradePrice <= buyPrice * 0.995
                try {
                    String strCurrencyBalance = new GetJson().getBalance(GetJson.coinName.substring(4));
                    double currencyBalance = Double.parseDouble(strCurrencyBalance);
                    double buyPrice = krw / currencyBalance;
                    System.out.println("산 코인 개수: " + currencyBalance);
                    System.out.println("그래서 buyPrice는 : " + buyPrice);

                    String strTradePrice = new GetJson().getTradePrice(GetJson.coinName);
                    double tradePrice = Double.parseDouble(strTradePrice);

                    System.out.println("tradePrice : " + tradePrice);
                    System.out.println("buyPrice : " + buyPrice);

                    if (tradePrice <= buyPrice * 0.995) { // tradePrice is parameter
                        sellMarketOrder(GetJson.coinName, currencyBalance * 0.9995);
                        System.out.println("손절 bye bye");
                        Thread.sleep(1000); // take a break
                        break;
                    }

                } catch (NullPointerException e) {
                    System.out.println("currencyBalance is null");
                }


                // condition 2 : now >= sellTime
                if (now.equals(sellTime) || now.isAfter(sellTime)) {

                    System.out.println(GetJson.coinName);
                    Log.d(TAG, "sell");

                    String strCurrencyBalance = new GetJson().getBalance(GetJson.coinName.substring(4));

                    if (strCurrencyBalance == null) {
                        deleteOrder(uuid);
                        break;
                    } else {
                        double currencyBalance = Double.parseDouble(strCurrencyBalance);
                        if (currencyBalance > 0.00008)
                            sellMarketOrder(GetJson.coinName, currencyBalance * 0.9995);
                        Thread.sleep(1000); // take a break
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
        LocalDateTime startTime = LocalDateTime.now().with(LocalTime.of(23, 0, 0));
        LocalDateTime endTime = LocalDateTime.now().with(LocalTime.of(23, 41, 0));

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
