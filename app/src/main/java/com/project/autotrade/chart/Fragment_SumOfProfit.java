package com.project.autotrade.chart;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.data.BarDataSet;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.autotrade.R;

import org.jetbrains.annotations.NotNull;

public class Fragment_SumOfProfit extends Fragment {

    private static float sumOfProfit;
    private static float preProfit;

    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference chartRef;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            firebaseDatabase = FirebaseDatabase.getInstance();
            chartRef = firebaseDatabase.getReference("ChartValues");
            calculateSumOfProfit();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment__sum_of_profit, container, false);
    }
    private void calculateSumOfProfit() {
        chartRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                if (snapshot.getValue(BarChartData.class) != null) {
                    BarChartData barChartData = snapshot.getValue(BarChartData.class);
                    float newProfit = barChartData.getyValue();
                    sumOfProfit += newProfit;
                    System.out.println("newProfit" + newProfit);
                    System.out.println("sumOfProfit" + sumOfProfit);
                }
            }
            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
            }
            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

//        System.out.println(Fragment_5minute.barList);
//
//        int listSize = Fragment_5minute.barList.size();
//
//        if (listSize != 0) {
//
//            float lastProfit= Fragment_5minute.barList.get(listSize - 1).getY();
//
//            System.out.println(lastProfit);
//
//            sumOfProfit += lastProfit;
//
//            //System.out.println(sumOfProfit);
//        }
    }
}