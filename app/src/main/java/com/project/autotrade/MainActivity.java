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
import com.project.autotrade.chart.Fragment_5minute;
import com.project.autotrade.trade.AutoTrade;
import com.project.autotrade.trade.Client;
import com.project.autotrade.trade.GetCurrent;
import com.project.autotrade.trade.GetJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.util.EntityUtils;


public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private static final String TAG = "Main";
    int value = 0;

    private static BackgroundTask task; // used for autoTrade() in AutoTrade.class
    private static BackgroundTask2 task2; // used for autoTradeOneMinute() in AutoTrade.class
    private static BackgroundTask3 task3; // used for autoTradeFiveMinute() in AutoTrade.class
    private static BackgroundTask4 task4; // used for newAutoTradeFiveMinute() in AutoTrade.class

    private static String currentPrice; // send to autoTrade() in AutoTrade.class
    private static String targetPrice; // send to autoTrade() in AutoTrade.class
    private static String coinNm; // send to autoTrade() in AutoTrade.class
    private static ArrayList<HashMap<String, String>> tradePriceList;
    AutoTrade autoTrade = new AutoTrade();
    GetJson getJson = new GetJson();

    EditText edit_coinNm;

    // <-- function -->
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // general task
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

        // autoTradeOneMinute task
        Button btn_oneMinute = findViewById(R.id.btn_oneMinute);
        btn_oneMinute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //task.cancel(true);
                task2 = new BackgroundTask2();
                task2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        });

        // autoTradeFiveMinute task
        Button btn_fiveMinute = findViewById(R.id.btn_fiveMinute);
        btn_fiveMinute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task3 = new BackgroundTask3();
                task3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        });

        // newAutoTradeFiveMinute task
        Button btn_newFiveMinute = findViewById(R.id.btn_newFiveMinute);
        btn_newFiveMinute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task4 = new BackgroundTask4();
                task4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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

        // go to chart activity
        Button btn_chart = findViewById(R.id.btn_chart);
        btn_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChartActivity.class);
                startActivity(intent);
            }
        });

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

    } // onCreate

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

    // Thread that used in BackgroundTask.class
    class AutoTradeThread implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(1000);
                    LocalDateTime now = LocalDateTime.now().withNano(0);
                    autoTrade.autoTrade(coinNm, currentPrice, targetPrice, now);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // autoTradeOneMinute
    class BackgroundTask2 extends AsyncTask<Integer, String, Integer> {
        @Override
        protected void onPreExecute() {
        }

        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        @Override
        protected Integer doInBackground(Integer... values) {

            try {
                getJson.getTopTenCoin(); // get all coins name & top ten coins list

                Thread.sleep(1000);
                AutoTradeOneMinuteThread autoTradeOneMinuteThread = new AutoTradeOneMinuteThread();
                autoTradeOneMinuteThread.run();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
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

    // Thread that used in BackgroundTask2.class
    class AutoTradeOneMinuteThread implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    getJson.getFinalCoin(1);

                    getTickerData(GetJson.coinName.substring(4));
                    Thread.sleep(1000);

                    autoTrade.autoTradeOneMinute();
                }
            } catch (InterruptedException | NoSuchAlgorithmException | JSONException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    // autoTradeFiveMinute
    class BackgroundTask3 extends AsyncTask<Integer, String, Integer> {
        @Override
        protected void onPreExecute() {
        }
        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        @Override
        protected Integer doInBackground(Integer... values) {
            try {
                getJson.getTopTenCoin(); // set the global data 'tradeTopTenCoin'
                Thread.sleep(1000);

                AutoTradeFiveMinuteThread autoTradeFiveMinuteThread = new AutoTradeFiveMinuteThread();
                autoTradeFiveMinuteThread.run();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
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
    // Thread that used in BackgroundTask3.class
    class AutoTradeFiveMinuteThread implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    getJson.getFinalCoin(5); // set the global data 'coinName'
                    getTickerData(GetJson.coinName.substring(4));
                    Thread.sleep(1000);

                    autoTrade.autoTradeFiveMinute();
                }
            } catch (InterruptedException | NoSuchAlgorithmException | JSONException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    // newAutoTradeFiveMinute
    class BackgroundTask4 extends AsyncTask<Integer, String, Integer> {
        @Override
        protected void onPreExecute() {
        }
        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        @Override
        protected Integer doInBackground(Integer... values) {
            try {
                getJson.getTopTenCoin(); // set the global data 'tradeTopTenCoin'
                getJson.getRecentTradeVolume(); // set the global data 'recentVolumeTenCoin'

                Thread.sleep(1000);

                NewAutoTradeFiveMinuteThread newAutoTradeFiveMinuteThread = new NewAutoTradeFiveMinuteThread();
                newAutoTradeFiveMinuteThread.run();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
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

    // Thread that used in BackgroundTask4.class
    class NewAutoTradeFiveMinuteThread implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {

                    tradePriceList = new ArrayList<>();
                    for (int i = 0; i < 5; i++) {
                        tradePriceList = new GetJson().getTradePrice(tradePriceList);
                        Thread.sleep(1000);
                    }

                    Thread.sleep(1000);
                    String finalCoinNm = getJson.getNewFinalCoin(tradePriceList);
                    autoTrade.newAutoTradeFiveMinute(finalCoinNm);
                    Fragment_5minute.calculateProfit();

                    Thread.sleep(1000);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

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
                        HashMap<String, Double> data = getJson.getTickerData(response); //get values
                        double openingPrice = data.get("openingPrice");
                        double highPrice = data.get("highPrice");
                        double lowPrice = data.get("lowPrice");

                        NumberFormat format = NumberFormat.getInstance();
                        format.setGroupingUsed(false);
                        targetPrice = format.format(openingPrice + (highPrice - lowPrice) * 0.3);
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
}
