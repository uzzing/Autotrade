package com.project.autotrade;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.project.autotrade.trade.AutoTrade;
import com.project.autotrade.trade.Client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import cz.msebera.android.httpclient.util.EntityUtils;

public class ChartActivity extends AppCompatActivity {

    private static final String TAG = "Main";
    int value = 0;

    private static BarChart barChart;
    public static ArrayList<BarEntry> barList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        barChart = (BarChart) findViewById(R.id.bar_chart);

        try {
            barList.add(new BarEntry(0, 0));

            BarDataSet barDataSet = new BarDataSet(barList, "5 minutes");
            barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
            barDataSet.setValueTextColor(Color.RED);
            barDataSet.setValueTextSize(13);
            BarData barData = new BarData(barDataSet);

            barChart.setData(barData);
            barChart.setFitBars(true);
            barChart.getDescription().setText("Bar Chart");
            barChart.animateY(2000);
            barChart.setVisibleXRangeMaximum(6);
            barChart.moveViewToX(10);
            barChart.getAxisRight().setAxisMinimum(-100);
            barChart.invalidate();

        }  catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    // for calculating profit
    public static String getBuyOrderInfo() throws IOException, NoSuchAlgorithmException {
        HashMap<String, String> params = new HashMap<>();
        params.put("uuid", AutoTrade.buyUUID);
        Client client = new Client();
        String data = EntityUtils.toString(client.getOrderInfo(params));
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, data);
        return data;
    }
    // for calculating profit
    public static String getSellOrderInfo() throws IOException, NoSuchAlgorithmException {
        HashMap<String, String> params = new HashMap<>();
        params.put("uuid", AutoTrade.sellUUID);
        Client client = new Client();
        String data = EntityUtils.toString(client.getOrderInfo(params));
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, data);
        return data;
    }

    public static void calculateProfit() throws IOException, NoSuchAlgorithmException, JSONException {

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

        double profit = 0;
        // calculate profit
        if (bidFunds > askFunds) {
            profit = - (1 - askFunds / bidFunds) - 0.05; // -0.0016 - 0.05
        }
        else {
            profit = (1 - bidFunds / askFunds) - 0.05;
        }

        System.out.println(profit);

        addfiveMinutesBar(barList, profit);
    }

    // 5분마다 바 하나씩 추가하는 함수
    private static void addfiveMinutesBar (ArrayList<BarEntry> barList, double profit) throws JSONException, NoSuchAlgorithmException, IOException {

        BarData barData = barChart.getData();

        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getDescription().setText("Bar Chart");
        barChart.animateY(2000);
        barChart.setVisibleXRangeMaximum(6);
        barChart.moveViewToX(10);
        barChart.getAxisRight().setAxisMinimum(-100);


        IBarDataSet barDataSet = createSet();
        barData.addDataSet(barDataSet);

        // get minute
        int minute = Calendar.getInstance().get(Calendar.MINUTE);

        // get profit
        float profitForChart = Float.parseFloat(profit + "f");

        // add one bar to bar chart
        //barList.add(new BarEntry(minute, profitForChart));
        barData.addEntry(new BarEntry(minute, profitForChart), 0);
        barData.notifyDataChanged();
        barChart.notifyDataSetChanged();
        barChart.moveViewTo(barData.getEntryCount(), 50f, YAxis.AxisDependency.LEFT);
    }

    private static BarDataSet createSet() {

        BarDataSet barDataSet = new BarDataSet(null, "5 minutes");
        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        barDataSet.setValueTextColor(Color.RED);
        barDataSet.setValueTextSize(13);

        return barDataSet;
    }
}