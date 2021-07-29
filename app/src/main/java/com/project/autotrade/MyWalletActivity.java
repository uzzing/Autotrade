package com.project.autotrade;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.project.autotrade.chat.message.MyData;
import com.project.autotrade.trade.GetCurrent;
import com.project.autotrade.trade.AutoTrade;
import com.project.autotrade.trade.Client;
import com.project.autotrade.trade.GetJson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

import cz.msebera.android.httpclient.util.EntityUtils;

public class MyWalletActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // toolbar
    private Toolbar toolbar;

    // balance
    private TextView balanceTextView;
    private String strBalance;

    // recent trades
    private ScrollView scrollView;
    private RecyclerView tradeListView;
    private LinearLayoutManager linearLayoutManager;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference RootRef;


    private ActionBarDrawerToggle toggle;
    public MyWalletActivity() { }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);

        // get account data
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();

        getBalance();
        initializeFields();

        // navigation drawer
        setUpNavigationDrawer();

    }

    private void getBalance() {

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                double parseDouble = Double.valueOf(String.valueOf(strBalance));
                String balance = String.format("%.2f", parseDouble);
                balanceTextView.setText(balance);
            }
        };

        new Thread() {
            public void run() {
                try {
                    strBalance = new GetJson().getBalance("KRW");
                    System.out.println(strBalance);
                    Message msg = handler.obtainMessage();
                    handler.sendMessage(msg);
                }
                catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void initializeFields() {

        // toolbar
        toolbar = findViewById(R.id.toolbar_mywallet);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // balance
        balanceTextView = (TextView) findViewById(R.id.mywallet_balance);
        balanceTextView.setText(strBalance);

        // trade list
        scrollView = (ScrollView) findViewById(R.id.scroll_view_recent_trade);
        tradeListView = (RecyclerView) findViewById(R.id.recent_trade_display);
        linearLayoutManager = new LinearLayoutManager(this);
        tradeListView.setLayoutManager(linearLayoutManager);
    }

    private void getRecentTrade() {

        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("uuid", AutoTrade.buyUUID);
            Client client = new Client();
            String data = EntityUtils.toString(client.getOrderInfo(params));


        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null) {
            sendUserToLoginActivity();
        } else {
            MyData.name = Arrays.stream(currentUser.getEmail().split("@")).findFirst().get();
        }
    }

    /**
    the menu on the top right -> logout
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu_mywallet, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.mywallet_ordercoin) {
            Intent intent = new Intent(getApplicationContext(), TradeActivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.mywallet_getcurrent) {
            Intent intent = new Intent(getApplicationContext(), GetCurrent.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.mywallet_logout_option) {
            auth.signOut();
            sendUserToLoginActivity();
        }
        return true;
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MyWalletActivity.this, com.project.autotrade.LoginActivity.class);
        startActivity(loginIntent);
    }


    /**
    the navigation drawer on the top left
     **/
    private void setUpNavigationDrawer() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle (
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toggle.syncState();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.drawer_icon);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (menuItem.getItemId() == R.id.home) {
            Intent intent = new Intent(getApplicationContext(), MyWalletActivity.class);
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.autotrade) {
            Intent intent = new Intent(getApplicationContext(), AutoTradeActivity.class);
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.chat) {
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.trade_chart) {
            Intent intent = new Intent(getApplicationContext(), ChartActivity.class);
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.mypage) {
            Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
}