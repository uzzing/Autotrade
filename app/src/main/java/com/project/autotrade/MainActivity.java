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
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private static final String TAG = "Main";
    int value = 0;

    public static BackgroundTask task; // used in AutoTrade.class
    private static String currentPrice; // send to AutoTrade.class
    private static String targetPrice; // send to AutoTrade.class
    private static String coinNm; // send to AutoTrade.class
    AutoTrade autoTrade = new AutoTrade();

    EditText edit_coinNm;

    // <-- function -->
    // UI
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // task
        Button btn_order = findViewById(R.id.btn_order);
        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_coinNm = findViewById(R.id.edit_coinNm);

                coinNm = edit_coinNm.getText().toString().toUpperCase();

                if (!coinNm.isEmpty()) {
                    task = new BackgroundTask();
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });

        // cancel task
        Button btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.cancel(true);
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

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
    } // onCreate

    // Http communication by using Volley library
    public void getOrderBookData(String coinNm) {

        final String sCoinNm = coinNm;

        String url = "https://api.upbit.com/v1/orderbook?markets=KRW-" + sCoinNm;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        GetJson getJson = new GetJson();
                        currentPrice = getJson.getCurrentPrice(response);
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
    } // getOrderBooks

    // Http communication by using Volley library
    public void getTickerData(String coinNm) {

        final String sCoinNm = coinNm;

        String url = "https://api.upbit.com/v1/ticker?markets=KRW-" + sCoinNm;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        GetJson getJson = new GetJson();
                        targetPrice = getJson.getTargetPrice(response);
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

    // Main thread
    class BackgroundTask extends AsyncTask<Integer, String, Integer> {
        @Override
        protected void onPreExecute() {
        }

        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        @Override
        protected Integer doInBackground(Integer... values) {

            try {
                getOrderBookData(coinNm);
                getTickerData(coinNm);

                Thread.sleep(1000);

                AutoTradeThread autoTradeThread = new AutoTradeThread();
                autoTradeThread.run();

                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            return value;
        }

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
