package com.project.autotrade;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class GetCurrent extends AppCompatActivity {

    private RequestQueue requestQueue;

    private static final String TAG = "Main";

    EditText edit_coinNm;
    TextView opening_price; //시가
    TextView high_price; //고가
    TextView low_price; //저가
    TextView trade_price; //종가
    TextView prev_closing_price;//전일 종가
    TextView acc_trade_price_24h; // 24시간 누적 거래대금
    TextView acc_trade_volume_24h;// 24시간 누적 거래량

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autotrade);

        opening_price = findViewById(R.id.opening_price);
        high_price = findViewById(R.id.high_price);
        low_price = findViewById(R.id.low_price);
        trade_price = findViewById(R.id.trade_price);
        prev_closing_price = findViewById(R.id.prev_closing_price);
        acc_trade_price_24h = findViewById(R.id.acc_trade_price_24h);
        acc_trade_volume_24h = findViewById(R.id.acc_trade_volume_24h);

        Button btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edit_coinNm = findViewById(R.id.edit_coinNm);

                String coinNm = edit_coinNm.getText().toString().toUpperCase();

                if (!coinNm.isEmpty()) {
                    getTicker(coinNm);
                }
            } //onClick
        }); //setOnClickListener

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
    } //onCreate()

    public void getTickerData(String coinNm) {

        final String coinName = coinNm;

        String url = "https://api.upbit.com/v1/ticker?markets=KRW-" + coinName;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //조회
                        getTicker(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getMessage());
                    }
                }) {
        };
        request.setShouldCache(false);
        requestQueue.add(request);
    }

    public void getTicker(String data) {

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

                //텍스트뷰에 데이터 담기
                opening_price.setText(toDoubleFormat(Double.parseDouble(sOpening_price)));
                high_price.setText(toDoubleFormat(Double.parseDouble(sHigh_price)));
                low_price.setText(toDoubleFormat(Double.parseDouble(sLow_price)));
                trade_price.setText(toDoubleFormat(Double.parseDouble(sTrade_price)));
                prev_closing_price.setText(toDoubleFormat(Double.parseDouble(sPrev_closing_price)));
                acc_trade_price_24h.setText(toDoubleFormat(Double.parseDouble(sAcc_trade_price_24h)));
                acc_trade_volume_24h.setText(toDoubleFormat(Double.parseDouble(sAcc_trade_volume_24h)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    } //getTickerData()

    /**
     * 1000자리 콤마
     *
     * @param num
     * @return
     */
    public String toDoubleFormat(Double num) {

        DecimalFormat df = null;

        if (num >= 100 && num <= 999.9) {
            df = new DecimalFormat("000.0");
        } else if (num >= 10 && num <= 99.99) {
            df = new DecimalFormat("00.00");
        } else if (num >= 1 && num <= 9.9999) {
            df = new DecimalFormat("0.000");
        } else if (num < 1) {
            df = new DecimalFormat("0.0000");
        } else {
            df = new DecimalFormat("###,###,###");
        }

        return df.format(num);
    }
}