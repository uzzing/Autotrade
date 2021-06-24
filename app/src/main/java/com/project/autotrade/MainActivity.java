package com.project.autotrade;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import cz.msebera.android.httpclient.util.EntityUtils;


public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private static final String TAG = "Main";
    int value = 0;
    private BackgroundTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // task
        Button btn_order = findViewById(R.id.btn_order);
        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    task = new BackgroundTask();
                    task.execute();
            }
        });

        // cancel task
        Button btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.cancel(true); //반복 취소
            }
        });

        // get current
        Button btn_getCurrent = findViewById(R.id.btn_getCurrent);
        btn_getCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GetCurrent.class);
                startActivity(intent);
            }
        });

        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
    } // onCreate

    private void getOrderBookData(String coinNm){

        final String sCoinNm = coinNm;

        String url = "https://api.upbit.com/v1/orderbook?markets=KRW-" + sCoinNm;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        GetJson getJson = new GetJson();
                        getJson.getCurrentPrice(response);
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
    } // getOrderBooks

    public void getTickerData(String coinNm) {

        final String coinName = coinNm;

        String url = "https://api.upbit.com/v1/ticker?markets=KRW-" + coinName;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        GetJson getJson = new GetJson();
                        getJson.getTargetPrice(response);
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

    class BackgroundTask extends AsyncTask<Integer, String, Integer>
    {
        @Override
        protected void onPreExecute(){ }

        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        @Override
        protected Integer doInBackground(Integer... values) {
            try {
                getOrderBookData("BTC");
                getTickerData("BTC");

                AutoTrade autoTrade = new AutoTrade();
                autoTrade.autotrade();

                Thread.sleep(1000);

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return value;
        }

        //상태확인
        @Override
        protected void onProgressUpdate(String... values) {
        }

        @Override
        protected void onPostExecute(Integer integer) {
        }

        @Override
        protected void onCancelled() {
        }
    }
}