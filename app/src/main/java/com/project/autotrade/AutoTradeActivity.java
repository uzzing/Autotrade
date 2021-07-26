package com.project.autotrade;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class AutoTradeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    int myProgress = 0;
    ProgressBar progressBarView;
    Button btn_start;
    TextView tv_time;
    EditText et_timer;
    int progress;
    CountDownTimer countDownTimer;
    int endTime = 250;


    @SuppressLint("WrongViewCast")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autotrade);

        progressBarView = (ProgressBar) findViewById(R.id.view_progress_bar);
        btn_start = (Button)findViewById(R.id.btn_start);
        tv_time= (TextView)findViewById(R.id.tv_timer);
        et_timer = (EditText)findViewById(R.id.et_timer);

        toolbar = findViewById(R.id.toolbar_crypto_autotrade);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        /*Animation*/
        RotateAnimation makeVertical = new RotateAnimation(0, -90, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        makeVertical.setFillAfter(true);
        progressBarView.startAnimation(makeVertical);
        progressBarView.setSecondaryProgress(endTime);
        progressBarView.setProgress(0);


        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fn_countdown();
            }
        });
    }

    private void fn_countdown() {

        if (et_timer.getText().toString().length()>0) {
            myProgress = 0;

            try {
                countDownTimer.cancel();

            } catch (Exception e) {

            }

            String timeInterval = et_timer.getText().toString();
            progress = 1;
            endTime = Integer.parseInt(timeInterval); // up to finish time

            countDownTimer = new CountDownTimer(endTime * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    setProgress(progress, endTime);
                    progress = progress + 1;
                    int seconds = (int) (millisUntilFinished / 1000) % 60;
                    int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                    int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                    String newtime = hours + ":" + minutes + ":" + seconds;

                    if (newtime.equals("0:0:0")) {
                        tv_time.setText("00:00:00");
                    } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(minutes).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                        tv_time.setText("0" + hours + ":0" + minutes + ":0" + seconds);
                    } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(minutes).length() == 1)) {
                        tv_time.setText("0" + hours + ":0" + minutes + ":" + seconds);
                    } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                        tv_time.setText("0" + hours + ":" + minutes + ":0" + seconds);
                    } else if ((String.valueOf(minutes).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                        tv_time.setText(hours + ":0" + minutes + ":0" + seconds);
                    } else if (String.valueOf(hours).length() == 1) {
                        tv_time.setText("0" + hours + ":" + minutes + ":" + seconds);
                    } else if (String.valueOf(minutes).length() == 1) {
                        tv_time.setText(hours + ":0" + minutes + ":" + seconds);
                    } else if (String.valueOf(seconds).length() == 1) {
                        tv_time.setText(hours + ":" + minutes + ":0" + seconds);
                    } else {
                        tv_time.setText(hours + ":" + minutes + ":" + seconds);
                    }

                }

                @Override
                public void onFinish() {
                    setProgress(progress, endTime);


                }
            };
            countDownTimer.start();
        }else {
            Toast.makeText(getApplicationContext(),"Please enter the value",Toast.LENGTH_LONG).show();
        }

    }

    public void setProgress(int startTime, int endTime) {
        progressBarView.setMax(endTime);
        progressBarView.setSecondaryProgress(endTime);
        progressBarView.setProgress(startTime);

    }

    private void setUpNavigationDrawer() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(
                this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        {
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//                isDrawerOpened = true;
//            }
//
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                super.onDrawerClosed(drawerView);
//                isDrawerOpened = false;
//            }
//        };
        drawer.addDrawerListener(toggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {
        int id = menuItem.getItemId();

//        if (menuItem.getItemId() == R.id.home) {
//            Intent intent = new Intent(getApplicationContext(), TradeActivity.class);
//            startActivity(intent);
//        }
        if (menuItem.getItemId() == R.id.autotrade) {
            Intent intent = new Intent(getApplicationContext(), TradeActivity.class);
            startActivity(intent);
        }
//        if (menuItem.getItemId() == R.id.chat) {
//            Intent intent = new Intent(getApplicationContext(), TradeActivity.class);
//            startActivity(intent);
//        }
        if (menuItem.getItemId() == R.id.trade_chart) {
            Intent intent = new Intent(getApplicationContext(), ChartActivity.class);
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.mypage) {
            Intent intent = new Intent(getApplicationContext(), AutoTradeActivity.class);
            startActivity(intent);
        }

//        if (id == R.id.main_layout) {
//            //getFragmentManager().beginTransaction().replace(R.id.main_tabs_pager, new ChatsFragment()).commit();
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }
}