package com.project.autotrade;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import com.project.autotrade.autotrade.fragment.AutoTrade_1day;
import com.project.autotrade.autotrade.fragment.AutoTrade_1minute;
import com.project.autotrade.autotrade.fragment.AutoTrade_5minute;

public class AutoTradeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private ImageButton goBackToggle;

    TabLayout tabLayout;
    ViewPager viewPager;

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

        goBackToggle = (ImageButton) findViewById(R.id.autotrade_goback);

//        progressBarView = (ProgressBar) findViewById(R.id.view_progress_bar);
//        btn_start = (Button)findViewById(R.id.btn_start);
//        tv_time= (TextView)findViewById(R.id.tv_timer);
//        et_timer = (EditText)findViewById(R.id.et_timer);

        tabLayout = findViewById(R.id.autotrade_tabs);
        viewPager = findViewById(R.id.autotrade_view_pager);

        toolbar = findViewById(R.id.toolbar_crypto_autotrade);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

//        /*Animation*/
//        RotateAnimation makeVertical = new RotateAnimation(0, -90, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
//        makeVertical.setFillAfter(true);
//        progressBarView.startAnimation(makeVertical);
//        progressBarView.setSecondaryProgress(endTime);
//        progressBarView.setProgress(0);


//        btn_start.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fn_countdown();
//            }
//        });

        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("1 miunte");
        arrayList.add("5 mimutesddd");
        arrayList.add("1 day");

        prepareViewPager(viewPager, arrayList);

        //Setup with view pager
        tabLayout.setupWithViewPager(viewPager);

        goBackToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void prepareViewPager(ViewPager viewPager, ArrayList<String> arrayList) {
        AutoTradeAdapter adapter = new AutoTradeAdapter(getSupportFragmentManager());
        //Initialize main fragment
        AutoTrade_5minute autoTrade_5minute = new AutoTrade_5minute();
        //Use for loop
        for(int i=0; i<arrayList.size(); i++) {
            //Initialize bundle
            Bundle bundle = new Bundle();
            //Put String
            bundle.putString("title", arrayList.get(i));
            //Set argument
            autoTrade_5minute.setArguments(bundle);
            adapter.addFragmet(autoTrade_5minute, arrayList.get(i));
            //Define new fragment
            autoTrade_5minute = new AutoTrade_5minute();
        }
        //Set adapter
        viewPager.setAdapter(adapter);
    }

    private class AutoTradeAdapter extends FragmentPagerAdapter {

        ArrayList<String> arrayList = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();

        public void addFragmet(Fragment fragment, String title) {
            //add title
            arrayList.add(title);
            //add fragment
            fragmentList.add(fragment);
        }
        public AutoTradeAdapter(@NonNull @NotNull FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
           switch (position) {
             case 0:
                 AutoTrade_1minute autoTrade_1minute = new AutoTrade_1minute();
                return autoTrade_1minute;

            case 1:
                AutoTrade_5minute autoTrade_5minute = new AutoTrade_5minute();
                return autoTrade_5minute;

            case 2:
                AutoTrade_1day autoTrade_1day = new AutoTrade_1day();
                return autoTrade_1day;

             default:
                 return null;
         }


//            //Return fragment position
//            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            //Return fragment list size
            return fragmentList.size();
        }

        @Nullable
        @org.jetbrains.annotations.Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
            case 0:
                return "1 minute";
            case 1:
                return "5 minutes";
            case 2:
                return "1 day";
             default:
                 return null;
         }


//            //Return array list position
//            return arrayList.get(position);
        }
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
//        if (menuItem.getItemId() == R.id.autotrade) {
//            Intent intent = new Intent(getApplicationContext(), TradeActivity.class);
//            startActivity(intent);
//        }
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



//    private void fn_countdown() {
//
//        if (et_timer.getText().toString().length()>0) {
//            myProgress = 0;
//
//            try {
//                countDownTimer.cancel();
//
//            } catch (Exception e) {
//
//            }
//
//            String timeInterval = et_timer.getText().toString();
//            progress = 1;
//            endTime = Integer.parseInt(timeInterval); // up to finish time
//
//            countDownTimer = new CountDownTimer(endTime * 1000, 1000) {
//                @Override
//                public void onTick(long millisUntilFinished) {
//                    setProgress(progress, endTime);
//                    progress = progress + 1;
//                    int seconds = (int) (millisUntilFinished / 1000) % 60;
//                    int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
//                    int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
//                    String newtime = hours + ":" + minutes + ":" + seconds;
//
//                    if (newtime.equals("0:0:0")) {
//                        tv_time.setText("00:00:00");
//                    } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(minutes).length() == 1) && (String.valueOf(seconds).length() == 1)) {
//                        tv_time.setText("0" + hours + ":0" + minutes + ":0" + seconds);
//                    } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(minutes).length() == 1)) {
//                        tv_time.setText("0" + hours + ":0" + minutes + ":" + seconds);
//                    } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(seconds).length() == 1)) {
//                        tv_time.setText("0" + hours + ":" + minutes + ":0" + seconds);
//                    } else if ((String.valueOf(minutes).length() == 1) && (String.valueOf(seconds).length() == 1)) {
//                        tv_time.setText(hours + ":0" + minutes + ":0" + seconds);
//                    } else if (String.valueOf(hours).length() == 1) {
//                        tv_time.setText("0" + hours + ":" + minutes + ":" + seconds);
//                    } else if (String.valueOf(minutes).length() == 1) {
//                        tv_time.setText(hours + ":0" + minutes + ":" + seconds);
//                    } else if (String.valueOf(seconds).length() == 1) {
//                        tv_time.setText(hours + ":" + minutes + ":0" + seconds);
//                    } else {
//                        tv_time.setText(hours + ":" + minutes + ":" + seconds);
//                    }
//
//                }
//
//                @Override
//                public void onFinish() {
//                    setProgress(progress, endTime);
//
//
//                }
//            };
//            countDownTimer.start();
//        }else {
//            Toast.makeText(getApplicationContext(),"Please enter the value",Toast.LENGTH_LONG).show();
//        }
//
//    }
//
//    public void setProgress(int startTime, int endTime) {
//        progressBarView.setMax(endTime);
//        progressBarView.setSecondaryProgress(endTime);
//        progressBarView.setProgress(startTime);
//
//    }


}