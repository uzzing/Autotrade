package com.project.autotrade.autotrade;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.autotrade.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class GetCurrent extends AppCompatActivity {

    private RequestQueue requestQueue;

    private static final String TAG = "Main";

    EditText edit_coinNm;
    TextView opening_price;
    TextView high_price;
    TextView low_price;
    TextView trade_price;
    TextView prev_closing_price;
    TextView acc_trade_price_24h;
    TextView acc_trade_volume_24h;
    ImageButton goBackToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_current);

        opening_price = findViewById(R.id.opening_price);
        high_price = findViewById(R.id.high_price);
        low_price = findViewById(R.id.low_price);
        trade_price = findViewById(R.id.trade_price);
        prev_closing_price = findViewById(R.id.prev_closing_price);
        acc_trade_price_24h = findViewById(R.id.acc_trade_price_24h);
        acc_trade_volume_24h = findViewById(R.id.acc_trade_volume_24h);
        goBackToggle = findViewById(R.id.get_current_goback);

        Button btn_search = findViewById(R.id.get_current_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edit_coinNm = findViewById(R.id.get_current_edit_coinNm);

                String coinNm = edit_coinNm.getText().toString().toUpperCase();

                if (!coinNm.isEmpty()) {
                    getTickerData(coinNm);
                }
            } //onClick
        }); //setOnClickListener

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        goBackToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

                String sOpening_price = jsonObject.get("opening_price").toString();
                String sHigh_price = jsonObject.get("high_price").toString();
                String sLow_price = jsonObject.get("low_price").toString();
                String sTrade_price = jsonObject.get("trade_price").toString();
                String sPrev_closing_price = jsonObject.get("prev_closing_price").toString();
                String sAcc_trade_price_24h = jsonObject.get("acc_trade_price_24h").toString();
                String sAcc_trade_volume_24h = jsonObject.get("acc_trade_volume_24h").toString();

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
    }

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