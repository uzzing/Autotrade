package com.project.autotrade;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.project.autotrade.trade.AutoTrade;
import com.project.autotrade.trade.Client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cz.msebera.android.httpclient.util.EntityUtils;

public class ChartActivity extends AppCompatActivity {

    private static final String TAG = "Main";

    BarChart barChart;

    // get date and time
    private String currentDate;
    private String currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        barChart = findViewById(R.id.bar_chart);

        ArrayList<BarEntry> fiveMinute = new ArrayList<>();
        fiveMinutesChart(fiveMinute);

        barChart.setFitBars(true);
        barChart.getDescription().setText("Bar Chart");
        barChart.animateY(2000);
        barChart.setVisibleXRangeMaximum(6);
        barChart.moveViewToX(10);
        barChart.getAxisRight().setAxisMinimum(-100);
    }

    private void getNow() {
        // get date and time
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy MM dd");
        currentDate = simpleDateFormat.format(calendar1.getTime());

        Calendar calendar2 = Calendar.getInstance();
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm a");
        currentTime = simpleTimeFormat.format(calendar2.getTime());
    }

    // 5분마다 바 하나씩 추가하는 함수
    private void fiveMinutesChart(ArrayList<BarEntry> fiveMinute) {

        // get minute
        int minute = Calendar.getInstance().get(Calendar.MINUTE);

        // get profit
        float profit = Float.parseFloat(-0.3 + "f");

        // add one bar to bar chart
        fiveMinute.add(new BarEntry(minute, profit));

        // ui
        BarDataSet barDataSet = new BarDataSet(fiveMinute, "fiveMinute");
        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        barDataSet.setValueTextColor(Color.RED);
        barDataSet.setValueTextSize(13);

        // show data
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
    }


}