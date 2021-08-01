package com.project.autotrade.autotrade;

import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import cz.msebera.android.httpclient.util.EntityUtils;

public class GetJson {

    private static final String TAG = "Main";
    public static String coinName; // share coin name with AutoTrade.class
    private static ArrayList<HashMap<String, String>> tradingTopTenCoin;
    private static ArrayList<HashMap<String, String>> recentVolumeTenCoin;

    public String getCurrentPrice(String data) {

        try {
            JSONArray jsonArray = new JSONArray(data);

            JSONObject jsonObject = jsonArray.getJSONObject(0);

            String orderbook_units = jsonObject.get("orderbook_units").toString();

            JSONArray arrayUnits = new JSONArray(orderbook_units);

            ArrayList<Double> askPriceList = new ArrayList<>();
            ArrayList<Double> bidPriceList = new ArrayList<>();

            for (int i = 0; i < arrayUnits.length(); i++) {

                JSONObject objectUnits = arrayUnits.getJSONObject(i);

                String ask_price = objectUnits.get("ask_price").toString();
                String bid_price = objectUnits.get("bid_price").toString();

                //1000자리 콤마 처리
                askPriceList.add(Double.parseDouble(ask_price));
                bidPriceList.add(Double.parseDouble(bid_price));
            }

            NumberFormat format = NumberFormat.getInstance();
            format.setGroupingUsed(false);

            return format.format((askPriceList.get(0) + bidPriceList.get(0)) / 2);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    } // getCurrentPrice

    public HashMap<String, Double> getTickerData(String data) {

        try {
            JSONArray jsonArray = new JSONArray(data);

            ArrayList<String> priceList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String opening_price = jsonObject.get("opening_price").toString();
                String high_price = jsonObject.get("high_price").toString();
                String low_price = jsonObject.get("low_price").toString();
                String trade_price = jsonObject.get("trade_price").toString();

                priceList.add(opening_price);
                priceList.add(high_price);
                priceList.add(low_price);
                priceList.add(trade_price);
            }

            double openingPrice = Double.parseDouble(priceList.get(0));
            double highPrice = Double.parseDouble(priceList.get(1));
            double lowPrice = Double.parseDouble(priceList.get(2));
            double tradePrice = Double.parseDouble(priceList.get(3));

            HashMap<String, Double> map = new HashMap<String, Double>();
            map.put("openingPrice", openingPrice);
            map.put("highPrice", highPrice);
            map.put("lowPrice", lowPrice);
            map.put("tradePrice", tradePrice);

            return map;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    } //getTickerData

    public String getBalance(String coinNm) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        try {
            // http client
            Client client = new Client();

            String data = EntityUtils.toString(client.getEntity());

            JSONArray jsonArray = new JSONArray(data);

            String currency;
            String balance = null;

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                currency = jsonObject.get("currency").toString();

                if (currency.equals(coinNm)) {
                    balance = jsonObject.get("balance").toString();
                    break;
                }
            }
            return balance;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    } // getBalance

    // get balances in my accounts
    public void getAccounts() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        try {
            // http client
            Client client = new Client();

            String data = EntityUtils.toString(client.getEntity());

            String currency = "";
            String balance = "";
            String locked = "";
            String avg_buy_price = "";

            JSONArray jsonArray = new JSONArray(data);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                currency = jsonObject.get("currency").toString();
                balance = jsonObject.get("balance").toString();
                locked = jsonObject.get("locked").toString();
                avg_buy_price = jsonObject.get("avg_buy_price").toString();

                Log.d(TAG, "Currency name: " + currency);
                Log.d(TAG, "Balance: " + balance);
                Log.d(TAG, "Locked Balance: " + locked);
                Log.d(TAG, "Average buying price : " + avg_buy_price);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    // get all coin names in upbit
    public ArrayList<String> getAllCoinNm() throws IOException, NoSuchAlgorithmException, JSONException, InterruptedException {

        System.out.println("getAllCoinNm()");
        // http
        Client client = new Client();

        // receive data
        String data = EntityUtils.toString(client.getAllCoins());

        JSONArray jsonArray = new JSONArray(data);

        ArrayList<String> allCoinNmList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String market = jsonObject.get("market").toString();

            // only get coins that is named "KRW-"
            if (market.contains("KRW")) allCoinNmList.add(market);

        }
        //for (String a : allCoinNmList) System.out.println(a);
        return allCoinNmList;
    } // getAllCoinNm

