package com.project.autotrade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.autotrade.chart.BarChartData;
import com.project.autotrade.trade.AutoTrade;
import com.project.autotrade.trade.Client;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.experimental.theories.DataPoint;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import cz.msebera.android.httpclient.util.EntityUtils;

public class ChartActivity extends AppCompatActivity {

    private static final String TAG = "Main";
    int value = 0;

    // bar chart
    private static BarChart barChart;
    private static ArrayList<BarEntry> barList = new ArrayList<>();
    private static BarDataSet barDataSet = new BarDataSet(barList, "5 minutes");
    private static BarData barData = new BarData(barDataSet);
    private static IBarDataSet iBarDataSet = barData.getDataSetByIndex(0);

    // insert data to firebase
    FirebaseDatabase firebaseDatabase;
    static DatabaseReference chartRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        barChart = (BarChart) findViewById(R.id.bar_chart);

        firebaseDatabase = FirebaseDatabase.getInstance();
        chartRef = firebaseDatabase.getReference("ChartValues");

        try {

            barList.add(new BarEntry(0, 0));

            barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
            barDataSet.setValueTextColor(Color.RED);
            barDataSet.setValueTextSize(13);
            barDataSet.notifyDataSetChanged();

            barData.notifyDataChanged();

            retrieveData();

            barChart.setData(barData);
            barChart.setFitBars(true);
            barChart.getDescription().setText("Bar Chart");
            barChart.animateY(2000);
            barChart.getAxisRight().setAxisMinimum(-100);
            barChart.moveViewTo(barData.getEntryCount(), 50f, YAxis.AxisDependency.LEFT);
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

        float profit = 0;
        // calculate profit
        if (bidFunds > askFunds) {
            profit = (float) (- (1 - askFunds / bidFunds) - 0.1);
        }
        else {
            profit = (float) ((1 - bidFunds / askFunds) - 0.1);
        }

        System.out.println(profit);

        addFiveMinutesBar(profit);
    }

    // 5분마다 바 하나씩 추가하는 함수
    private static void addFiveMinutesBar (float profit) throws JSONException, NoSuchAlgorithmException, IOException {

        BarData barData = barChart.getData(); // get data first

        if (barData == null) {
            barData = new BarData();
            barChart.setData(barData);
        }

        if (barDataSet == null) {
            barDataSet = createSet();
            barData.addDataSet(barDataSet);
        }

        // get minute
        int minute = Calendar.getInstance().get(Calendar.MINUTE);

        // get profit
        float profitForChart = Float.parseFloat(profit + "f");

        // add one bar to bar chart
        barList.add(new BarEntry(minute, profitForChart));
        iBarDataSet = new BarDataSet(barList, "5 minutes");
        barData = new BarData(iBarDataSet);

        barData.notifyDataChanged();
        barChart.setData(barData);
        barChart.notifyDataSetChanged();
        barChart.setVisibleXRangeMaximum(6);

        insertData(minute, profitForChart);
    }

    private static BarDataSet createSet() {

        BarDataSet barDataSet = new BarDataSet(null, "5 minutes");
        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        barDataSet.setValueTextColor(Color.RED);
        barDataSet.setValueTextSize(13);

        return barDataSet;
    }

    private static void insertData(int minute, float profitForChart) {
        String id = chartRef.push().getKey();
        HashMap<String, Object> data = new HashMap<>();
        data.put("xValue", minute);
        data.put("yValue", profitForChart);
        chartRef.child(id).updateChildren(data);
    }

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
                    barChart.notifyDataSetChanged();
                    barChart.setVisibleXRangeMaximum(6);
                }
                else {
                    barChart.clear();
                    barChart.invalidate();
                }

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}