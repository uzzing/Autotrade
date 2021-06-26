package com.project.autotrade;

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

    public String getTargetPrice(String data) {

        try {
            JSONArray jsonArray = new JSONArray(data);

            ArrayList<String> priceList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String sOpening_price = jsonObject.get("opening_price").toString();
                String sHigh_price = jsonObject.get("high_price").toString();//고가
                String sLow_price = jsonObject.get("low_price").toString();//저가

                priceList.add(sHigh_price);
                priceList.add(sLow_price);
                priceList.add(sOpening_price);
            }

            double highPrice = Double.parseDouble(priceList.get(0));
            double lowPrice = Double.parseDouble(priceList.get(1));
            double openingPrice = Double.parseDouble(priceList.get(2));

            NumberFormat format = NumberFormat.getInstance();
            format.setGroupingUsed(false);

            return format.format(openingPrice + (highPrice - lowPrice) * 0.3);

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

    // the top ten coins that have the highest trade price on 24 hours
    public void getTopTenCoin() {
        try {
            Client client = new Client();

            // get all coin names
            GetJson getJson = new GetJson();
            ArrayList<String> coinNm = getJson.getAllCoinNm();

            // to input datas
            ArrayList<HashMap<String, String>> allCoinList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> hashMap = new HashMap<>();

            // store the trade price of 24 hours of each coins to hash map and arraylist
            // hashMap(coin name, trade price 24)
            // arraylist(hashMap)
            for (int i = 0; i < coinNm.size(); i++) {
                JSONArray jsonArray = new JSONArray(EntityUtils.toString(client.getTickerData(coinNm.get(i))));

                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String tradePrice24 = jsonObject.get("acc_trade_price_24h").toString();

                BigDecimal convertedPrice = new BigDecimal(Double.parseDouble(tradePrice24));
                allCoinList.add(setHashMap(coinNm.get(i), convertedPrice.toString(), null));

                Thread.sleep(30); // to receive datas without error
            }
            System.out.println("\nEnd\n");

            // sort hashMap in descending order
            Collections.sort(allCoinList, new Comparator<HashMap<String, String>>() {
                @Override
                public int compare(HashMap<String, String> hm1, HashMap<String, String> hm2) {
                    Double price1 = Double.parseDouble(hm1.get("tradePrice24"));
                    Double price2 = Double.parseDouble(hm2.get("tradePrice24"));
                    return price2.compareTo(price1);
                }
            });

            // the top ten coins that have the highest trade price on 24 hours
            ArrayList<HashMap<String, String>> topTenCoins = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < 10; i++)
                topTenCoins.add(allCoinList.get(i));

            recentTradeVolume(topTenCoins);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    } // getTradePrice

    // used in getTopTenCoin function
    HashMap<String, String> setHashMap(String coinNm, String tradePrice24, String changeRate) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("coinNm", coinNm);
        if (tradePrice24 != null) hashMap.put("tradePrice24", tradePrice24);
        if (changeRate != null) hashMap.put("changeRate", changeRate);
        return hashMap;
    }

    // get all coin names in upbit
    public ArrayList<String> getAllCoinNm() {
        try {
            // http client
            Client client = new Client();

            // receive data
            String data = EntityUtils.toString(client.getAllCoins());

            JSONArray jsonArray = new JSONArray(data);

            ArrayList<String> coinNmList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String market = jsonObject.get("market").toString();

                // only get coins that is named "KRW-"
                if (market.contains("KRW")) coinNmList.add(market);
            }
            return coinNmList;

        } catch (IOException | JSONException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
