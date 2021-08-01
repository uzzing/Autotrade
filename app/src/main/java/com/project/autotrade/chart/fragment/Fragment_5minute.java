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
import com.google.firebase.database.ValueEventListener;
import com.project.autotrade.R;
import com.project.autotrade.chart.model.BarChartData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class Fragment_5minute extends Fragment {

    View view;

    // bar chart
    private static BarChart barChart;
    private static ArrayList<BarEntry> barList = new ArrayList<>();
    private static BarDataSet barDataSet = new BarDataSet(barList, "5 minutes");
    private static BarData barData = new BarData(barDataSet);

    // insert data to firebase
    private DatabaseReference ChartRef;
    private String currentUserID;

    // send datas to Fragement_SumOfProfit
    public static int lastX;
    public static float lastY;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup convertView,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_5minute, convertView, false);
        barChart = (BarChart) view.findViewById(R.id.bar_chart_5minute);

        currentUserID = Arrays.stream(FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")).findFirst().get();
        ChartRef = FirebaseDatabase.getInstance().getReference().child(currentUserID).child("Chart - 5 minutes");

        retrieveData();

        // Inflate the layout for this fragment
        return view;
    }

    // get data from firebase
    private void retrieveData() {
        ChartRef.addValueEventListener(new ValueEventListener() {
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
    }

    private void initialize() {

        barDataSet = new BarDataSet(barList, "5 minutes");
        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        barDataSet.setValueTextColor(Color.RED);
        barDataSet.setValueTextSize(13);

        barData = new BarData(barDataSet);
        barData.setBarWidth(5f);

        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getDescription().setText("");
        barChart.animateY(1000);
        barChart.moveViewTo(barData.getEntryCount(), 50f, YAxis.AxisDependency.LEFT);
        barChart.getXAxis().setTextSize(11);
        barChart.getXAxis().setTextColor(R.color.black);
        barChart.getAxisLeft().setTextColor(getResources().getColor(R.color.blue_700));
        barChart.getAxisRight().setTextColor(getResources().getColor(R.color.white));
        barChart.getAxisLeft().setTextSize(13);
        barChart.getRendererLeftYAxis();

        XAxis x = barChart.getXAxis();
        x.setLabelCount(6);
        x.setAxisMaxValue(60);
        x.setAxisMinValue(0);

        barChart.notifyDataSetChanged();
        barChart.invalidate();

    }
}