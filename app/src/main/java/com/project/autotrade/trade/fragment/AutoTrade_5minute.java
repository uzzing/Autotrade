package com.project.autotrade.trade.fragment;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.autotrade.R;
import com.project.autotrade.chart.fragment.Fragment_5minute;
import com.project.autotrade.trade.AutoTrade;
import com.project.autotrade.trade.Client;
import com.project.autotrade.trade.GetJson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import cz.msebera.android.httpclient.util.EntityUtils;

import static android.view.animation.Animation.RELATIVE_TO_SELF;


public class AutoTrade_5minute extends Fragment {

    int myProgress = 0;
    ProgressBar progressBarView;
    Button btn_start;
    Button btn_stop;
    TextView tv_time;
    int progress;
    CountDownTimer countDownTimer;
    int endTime = 300;

    private static final String TAG = "Main";
    private static BackgroundTask backgroundTask;
    private RequestQueue requestQueue;
    private static String targetPrice;
    private int value = 0;

    private GetJson getJson = new GetJson();
    private AutoTrade autoTrade = new AutoTrade();
    private static ArrayList<HashMap<String, String>> tradePriceList;

    private static DatabaseReference ChartRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Initialize view
        View view = inflater.inflate(R.layout.fragment_auto_trade_5minute, container, false);

        progressBarView = (ProgressBar) view.findViewById(R.id.view_progress_bar_5min);
        btn_start = (Button) view.findViewById(R.id.btn_start_5min);
        tv_time= (TextView) view.findViewById(R.id.tv_timer_5min);
        btn_stop = (Button) view.findViewById(R.id.btn_stop_5min);

