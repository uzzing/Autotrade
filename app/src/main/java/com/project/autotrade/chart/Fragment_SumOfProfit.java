package com.project.autotrade.chart;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.autotrade.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class Fragment_SumOfProfit extends Fragment {

    View view;

    // bar chart
    private static BarChart barChart1;
    public static ArrayList<BarEntry> barList1 = new ArrayList<>();
    private static BarDataSet barDataSet = new BarDataSet(barList1, "5 minutes");
    private static BarData barData = new BarData(barDataSet);

    private static final Object TAG = null;
    private static float sumOfProfit;
    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference chartRef;
    private static DatabaseReference chart5minutesRef;
    private static DatabaseReference lastRef;

    private static float[] lastYofDB = new float[1];
    private static int[] lastKeyofDB = new int[1];
    private static Integer countOfDB = 0;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Fragment_SumOfProfit : "+"onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("Fragment_SumOfProfit : "+"onCreateView()");
        view = inflater.inflate(R.layout.fragment_sum_of_profit, container, false);

        barChart1 = (BarChart) view.findViewById(R.id.bar_chart_sum_of_profit);
        firebaseDatabase = FirebaseDatabase.getInstance();
        chartRef = firebaseDatabase.getReference("Sum of 5 minute's profit");
        lastRef = firebaseDatabase.getReference().child("Sum of 5 minute's profit");
        chart5minutesRef = firebaseDatabase.getReference("Profit per 5 minutes");

        try {

            barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
            barDataSet.setValueTextColor(Color.RED);
            barDataSet.setValueTextSize(13);
            barDataSet.notifyDataSetChanged();
            barData.notifyDataChanged();

            retrieveData();

            barChart1.setData(barData);
            barChart1.setFitBars(true);
            barChart1.getDescription().setText("Bar Chart");
            barChart1.animateY(2000);
            barChart1.moveViewTo(barData.getEntryCount(), 50f, YAxis.AxisDependency.LEFT);
            barChart1.invalidate();

        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        // Inflate the layout for this fragment
        return view;
    }

    public static void calculateSumOfProfit() {

        Query lastQuery = lastRef.orderByKey().limitToLast(1);
        lastQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for (DataSnapshot lastChild : snapshot.getChildren()) {
                    lastKeyofDB[0] = Integer.parseInt(lastChild.getKey());
                    lastYofDB[0] = lastChild.getValue(BarChartData.class).getyValue();

                    System.out.println("lastKeyofDB : " + lastKeyofDB[0]);
                    System.out.println("lastYofDB : " + lastYofDB[0]);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        System.out.println("lastKeyofDB : " + lastKeyofDB[0]);
        System.out.println("lastYofDB : " + lastYofDB[0]);

        System.out.println("Fragment_5minute.lastY : " + Fragment_5minute.lastY);
        lastYofDB[0] += Fragment_5minute.lastY;

        insertData(lastKeyofDB[0], Fragment_5minute.lastX, lastYofDB[0]);

    }

    // save data to firebase
    private static void insertData(int key, int minute, float sumOfProfit) {
        //String id = chartRef.child(String.valueOf(countOfDB++)).setValue();
        HashMap<String, Object> data = new HashMap<>();
        data.put("xValue", minute);
        data.put("yValue", sumOfProfit);
        //chartRef.child(id).updateChildren(data);
        chartRef.child(String.valueOf(++key)).updateChildren(data);
    }

    // get data from firebase
    private static void retrieveData() {
        System.out.println("(Fragment_5minute)retrieveData");

        chartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

//                if (snapshot.getValue(BarChartData.class) != null) {
                    for (DataSnapshot eachSnapshot : snapshot.getChildren()) {
                        BarChartData barChartData = eachSnapshot.getValue(BarChartData.class);
                        barList1.add(new BarEntry(barChartData.getxValue(), barChartData.getyValue()));
                    }

                    barDataSet.notifyDataSetChanged();
                    barData.notifyDataChanged();
                    barChart1.setData(barData);
                    barChart1.notifyDataSetChanged();
                    barChart1.setVisibleXRangeMinimum(6);
                    barChart1.setVisibleXRangeMaximum(6);
                    barChart1.setVisibleXRange(0, 60);
                    System.out.println("(Fragment_SumOfProfit)barList1: " + barList1);

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

    @Override
    public void onDestroy() {
        sumOfProfit = 0;
        barList1 = new ArrayList<>();
        System.out.println("Fragment_SumOfProfit : "+"onDestroy()");
        super.onDestroy();
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