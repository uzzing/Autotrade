package com.project.autotrade;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import cz.msebera.android.httpclient.util.EntityUtils;

public class GetJson {
    private RequestQueue requestQueue;
    private static final String TAG = "Main";

    private void getOrderBooks(String coinNm) {

        //final String sCoinNm = coinNm;
        final String sCoinNm = "DOGE";

        String url = "https://api.upbit.com/v1/orderbook?markets=KRW-" + sCoinNm;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getCurrentPrice(response);
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

    // currentPrice
    public double getCurrentPrice(String data) {
        ArrayList<OrderBookVo> items = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(data);

            String ask_price; //매도 호가
            String bid_price; //매수 호가

            JSONObject jsonObject = jsonArray.getJSONObject(0);

            //변수에 호가정보를 담는다.
            String orderbook_units = jsonObject.get("orderbook_units").toString();

            //변수를 jsonArray 타입의 변수에 담는다.
            JSONArray arrayUnits = new JSONArray(orderbook_units);

            //매수,매도 정보를 담을 리스트 선언
            ArrayList<Double> arr_ask_price = new ArrayList<>();
            ArrayList<Double> arr_bid_price = new ArrayList<>();

            for (int i = 0; i < arrayUnits.length(); i++) {

                //배열안에 있는 오브젝트 타입의 데이터를 변수에 담는다.
                JSONObject objectUnits = arrayUnits.getJSONObject(i);

                //변수에서 호가정보를 꺼내서 각각의 변수에 담는다.
                ask_price = objectUnits.get("ask_price").toString();
                bid_price = objectUnits.get("bid_price").toString();

                //1000자리 콤마 처리
                arr_ask_price.add(Double.parseDouble(ask_price));
                arr_bid_price.add(Double.parseDouble(bid_price));
            }

            // 매도 담기 : 0
            double askPrice = arr_ask_price.get(0);

            // 매수 담기 : arr_bid_price.size() - 1
            double bidPrice = arr_bid_price.get(arr_ask_price.size() - 1);

            double currentPrice = (askPrice + bidPrice) / 2;
            return currentPrice;

        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    } //getCurrentPrice

    public void getTickerData(String data) {

        try {
            JSONArray jsonArray = new JSONArray(data);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String sOpening_price = jsonObject.get("opening_price").toString();//시가
                String sHigh_price = jsonObject.get("high_price").toString();//고가
                String sLow_price = jsonObject.get("low_price").toString();//저가
                String sTrade_price = jsonObject.get("trade_price").toString();//종가
                String sPrev_closing_price = jsonObject.get("prev_closing_price").toString();//전일종가
                String sAcc_trade_price_24h = jsonObject.get("acc_trade_price_24h").toString();//24시간 누적거래대금
                String sAcc_trade_volume_24h = jsonObject.get("acc_trade_volume_24h").toString();//24시간 누적거래량

                new AutoTrade(sHigh_price, sLow_price, sTrade_price);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    } //getTickerData

    public double getBalance(String coinNm) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        try {
            // http client
            Client client = new Client();

            //1. 데이터 담기
            String data =  EntityUtils.toString(client.getEntity());

            //2. 데이터를 배열에 담기
            JSONArray jsonArray = new JSONArray(data);

            String currency;
            Long balance = 0L;

            for (int i = 0; i < jsonArray.length(); i++) {

                //3. 배열에 있는 오브젝트를 오브젝트에 담기
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                //4. 오브젝트에 있는 데이터를 key값으로 불러오기
                currency = jsonObject.get("currency").toString(); //화폐 종류

                if (currency.equals(coinNm)) {
                    balance = Long.parseLong(jsonObject.get("balance").toString());
                    break;
                }
            }
            return balance;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return 0;
        }
    } // getBalance

    private void getAccounts() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        try {
            // http client
            Client client = new Client();

            //1. 데이터 담기
            String data =  EntityUtils.toString(client.getEntity());

            String currency      = ""; //화폐 종류
            String balance       = "";//주문가능 금액/수량
            String locked        = ""; //주문 중 묶여있는 금액
            String avg_buy_price = ""; //매수 평균가

            //2. 데이터를 배열에 담기
            JSONArray jsonArray = new JSONArray(data);

            for (int i = 0; i < jsonArray.length(); i++) {

                //3. 배열에 있는 오브젝트를 오브젝트에 담기
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                //4. 오브젝트에 있는 데이터를 key값으로 불러오기
                currency      = jsonObject.get("currency").toString();//화폐 종류
                balance       = jsonObject.get("balance").toString();//주문가능 금액&수량
                locked        = jsonObject.get("locked").toString();//주문 중 묶여있는 금액&수량
                avg_buy_price = jsonObject.get("avg_buy_price").toString();//매수 평균가

                Log.d(TAG, "화폐종류: " + currency);
                Log.d(TAG, "주문가능 금액&수량: " + balance);
                Log.d(TAG, "주문 중 묶여있는 금액&수량: " + locked);
                Log.d(TAG, "매수 평균가: " + avg_buy_price);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public String toDoubleFormat(Double num) {

        DecimalFormat df = null;

        if(num >= 100 && num <= 999.9){
            df = new DecimalFormat("000.0");
        }else if(num >= 10 && num <= 99.99){
            df = new DecimalFormat("00.00");
        }else if(num >= 1 && num <= 9.9999){
            df = new DecimalFormat("0.000");
        }else if(num < 1){
            df = new DecimalFormat("0.0000");
        }else{
            df = new DecimalFormat("###,###,###");
        }
        return df.format(num);
    } // toDoubleFormat

}
