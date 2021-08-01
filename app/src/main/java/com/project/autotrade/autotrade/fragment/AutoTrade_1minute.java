package com.project.autotrade.autotrade.fragment;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.autotrade.R;
import com.project.autotrade.autotrade.AutoTrade;
import com.project.autotrade.autotrade.GetJson;

import org.json.JSONException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.HashMap;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class AutoTrade_1minute extends Fragment {

    int myProgress = 0;
    ProgressBar progressBarView;
    Button btn_start;
    Button btn_stop;
    TextView tv_time;
    int progress;
    CountDownTimer countDownTimer;
    int endTime = 100;


    private static final String TAG = "Main";
    private static BackgroundTask backgroundTask;
    private RequestQueue requestQueue;
    private static String targetPrice;
    private int value = 0;

    private GetJson getJson = new GetJson();
    private AutoTrade autoTrade = new AutoTrade();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_auto_trade_1minute, container, false);

        progressBarView = (ProgressBar) view.findViewById(R.id.view_progress_bar_1min);
        btn_start = (Button) view.findViewById(R.id.btn_start_1min);
        btn_stop = (Button) view.findViewById(R.id.btn_stop_1min);
        tv_time= (TextView) view.findViewById(R.id.tv_timer_1min);

        /*Animation*/
        RotateAnimation makeVertical = new RotateAnimation(0, -90, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        makeVertical.setFillAfter(true);
        progressBarView.startAnimation(makeVertical);
        progressBarView.setSecondaryProgress(endTime);
        progressBarView.setProgress(0);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                backgroundTask = new BackgroundTask();
//                backgroundTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                fn_countdown();
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundTask.cancel(true);
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
        protected void onPreExecute() { }

        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        @Override
        protected Integer doInBackground(Integer... values) {

            try {
                getJson.getTopTenCoin(); // get all coins name & top ten coins list

                Thread.sleep(1000);
                AutoTradeOneMinuteThread autoTradeOneMinuteThread = new AutoTradeOneMinuteThread();
                autoTradeOneMinuteThread.run();

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

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            fn_countdown();

        }
    };

    // Thread that used in BackgroundTask.class
    class AutoTradeOneMinuteThread implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    getJson.getFinalCoin(1);

                    getTickerData(GetJson.coinName.substring(4));
                    Thread.sleep(1000);

                    Message msg = handler.obtainMessage();
                    handler.sendMessage(msg);

                    autoTrade.autoTradeOneMinute();
                }
            } catch (InterruptedException | NoSuchAlgorithmException | JSONException | IOException e) {
                e.printStackTrace();
            }
        }
    }

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


    private void fn_countdown() {

            myProgress = 0;

            try {
                countDownTimer.cancel();
            } catch (Exception e) { }

            progress = 1;
            endTime = 60;

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