    // the top ten coins that have the highest trade price on 24 hours
    public void getTopTenCoin() throws InterruptedException, JSONException, IOException, NoSuchAlgorithmException {

        System.out.println("getTopTenCoin()");

        // get datas
        ArrayList<String> allCoinNmList = getAllCoinNm();

        // input datas
        ArrayList<HashMap<String, String>> notOrderedAllCoin = new ArrayList<HashMap<String, String>>();

        // http
        Client client = new Client();

        // store the trade price of 24 hours of each coins to hash map and arraylist
        // hashMap(coin name, trade price 24)
        // arraylist(hashMap)
        for (int i = 0; i < allCoinNmList.size(); i++) {
            String coinNm = allCoinNmList.get(i);
            Thread.sleep(30);
            JSONArray jsonArray = new JSONArray(EntityUtils.toString(client.getTickerData(coinNm)));

            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String tradePrice24 = jsonObject.get("acc_trade_price_24h").toString();

            BigDecimal trade_price_24 = new BigDecimal(Double.parseDouble(tradePrice24));
            notOrderedAllCoin.add(setHashMap(coinNm, trade_price_24.toString(), null));

            Thread.sleep(30); // to receive datas without error
        }

        // sort hashMap in descending order
        Collections.sort(notOrderedAllCoin, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> hm1, HashMap<String, String> hm2) {
                Double price1 = Double.parseDouble(hm1.get("tradePrice24"));
                Double price2 = Double.parseDouble(hm2.get("tradePrice24"));
                return price2.compareTo(price1);
            }
        });

        // the top ten coins that have the highest trade price on 24 hours
        tradingTopTenCoin = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < 10; i++) {
            tradingTopTenCoin.add(notOrderedAllCoin.get(i));
            System.out.println(tradingTopTenCoin.get(i));
        }

    } // getTopTenCoin

    // used in getTopTenCoin function
    HashMap<String, String> setHashMap(String coinNm, String tradePrice24, String changeRate) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("coinNm", coinNm);
        if (tradePrice24 != null) hashMap.put("tradePrice24", tradePrice24);
        if (changeRate != null) hashMap.put("changeRate", changeRate);

        return hashMap;
    } // setHashMap

    public ArrayList<HashMap<String, String>> getRecentTradeVolume() throws JSONException, IOException, NoSuchAlgorithmException, InterruptedException {

        System.out.println("getRecentTradeVolume()");

        // new arraylist for newTopTenCoins list
        ArrayList<HashMap<String, String>> newTopTenList = new ArrayList<HashMap<String, String>>();

        // http
        Client client = new Client();

        for (int i = 0; i < tradingTopTenCoin.size(); i++) {
            String coinNm = tradingTopTenCoin.get(i).get("coinNm");
            JSONArray jsonArray = new JSONArray(EntityUtils.toString(client.getTickerData(coinNm)));
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String changeRate = jsonObject.get("signed_change_rate").toString();
            newTopTenList.add(setHashMap(coinNm, null, changeRate));
            Thread.sleep(30); // to receive datas without error
        }

        // sort hashMap in descending order
        Collections.sort(newTopTenList, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> hm1, HashMap<String, String> hm2) {
                Double rate1 = Double.parseDouble(hm1.get("changeRate"));
                Double rate2 = Double.parseDouble(hm2.get("changeRate"));
                return rate2.compareTo(rate1);
            }
        });

        recentVolumeTenCoin = newTopTenList;
        return newTopTenList;
    } // recentTradeVolume

    public HashMap<String, Double> getFinalCoin(int minute) throws IOException, NoSuchAlgorithmException, JSONException, InterruptedException {

        System.out.println("getFinalCoin()");

        // get data
        ArrayList<HashMap<String, String>> newTopTenList = getRecentTradeVolume();
        for (HashMap<String, String> temp : newTopTenList) System.out.println(temp);

        // http
        Client client = new Client();

        while (true) {
            for (int i = 0; i < newTopTenList.size(); i++) {
                String coinNm = newTopTenList.get(i).get("coinNm");

                JSONArray jsonArray = new JSONArray(EntityUtils.toString(client.getCandleData(coinNm, minute)));
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                String opening_price = jsonObject.get("opening_price").toString();
                String trade_price = jsonObject.get("trade_price").toString();
                Double openingPrice = Double.parseDouble(opening_price);
                Double tradePrice = Double.parseDouble(trade_price);

                System.out.println(openingPrice + "\t" + tradePrice);

                HashMap<String, Double> map = new HashMap<>();
                map.put("openingPrice", openingPrice);
                map.put("tradePrice", tradePrice);

                Thread.sleep(30);

                if (tradePrice > openingPrice) {
                    coinName = coinNm;
                    return map;
                }
                else continue;
            }
        }
    }

    public String getCandleStartTime(String coinName, int minute) throws InterruptedException, NoSuchAlgorithmException, JSONException, IOException {
        Client client = new Client();

        String data = EntityUtils.toString(client.getCandleData(coinName, minute));
        JSONArray jsonArray = new JSONArray(data);
        JSONObject jsonObject = jsonArray.getJSONObject(0);

        String candleStartTime = jsonObject.get("candle_date_time_kst").toString();
        return candleStartTime;
    }

    public String getTradePrice(String coinNm) throws IOException, NoSuchAlgorithmException, JSONException {
        Client client = new Client();

        String data = EntityUtils.toString(client.getTickerData(coinNm));
        JSONArray jsonArray = new JSONArray(data);
        JSONObject jsonObject = jsonArray.getJSONObject(0);

        String tradePrice = jsonObject.get("trade_price").toString();
        return tradePrice;
    }

    public ArrayList<HashMap<String, String>> getTradePrice(ArrayList<HashMap<String, String>> tradePriceList) throws JSONException, NoSuchAlgorithmException, IOException, InterruptedException {

        for (int i = 0; i < tradingTopTenCoin.size(); i++) {
            String coinNm = tradingTopTenCoin.get(i).get("coinNm");
            String tradePrice = getTradePrice(coinNm);

            HashMap<String, String> map = new HashMap<>();
            map.put("coinNm", coinNm);
            map.put("tradePrice", tradePrice);
            tradePriceList.add(map); // arraylist
        }

        return tradePriceList;
    }

    public String getNewFinalCoin(ArrayList<HashMap<String, String>> tradePriceList) throws JSONException, NoSuchAlgorithmException, IOException, InterruptedException {

        // 1. get ten coins list --> tradingTopTenCoin arraylist<hashmap>
        // 2. get each trade price and store to one hashmap & repeat 5 times
        // 3. get the five trade price of each coin on the arraylist
        // 4. compare those prices and calculate the percent of 'up'
        // 5. store the percent to new arraylist with hashmap
        ArrayList<HashMap<String, Object>> percentList = new ArrayList<>();
        String finalCoinNm;

        for (int count = 0; count < 10; count++) { // ten coin loop

            int up = 0;
            double preTradePrice = 0;

            for (int i = count; i < tradePriceList.size(); i += 10) { // one coin loop

                double tradePrice = Double.parseDouble(tradePriceList.get(i).get("tradePrice"));

                if (preTradePrice != 0 && preTradePrice < tradePrice) up++;
                preTradePrice = tradePrice;
            }

            HashMap<String, Object> newMap = new HashMap<>();
            newMap.put("coinNm", tradePriceList.get(count).get("coinNm"));
            newMap.put("upCount", up);
            percentList.add(newMap);
        }

        // 6. sort the arraylist in descending order
        Collections.sort(percentList, new Comparator<HashMap<String, Object>>() {
            @Override
            public int compare(HashMap<String, Object> map1, HashMap<String, Object> map2) {
                Integer upCount1 = (Integer) map1.get("upCount");
                Integer upCount2 = (Integer) map2.get("upCount");
                return upCount2.compareTo(upCount1);
            }
        });

        for (HashMap<String, Object> map : percentList)
            System.out.println(map);

        // 7. if there are more than two coins that has the same percent,
        //    check which the coin index is faster in the top ten coin arraylist
        double firstIndexPercent = (Integer) percentList.get(0).get("upCount");
        System.out.println("firstIndexPercent" + firstIndexPercent);

        int newIndex = 0;
        for (int i = 0; i < percentList.size(); i++) {
            if (firstIndexPercent == (Integer) percentList.get(i).get("upCount"))
                newIndex = i;
        }

        if (newIndex == 0) {
            finalCoinNm = (String) percentList.get(0).get("coinNm");
        }
        else {
            // get the final coin's index in recentVolumeTenCoin
            int finalIndex = recentVolumeTenCoin.size();

            for (int i = 0; i < newIndex; i++) {
                for (int index = 0; index < recentVolumeTenCoin.size(); index++) {
                    if (percentList.get(i).get("coinNm").equals(recentVolumeTenCoin.get(index).get("coinNm"))) {
                        if (finalIndex > index) finalIndex = index;
                    }
                }
            }
            finalCoinNm = recentVolumeTenCoin.get(finalIndex).get("coinNm");
            System.out.println("so, finalCoinNm is" + finalCoinNm);
        }

        return finalCoinNm;
    }
}
