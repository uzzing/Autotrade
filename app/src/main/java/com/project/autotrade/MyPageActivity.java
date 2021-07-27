package com.project.autotrade;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MyPageActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Button saveButton;
    private EditText EditText_AccessKey, EditText_SecretKey;
    private TextView text_AccessKey, text_SecretKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        initializeFields();

        toolbar = findViewById(R.id.toolbar_mypage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void initializeFields() {
        saveButton = (Button) findViewById(R.id.mypage_button);
        EditText_AccessKey = (EditText) findViewById(R.id.edittext_accesskey);
        EditText_SecretKey = (EditText) findViewById(R.id.edittext_secretkey);
        text_AccessKey = (TextView) findViewById(R.id.mypage_accesskey);
        text_SecretKey = (TextView) findViewById(R.id.mypage_secretkey);
    }
}