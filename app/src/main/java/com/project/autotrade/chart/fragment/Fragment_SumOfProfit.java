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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Arrays;
import java.util.HashMap;

public class Fragment_SumOfProfit extends Fragment {

    View view;

    // bar chart
    private static BarChart barChart;
    public static ArrayList<BarEntry> barList = new ArrayList<>();
    private static BarDataSet barDataSet = new BarDataSet(barList, "sum of 5 minutes profit");
    private static BarData barData = new BarData(barDataSet);

    private final Object TAG = null;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference chartSumsRef;
    private DatabaseReference chart5minutesRef;
    private DatabaseReference lastRef;
    private String currentUserID;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_sum_of_profit, container, false);

        barChart = (BarChart) view.findViewById(R.id.bar_chart_sum_of_profit);

        currentUserID = Arrays.stream(FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")).findFirst().get();
        firebaseDatabase = FirebaseDatabase.getInstance();
        chartSumsRef = firebaseDatabase.getReference().child(currentUserID).child("Chart - Sum of profit");
        chart5minutesRef = firebaseDatabase.getReference().child(currentUserID).child("Chart - 5 minutes");

        retrieveData();

        // Inflate the layout for this fragment
        return view;
    }

    // get data from firebase
    private void retrieveData() {

        chartSumsRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot eachSnapshot : snapshot.getChildren()) {
                        BarChartData barChartData = eachSnapshot.getValue(BarChartData.class);
                        barList.add(new BarEntry(barChartData.getxValue(), barChartData.getyValue()));
                    }
                    initialize();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        Query lastQuery = chartSumsRef.orderByKey().limitToLast(1);
        lastQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for (DataSnapshot lastChild : snapshot.getChildren()) {
                    BarChartData barChartData = lastChild.getValue(BarChartData.class);

                    if (barChartData.getxValue() == 0) {
                        barList.clear();
                        barChart.clear();
                        barList.add(new BarEntry(barChartData.getxValue(), barChartData.getyValue()));
                        initialize();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void initialize() {

        barDataSet = new BarDataSet(barList, "sum of 5 minutes profit");
        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        barDataSet.setValueTextColor(Color.RED);
        barDataSet.setValueTextSize(13);

        barData = new BarData(barDataSet);
        barData.setBarWidth(5f);

        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getDescription().setText("");
        barChart.setVisibleXRangeMaximum(6);
//        barChart.setVisibleXRange(0, 60);
        barChart.animateY(1000);
        barChart.moveViewTo(barData.getEntryCount(), 50f, YAxis.AxisDependency.LEFT);
        barChart.notifyDataSetChanged();
        barChart.invalidate();
        barChart.getXAxis().setTextSize(11);
        barChart.getXAxis().setTextColor(Color.BLUE);
        barChart.getAxisLeft().setTextColor(getResources().getColor(R.color.blue_700));
        barChart.getAxisRight().setTextColor(getResources().getColor(R.color.blue_700));
        barChart.getAxisLeft().setTextSize(13);
        barChart.getAxisRight().setTextSize(13);
        barChart.getRendererLeftYAxis();

        XAxis x = barChart.getXAxis();
        x.setAxisMaxValue(60);
        x.setAxisMinValue(0);
    }
}