        /*Animation*/
        RotateAnimation makeVertical = new RotateAnimation(0, -90, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        makeVertical.setFillAfter(true);
        progressBarView.startAnimation(makeVertical);
        progressBarView.setSecondaryProgress(endTime);
        progressBarView.setProgress(0);


        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundTask = new BackgroundTask();
                backgroundTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                fn_countdown();
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                tv_time.setText("00:00:00");
            }
        });

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(view.getContext());
        }

        return view;
    }

    class BackgroundTask extends AsyncTask<Integer, String, Integer> {

        @Override
        protected void onPreExecute() {}

        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        @Override
        protected Integer doInBackground(Integer... values) {
            try {
                getJson.getTopTenCoin(); // set the global data 'tradeTopTenCoin'
                getJson.getRecentTradeVolume(); // set the global data 'recentVolumeTenCoin'

                Thread.sleep(1000);

                NewAutoTradeFiveMinuteThread newAutoTradeFiveMinuteThread = new NewAutoTradeFiveMinuteThread();
                newAutoTradeFiveMinuteThread.run();

            } catch (InterruptedException | NoSuchAlgorithmException | IOException | JSONException e ) {
                e.printStackTrace();
            }

            return value;
        }

        @Override
        protected void onProgressUpdate(String... values) { }

        @Override
        protected void onPostExecute(Integer integer) { }

        @Override
        protected void onCancelled() { }
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

                    String finalCoinNm = getJson.getNewFinalCoin(tradePriceList);
                    autoTrade.newAutoTradeFiveMinute(finalCoinNm);
                    calculateProfit();
//                    Fragment_SumOfProfit.calculateSumOfProfit();

                    Thread.sleep(1000);
                }
            } catch (InterruptedException | NoSuchAlgorithmException | IOException | JSONException e ) {
                e.printStackTrace();
            }
        }
    }

    /**
     to save trade result to firebase for chart
     */
    public void calculateProfit() throws IOException, NoSuchAlgorithmException, JSONException {

        // get buy price from json
        String buyData = getBuyOrderInfo();
        JSONObject buyJsonObject = new JSONObject(buyData);
        String buyTrades = buyJsonObject.get("trades").toString();

        JSONArray buyTradesArray = new JSONArray(buyTrades);
        JSONObject buyTradesObject = buyTradesArray.getJSONObject(0);
        String buyTradeFunds = buyTradesObject.get("funds").toString();
        Double bidFunds = Double.parseDouble(buyTradeFunds);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // get sell price from json
        String sellData = getSellOrderInfo();
        JSONObject sellJsonObject = new JSONObject(sellData);
        String sellTrades = sellJsonObject.get("trades").toString();

        JSONArray sellTradesArray = new JSONArray(sellTrades);
        JSONObject sellTradesObject = sellTradesArray.getJSONObject(0);
        String sellTradeFunds = sellTradesObject.get("funds").toString();
        Double askFunds = Double.parseDouble(sellTradeFunds);

        System.out.println("bid funds = " + bidFunds);
        System.out.println("ask funds = " + askFunds);

        float profit = 0;

        // calculate profit
        if (bidFunds > askFunds) {
            profit = (float) (-(1 - askFunds / bidFunds) - 0.1);
        } else {
            profit = (float) ((1 - bidFunds / askFunds) - 0.1);
        }

        System.out.println(profit);

        saveResultToDB(profit);
    }

    // save trade result to firebase
    private void saveResultToDB(float profit) throws JSONException, NoSuchAlgorithmException, IOException {

        ChartRef = FirebaseDatabase.getInstance().getReference().child("Profits per 5 minutes");

        // get minute
        int minute = Calendar.getInstance().get(Calendar.MINUTE);

        // get profit
        float profitForChart = Float.parseFloat(profit + "f");

        // insert data to firebase
        String id = ChartRef.push().getKey();
        HashMap<String, Object> data = new HashMap<>();
        data.put("xValue", minute);
        data.put("yValue", profitForChart);

        ChartRef.child(id).updateChildren(data);
    }


    // for calculating profit
    public String getBuyOrderInfo() throws IOException, NoSuchAlgorithmException {

        HashMap<String, String> params = new HashMap<>();
        params.put("uuid", AutoTrade.buyUUID);
        Client client = new Client();
        String data = EntityUtils.toString(client.getOrderInfo(params));

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(" buy order info : " + data);

        return data;
    }

    // for calculating profit
    public String getSellOrderInfo() throws IOException, NoSuchAlgorithmException {

        HashMap<String, String> params = new HashMap<>();
        params.put("uuid", AutoTrade.sellUUID);
        Client client = new Client();
        String data = EntityUtils.toString(client.getOrderInfo(params));

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(" sell order info : " + data);

        return data;
    }


    private void fn_countdown() {

            myProgress = 0;

            try {
                countDownTimer.cancel();
            } catch (Exception e) { }

            progress = 1;

            countDownTimer = new CountDownTimer(endTime * 1000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                    setProgress(progress, endTime);
                    progress = progress + 1;
                    int seconds = (int) (millisUntilFinished / 1000) % 60;
                    int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                    int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                    String newtime = hours + ":" + minutes + ":" + seconds;

                    if (newtime.equals("0:0:0")) {
                        tv_time.setText("00:00:00");
                    } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(minutes).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                        tv_time.setText("0" + hours + ":0" + minutes + ":0" + seconds);
                    } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(minutes).length() == 1)) {
                        tv_time.setText("0" + hours + ":0" + minutes + ":" + seconds);
                    } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                        tv_time.setText("0" + hours + ":" + minutes + ":0" + seconds);
                    } else if ((String.valueOf(minutes).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                        tv_time.setText(hours + ":0" + minutes + ":0" + seconds);
                    } else if (String.valueOf(hours).length() == 1) {
                        tv_time.setText("0" + hours + ":" + minutes + ":" + seconds);
                    } else if (String.valueOf(minutes).length() == 1) {
                        tv_time.setText(hours + ":0" + minutes + ":" + seconds);
                    } else if (String.valueOf(seconds).length() == 1) {
                        tv_time.setText(hours + ":" + minutes + ":0" + seconds);
                    } else {
                        tv_time.setText(hours + ":" + minutes + ":" + seconds);
                    }
                }

                @Override
                public void onFinish() {
                    setProgress(progress, endTime);
                }
            };
            countDownTimer.start();

    }

    public void setProgress(int startTime, int endTime) {
        progressBarView.setMax(endTime);
        progressBarView.setSecondaryProgress(endTime);
        progressBarView.setProgress(startTime);

    }


}