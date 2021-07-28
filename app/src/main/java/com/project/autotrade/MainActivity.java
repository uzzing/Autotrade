package com.project.autotrade;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
//    private TabsAccessorAdapter tabsAccessorAdapter;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference RootRef;

    private ActionBarDrawerToggle toggle;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar_mywallet);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // get account data
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();

        // toolbar
//        getSupportActionBar().setTitle("AutoTradeApp");
        setUpNavigationDrawer();

        initializeFields();

    }

    private void initializeFields() {


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

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, com.project.autotrade.LoginActivity.class);
        startActivity(loginIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

//        if (toggle.onOptionsItemSelected(item)) return true;
//        return super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_option) {
            auth.signOut();
            sendUserToLoginActivity();
        }

        if (item.getItemId() == R.id.main_create_group_option) {
            requestNewGroupChat();
        }

        return true;
    }

    private void requestNewGroupChat() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter group name : ");

        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("Group chat name");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(groupName))
                    Toast.makeText(MainActivity.this, "Please enter group Name", Toast.LENGTH_SHORT).show();
                else
                    createNewGroup(groupName);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void createNewGroup(String groupName) {
        RootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Toast.makeText(MainActivity.this, groupName + " group is created successfully", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setUpNavigationDrawer() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle (
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

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.drawer_icon);


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

        if (menuItem.getItemId() == R.id.home) {
            Intent intent = new Intent(getApplicationContext(), AutoTradeActivity.class);
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.autotrade) {
            Intent intent = new Intent(getApplicationContext(), TradeActivity.class);
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

//        if (id == R.id.main_layout) {
//            //getFragmentManager().beginTransaction().replace(R.id.main_tabs_pager, new ChatsFragment()).commit();
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}