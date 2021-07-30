package com.project.autotrade.chart.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.autotrade.R;
import com.project.autotrade.chart.model.BarChartData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class Fragment_SumOfProfit extends Fragment {

    View view;

    // bar chart
    private static BarChart barChart;
    public static ArrayList<BarEntry> barList = new ArrayList<>();
    private static BarDataSet barDataSet = new BarDataSet(barList, "sum of 5 minutes profit");
    private static BarData barData = new BarData(barDataSet);

    private static final Object TAG = null;
    private static float sumOfProfit;
    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference chartRef;
    private static DatabaseReference chart5minutesRef;
    private static DatabaseReference lastRef;

    private static int[] lastXofDB = new int[1];
    private static float[] lastYofDB = new float[1];
    private static Integer countOfDB = 0;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_sum_of_profit, container, false);

        barChart = (BarChart) view.findViewById(R.id.bar_chart_sum_of_profit);
        firebaseDatabase = FirebaseDatabase.getInstance();
        chartRef = firebaseDatabase.getReference().child("Sum of 5 minute's profit");
        chart5minutesRef = firebaseDatabase.getReference().child("Profit per 5 minutes");


        retrieveData();

        // Inflate the layout for this fragment
        return view;
    }

    public void calculateSumOfProfit() {

        Query lastQuery = lastRef.orderByKey().limitToLast(1);
        lastQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for (DataSnapshot lastChild : snapshot.getChildren()) {
                    lastXofDB[0] = Integer.parseInt(lastChild.getKey());
                    lastYofDB[0] = lastChild.getValue(BarChartData.class).getyValue();

                    System.out.println("lastKeyofDB : " + lastXofDB[0]);
                    System.out.println("lastYofDB : " + lastYofDB[0]);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        System.out.println("lastKeyofDB : " + lastXofDB[0]);
        System.out.println("lastYofDB : " + lastYofDB[0]);

        System.out.println("Fragment_5minute.lastY : " + Fragment_5minute.lastY);
        lastYofDB[0] += Fragment_5minute.lastY;

        insertData(lastXofDB[0], Fragment_5minute.lastX, lastYofDB[0]);

    }

    // save data to firebase
    private void insertData(int key, int minute, float sumOfProfit) {
        //String id = chartRef.child(String.valueOf(countOfDB++)).setValue();
        HashMap<String, Object> data = new HashMap<>();
        data.put("xValue", minute);
        data.put("yValue", sumOfProfit);
        //chartRef.child(id).updateChildren(data);
        chartRef.child(String.valueOf(++key)).updateChildren(data);
    }

    // get data from firebase
    private void retrieveData() {
        System.out.println("(Fragment_5minute)retrieveData");

        chartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

//                if (snapshot.getValue(BarChartData.class) != null) {
                    for (DataSnapshot eachSnapshot : snapshot.getChildren()) {
                        BarChartData barChartData = eachSnapshot.getValue(BarChartData.class);
                        barList.add(new BarEntry(barChartData.getxValue(), barChartData.getyValue()));
                    }

                    barDataSet.notifyDataSetChanged();
                    barData.notifyDataChanged();
                    barChart.setData(barData);
                    barChart.notifyDataSetChanged();
                    barChart.setVisibleXRangeMinimum(6);
                    barChart.setVisibleXRangeMaximum(6);
                    barChart.setVisibleXRange(0, 60);
                    System.out.println("(Fragment_SumOfProfit)barList1: " + barList);

//                } else {
//                    barChart1.clear();
//                    barChart1.invalidate();
//                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}


//    public static void calculateSumOfProfit() {
//
//        int listSize = Fragment_5minute.barList.size();
//
//        System.out.println("(Fragment_SumOfProfit) list size : " + listSize);
//
//        if (listSize != 0) {
//            //리스트 마지막 인덱스 값 가져오기
//            float lastProfit= Fragment_5minute.barList.get(listSize - 1).getY();
//
//            System.out.println("(Fragment_SumOfProfit)lastProfit: " + lastProfit);
//
//            sumOfProfit += lastProfit;
//
//            System.out.println("(Fragment_SumOfProfit)sumOfProfit: " + sumOfProfit);
//
//            BarData barData = barChart1.getData(); // get data first
//
//
//            if (barData == null) {
//                barData = new BarData();
//                barChart1.setData(barData);
//            }
//
//            if (barDataSet == null) {
//                barDataSet = new BarDataSet(null, "sum of profit");
//                barData.addDataSet(barDataSet);
//            }
//
//            // get minute
//            int minute = (int)(Fragment_5minute.barList.get(listSize - 1).getX());
//
//
//
//            barList1.add(new BarEntry(minute, sumOfProfit));
//
//            barData.notifyDataChanged();
//            barChart1.setData(barData);
//            barChart1.notifyDataSetChanged();
//            barChart1.setVisibleXRangeMinimum(6);
//            barChart1.setVisibleXRangeMaximum(6);
//            barChart1.setVisibleXRange(0, 30);
//            barChart1.notifyDataSetChanged();
//            barChart1.invalidate();
//
//            insertData(Fragment_5minute.lastX, sumOfProfit);
//            insertData(minute, sumOfProfit);
//        }
//    }