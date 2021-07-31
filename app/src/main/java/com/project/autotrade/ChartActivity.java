package com.project.autotrade;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.project.autotrade.chart.fragment.Fragment_5minute;
import com.project.autotrade.chart.fragment.Fragment_SumOfProfit;

public class ChartActivity extends AppCompatActivity {

    private static final String TAG = "Main";
    int value = 0;

    // ui
    Spinner spinner;
    Fragment_5minute fragment_5minute;
    Fragment_SumOfProfit fragment_sumOfProfit;

    private ImageButton goBackToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        initialization();

        goBackToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initialization() {
        spinner = findViewById(R.id.chart_spinner);
        fragment_sumOfProfit = new Fragment_SumOfProfit();
        fragment_5minute = new Fragment_5minute();
        goBackToggle = (ImageButton) findViewById(R.id.chart_goback);
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
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.chart_fragment, fragment);
        fragmentTransaction.commit();

    }
}