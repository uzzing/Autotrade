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

import com.google.android.material.internal.NavigationSubMenu;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import cz.msebera.android.httpclient.util.EntityUtils;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";
    int value = 0;

    private BackgroundTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //조회버튼
        Button btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task = new BackgroundTask();
                task.execute(); //반복시작
            }
        });

        //취소버튼
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
    }

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


    class BackgroundTask extends AsyncTask<Integer, String, Integer>
    {
        @Override
        protected void onPreExecute(){ }

        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        @Override
        protected Integer doInBackground(Integer... values) {

            //정지 시킬때까지 반복
            while (!isCancelled()) {
                try {
                    getAccounts();
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } //while

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