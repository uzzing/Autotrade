package com.project.autotrade.chart.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.autotrade.R;
import com.project.autotrade.chart.model.BarChartData;
import com.project.autotrade.trade.AutoTrade;
import com.project.autotrade.trade.Client;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import cz.msebera.android.httpclient.util.EntityUtils;

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
        ChartRef = FirebaseDatabase.getInstance().getReference().child("5 minutes chart").child(currentUserID);

        retrieveData();

        // Inflate the layout for this fragment
        return view;
    }







}