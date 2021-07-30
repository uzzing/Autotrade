package com.project.autotrade.trade;

import android.util.Log;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import cz.msebera.android.httpclient.util.EntityUtils;

public class AutoTrade {

    private static final String TAG = "Main";
    GetJson getJson = new GetJson();

    public static String buyUUID;
    public static String sellUUID;

    private DatabaseReference UUIDRef;

    private void sendBuyUUIDToDB(String date, String uuid) {
        UUIDRef = FirebaseDatabase.getInstance().getReference().child("UUID").child(date);
        UUIDRef.setValue(uuid);
    }

    public void newAutoTradeFiveMinute(String finalCoinNm) throws InterruptedException, NoSuchAlgorithmException, JSONException, IOException {

        // get time
        String date = getJson.getCandleStartTime(finalCoinNm, 5);
        LocalDateTime startTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime sellTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME).plusMinutes(5);
        HashMap<String, Object> orderData = null;


        // print
        System.out.println("Which coin?? " + finalCoinNm);
        System.out.println("candle start time: " + date);

        try {
            // buy
            Log.d(TAG, "buy");
            String strKrw = new GetJson().getBalance("KRW");
            double krw = Double.parseDouble(strKrw);
            System.out.println("the balance : " + krw);

            if (krw > 5000) {
                // 시장가로 구매
                String buyData = buyMarketOrder(finalCoinNm, krw * 0.9995);

                // get buy data
                orderData = getBuyOrderData(buyData);
                buyUUID = orderData.get("uuid").toString();
                sendBuyUUIDToDB(date, buyUUID);
            }

            Thread.sleep(1000);

            // for selling earlier
            ArrayList<Double> priceList = new ArrayList<>();

            double topPrice = 0.0; // for sell condition 2, 3
            double maxPrice = 0.0; // for sell condition 4

            // sell
            while (true) {
                try {
                    // get date time
                    LocalDateTime now = LocalDateTime.now().withNano(0);
                    System.out.println("now : " + now); // unfix
                    System.out.println("selltime : " + sellTime); // fix

                    // get Balance
                    String strCurrencyBalance = new GetJson().getBalance(finalCoinNm.substring(4));
                    double currencyBalance = Double.valueOf(String.valueOf(strCurrencyBalance));

                    // get trade price
                    String strTradePrice = new GetJson().getTradePrice(finalCoinNm);
                    double tradePrice = Double.valueOf(strTradePrice);
                    double buyPrice = 0;

                    // sell condition 1: tradePrice <= buyPrice * 0.99
                    try {
                        buyPrice = krw / currencyBalance;
                        System.out.println("tradePrice : " + tradePrice);
                        System.out.println("buyPrice : " + buyPrice);
                        if (tradePrice <= buyPrice * 0.993) {
                            String sellData = sellMarketOrder(finalCoinNm, currencyBalance);
                            orderData = getSellOrderData(sellData);
                            sellUUID = orderData.get("uuid").toString();
                            sendBuyUUIDToDB(now.toString(), sellUUID);
                            System.out.println("손절때문에 팔렸어요");
                            Thread.sleep(1000); // take a break
                        }
                    } catch (NullPointerException e) {
                        System.out.println("currencyBalance is null");
                    } // sell condition 1


                    // sell condition 2 : start time <= now <= 3 minutes
                    // compare tradePrice and topPrice
                    if (now.isAfter(startTime) && now.isBefore(startTime.plusMinutes(3))) {
                        if (tradePrice > topPrice) topPrice = tradePrice;
                    } else {
                        System.out.println("now is not before three minutes, Top price can't be set");
                    }

                    System.out.println("topPrice : " + topPrice);


                    // sell condition 3 : 3 minute <= now <= sellTime
                    if (now.isAfter(startTime.plusMinutes(3)) && now.isBefore(sellTime)) {
                        if (tradePrice <= topPrice * 0.997) {
                            if (tradePrice >= buyPrice) {
                                if (currencyBalance > 0.00008) {
                                    String sellData = sellMarketOrder(finalCoinNm, currencyBalance);
                                    orderData = getSellOrderData(sellData);
                                    sellUUID = orderData.get("uuid").toString();
                                    sendBuyUUIDToDB(now.toString(), sellUUID);
                                    System.out.println("현재가가 topPrice 0.007이라서 팔렸어요");
                                }
                            }
                        }
                    }


                    // sell condition 4 : sellTime - 30second < now < sellTime (30-59 seconds)
                    // 가장 높은 값을 찾아내고 다시 그 값이 다시 되면 팔기
                    if (now.isAfter(sellTime.minusSeconds(30)) && now.isBefore(sellTime)) {

                        System.out.println(sellTime.minusSeconds(30));
                        priceList.add(tradePrice); // add data every while loop // BigDecimal
                        System.out.println("before sorting" + priceList);

                        if (priceList.size() == 15) { // tradePrice in 30 ~ 45 seconds
                            Collections.sort(priceList, Collections.reverseOrder());
                            maxPrice = priceList.get(0);
                            System.out.println("after sorting" + priceList);
                        }

                        System.out.println("maxPrice : " + maxPrice);

                        if (maxPrice == tradePrice) { // 45 ~ 59 seconds
                            String sellData = sellMarketOrder(finalCoinNm, currencyBalance);
                            orderData = getSellOrderData(sellData);
                            sellUUID = orderData.get("uuid").toString();
                            sendBuyUUIDToDB(now.toString(), sellUUID);
                            System.out.println("마지막 30초때 최고가가 45초 이후에 다시 되어서 팔았어요~");
                            // and don't break
                        }
                    } // sell condition 4


                    // sell condition 5 : now >= sellTime
                    if (now.equals(sellTime) || now.isAfter(sellTime)) {

                        System.out.println(finalCoinNm);
                        Log.d(TAG, "sell");

                        if (currencyBalance > 0.00008) {
                            String sellData = sellMarketOrder(finalCoinNm, currencyBalance);
                            orderData = getSellOrderData(sellData);
                            sellUUID = orderData.get("uuid").toString();
                            sendBuyUUIDToDB(now.toString(), sellUUID);
                            System.out.println("5분 캔들이 끝나서 팔았어요~");
                        }
                        Thread.sleep(1000); // take one second
                        break;

                    } // sell condition 5

    /* if the balance is null because currency sold when it was condition2,
   the balance become null and come here */
                    Thread.sleep(1000); // take one second -> plue one second to now

                } catch (NumberFormatException e) {

                    e.printStackTrace();
                    LocalDateTime now = LocalDateTime.now().withNano(0);

                    while (now.isBefore(sellTime)) {
                        System.out.println("now : " + now);
                        System.out.println("sell time : " + sellTime);
                        Thread.sleep(1000);
                        now = now.plusSeconds(1);
                    }
                    break;

                } // catch
            } // sell while loop

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    } // newAutoTradeFiveMinute

