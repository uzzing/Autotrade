package com.project.autotrade.chart;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.autotrade.R;
import com.project.autotrade.trade.AutoTrade;
import com.project.autotrade.trade.Client;

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

public class Fragment_5minute extends Fragment {

    View view;

    // bar chart
    private static BarChart barChart;
    public static ArrayList<BarEntry> barList = new ArrayList<>();
    private static BarDataSet barDataSet = new BarDataSet(barList, "5 minutes");
    private static BarData barData = new BarData(barDataSet);

    // insert data to firebase
    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference chartRef;

    // send datas to Fragement_SumOfProfit
    public static int lastX;
    public static float lastY;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Fragment_5minute : "+"onCreate()");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("Fragment_5minute : "+"onCreateView()");
        view = inflater.inflate(R.layout.fragment_5minute, container, false);

        barChart = (BarChart) view.findViewById(R.id.bar_chart_5minute);
        firebaseDatabase = FirebaseDatabase.getInstance();
        chartRef = firebaseDatabase.getReference("Profit per 5 minutes");

        try {

            barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
            barDataSet.setValueTextColor(Color.RED);
            barDataSet.setValueTextSize(13);
            barDataSet.notifyDataSetChanged();

            retrieveData();

            barData.notifyDataChanged();

            barChart.setData(barData);
            barChart.setFitBars(true);
            barChart.getDescription().setText("Bar Chart");
            barChart.animateY(2000);
            barChart.moveViewTo(barData.getEntryCount(), 50f, YAxis.AxisDependency.LEFT);
            barChart.invalidate();



        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        // Inflate the layout for this fragment
        return view;
    }

    // called by MainActivity
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

        float profit = 0;

        // calculate profit
        if (bidFunds > askFunds) {
            profit = (float) (-(1 - askFunds / bidFunds) - 0.1);
        } else {
            profit = (float) ((1 - bidFunds / askFunds) - 0.1);
        }

        System.out.println(profit);

        addFiveMinutesBar(profit);
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

        System.out.println(" buy order info : " + data);

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

        System.out.println(" sell order info : " + data);

        return data;

    }

    private static void addFiveMinutesBar(float profit) throws JSONException, NoSuchAlgorithmException, IOException {

        try {
            barData = barChart.getData(); // get data first
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (barData == null) {
            barData = new BarData();
            barChart.setData(barData);

            System.out.println("barData is null");
            System.out.println("barData : " + barData);
            System.out.println("barDataSet : " + barDataSet);
            System.out.println("barChart : " + barChart);
        }

        if (barDataSet == null) {
            barDataSet = new BarDataSet(null, "5 minutes");
            barData.addDataSet(barDataSet);

            System.out.println("barDataSet is null");
            System.out.println("barDataSet : " + barDataSet);
            System.out.println("barData : " + barData);
            System.out.println("barChart : " + barChart);
        }

        // get minute
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        lastX = minute;

        // get profit
        float profitForChart = Float.parseFloat(profit + "f");
        lastY = profitForChart;

        System.out.println("barDataSet : " + barDataSet);
        System.out.println("barData : " + barData);
        System.out.println("barChart : " + barChart);

        barData.notifyDataChanged();

        try {
            barChart.setData(barData);
            barChart.notifyDataSetChanged();
            barChart.setVisibleXRangeMaximum(6);
            barChart.notifyDataSetChanged();

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        insertData(minute, profitForChart);
    }

    // save data to firebase
    private static void insertData(int minute, float profitForChart) {
        String id = chartRef.push().getKey();
        HashMap<String, Object> data = new HashMap<>();
        data.put("xValue", minute);
        data.put("yValue", profitForChart);
        chartRef.child(id).updateChildren(data);
    }

    // get data from firebase
    private static void retrieveData() {

        chartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if (snapshot.getValue(BarChartData.class) != null) {
                    for (DataSnapshot eachSnapshot : snapshot.getChildren()) {
                        BarChartData barChartData = eachSnapshot.getValue(BarChartData.class);
                        barList.add(new BarEntry(barChartData.getxValue(), barChartData.getyValue()));
                        System.out.println(barChartData.getxValue() + barChartData.getyValue());
                    }

                    barDataSet.notifyDataSetChanged();
                    barData.notifyDataChanged();
                    barChart.setData(barData);
                    barChart.setVisibleXRangeMinimum(6);
                    barChart.setVisibleXRangeMaximum(6);
                    barChart.setVisibleXRange(0, 60);
                    System.out.println("(Fragment_5minute)barList: " + barList);
                    barChart.notifyDataSetChanged();
                    barChart.invalidate();

                } else {
                    barChart.clear();
                    barChart.invalidate();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

//    public static void saveListData() {
//        int listSize = barList.size() - 1;
//        lastX = (int) barList.get(listSize).getX();
//        lastY = barList.get(listSize).getY();
//    }

    @Override
    public void onDestroy() {
        barList = new ArrayList<>();
        System.out.println("Fragment_5minute: "+"onDestroy()");
        super.onDestroy();
    }
}