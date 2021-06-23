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

public class getJson {
    private RequestQueue requestQueue;
    private static final String TAG = "Main";

    private void getOrderBooks(String coinNm) {

        final String sCoinNm = coinNm;

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
    } //getOrderBookss

    // currentPrice
    private void getCurrentPrice(String data) {
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

            new AutoTrade(askPrice, bidPrice);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    } //getCurrentPrice

    // get balance
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
    } // getAccounts

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