    public void autoTradeFiveMinute() throws
            InterruptedException, NoSuchAlgorithmException, JSONException, IOException {

        String date = getJson.getCandleStartTime(GetJson.coinName, 5);
        LocalDateTime sellTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME).plusMinutes(5);
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
                buyData = getBuyOrderData(buy_data);
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
                        sellMarketOrder(GetJson.coinName, currencyBalance);
//                        sellMarketOrder(GetJson.coinName, currencyBalance * 0.9995);
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
                        sellMarketOrder(GetJson.coinName, currencyBalance);
//                        sellMarketOrder(GetJson.coinName, currencyBalance * 0.9995);
                        // and don't break
                    }
                }

                // sell condition 3 : now >= sellTime
                try {
                    if (now.equals(sellTime) || now.isAfter(sellTime)) {

                        System.out.println(GetJson.coinName);
                        Log.d(TAG, "sell");

                        if (currencyBalance > 0.00008)
                            sellMarketOrder(GetJson.coinName, currencyBalance);
//                            sellMarketOrder(GetJson.coinName, currencyBalance * 0.9995);

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

    public void autoTradeOneMinute() throws
            InterruptedException, NoSuchAlgorithmException, JSONException, IOException {
        System.out.println(GetJson.coinName);
        System.out.println(getJson.getCandleStartTime(GetJson.coinName, 1));

        String date = getJson.getCandleStartTime(GetJson.coinName, 1);
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
                buyData = getBuyOrderData(buy_data);
                uuid = buyData.get("uuid").toString();

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

                    String strTradePrice = new GetJson().getTradePrice(GetJson.coinName);
                    double tradePrice = Double.parseDouble(strTradePrice);

                    System.out.println("tradePrice : " + tradePrice);
                    System.out.println("buyPrice : " + buyPrice);

                    if (tradePrice <= buyPrice * 0.995) { // tradePrice is parameter
                        String sell_data = sellMarketOrder(GetJson.coinName, currencyBalance);
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
                            sellMarketOrder(GetJson.coinName, currencyBalance);
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

    public void autoTradeOneDay(String coinNm, String currentPrice, String
            targetPrice, LocalDateTime now) throws IOException, NoSuchAlgorithmException, InterruptedException {

        LocalDateTime startTime = LocalDateTime.now().with(LocalTime.of(9, 0, 0));
        LocalDateTime endTime = startTime.plusDays(1).with(LocalTime.of(8, 59, 50));

        System.out.println(coinNm);
        System.out.println(currentPrice);
        System.out.println(targetPrice);

        try {

            // buy
            if (startTime.isBefore(now) && endTime.isAfter(now)) {
                if (Integer.parseInt(targetPrice) < Integer.parseInt(currentPrice)) {
                    Log.d(TAG, "buy");

                    String strKrw = new GetJson().getBalance("KRW");
                    double krw = Double.parseDouble(strKrw);

                    if (krw > 5000)
                        buyMarketOrder("KRW-" + coinNm, krw * 0.9995);
                }
            }
            else { // sell

                Log.d(TAG, "sell");

                String strCurrencyBalance = new GetJson().getBalance(coinNm);
                double currencyBalance = Double.parseDouble(strCurrencyBalance);

                if (currencyBalance > 0.00008)
                    sellMarketOrder("KRW-" + coinNm, currencyBalance);
            }
        } catch (NumberFormatException | JSONException e) {
            e.printStackTrace();
        }
    }

    public String buyMarketOrder(String coinNm, double price) throws
            IOException, NoSuchAlgorithmException, JSONException {
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

    public String sellMarketOrder(String coinNm, double volume) throws
            IOException, NoSuchAlgorithmException {
        HashMap<String, String> params = new HashMap<>();
        params.put("market", coinNm);
        params.put("side", "ask"); // sell
        params.put("volume", Double.toString(volume));
        params.put("ord_type", "market");

        Client client = new Client();
        String data = EntityUtils.toString(client.postEntity(params));

        Log.d(TAG, data);
        return data;
    }

    public String buyOpeningPriceOrder(String coinNm, double price, double volume) throws
            IOException, NoSuchAlgorithmException, JSONException, InterruptedException {
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

    public HashMap<String, Object> getBuyOrderData(String data) throws
            InterruptedException, JSONException {
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

    public HashMap<String, Object> getSellOrderData(String data) throws
            InterruptedException, JSONException {
        Thread.sleep(30);
        JSONObject jsonObject = new JSONObject(data);
        String uuid = jsonObject.get("uuid").toString();
        String strVolume = jsonObject.get("volume").toString();
        Double volume = Double.parseDouble(strVolume);

        HashMap<String, Object> map = new HashMap<>();
        map.put("uuid", uuid);
        map.put("volume", volume);

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
