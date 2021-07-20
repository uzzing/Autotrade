package com.project.autotrade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.autotrade.chart.BarChartData;
import com.project.autotrade.chart.Fragment_1day;
import com.project.autotrade.chart.Fragment_1hour;
import com.project.autotrade.chart.Fragment_5minute;
import com.project.autotrade.chart.Fragment_SumOfProfit;
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

    // ui
    Spinner spinner;
    Fragment_5minute fragment_5minute;
    Fragment_SumOfProfit fragment_sumOfProfit;
    Fragment_1hour fragment_1hour;
    Fragment_1day fragment_1day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        initialization();
    }

    private void initialization() {
        spinner = findViewById(R.id.chart_spinner);
        fragment_sumOfProfit = new Fragment_SumOfProfit();
        fragment_5minute = new Fragment_5minute();
        fragment_1hour = new Fragment_1hour();
        fragment_1day = new Fragment_1day();
        spinner();
    }

    private void spinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ChartActivity.this,
                R.layout.dropdown_item, getResources().getStringArray(R.array.charts));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0 :
                        setFragment(fragment_sumOfProfit);
                        break;
                    case 1 :
                        setFragment(fragment_5minute);
                        break;
                    case 2 :
                        setFragment(fragment_1hour);
                        break;
                    case 3 :
                        setFragment(fragment_1day);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.chart_fragment, fragment);
        fragmentTransaction.commit();

    }
}