package com.project.autotrade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.autotrade.chat.Adapter.GroupListItem;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;

public class MyPageActivity extends AppCompatActivity {
    private ImageButton goBackToggle;
    private Toolbar toolbar;
    private Button saveButton;
    private EditText EditText_AccessKey, EditText_SecretKey;
    private TextView text_AccessKey, text_SecretKey;

    private DatabaseReference keyRef;
    private String currentUserID;
    private FirebaseAuth auth;

    String Accesskey;
    String Secretkey;
    HashMap<Object, String> hashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        auth = FirebaseAuth.getInstance();
        currentUserID = Arrays.stream(auth.getCurrentUser().getEmail().split("@")).findFirst().get();
        keyRef = FirebaseDatabase.getInstance().getReference().child("Keys").child(currentUserID);

        initializeFields();

        toolbar = findViewById(R.id.toolbar_mypage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Accesskey = EditText_AccessKey.getText().toString();
                Secretkey = EditText_SecretKey.getText().toString();
                hashMap.put("Accesskey", Accesskey);
                hashMap.put("Secretkey", Secretkey);
                keyRef.setValue(hashMap);

                Toast.makeText(getApplicationContext(),"Saved AccessKey and SecretKey", Toast.LENGTH_SHORT).show();

                EditText_AccessKey.setText(null);
                EditText_SecretKey.setText(null);
            }

        });

        keyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                        String key = snapshot.getKey();
                        String accesskey = snapshot.child("Accesskey").getValue().toString();
                        String secretkey = snapshot.child("Secretkey").getValue().toString();
                        System.out.println(accesskey + "," + secretkey);
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        goBackToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initializeFields() {
        saveButton = (Button) findViewById(R.id.mypage_button);
        EditText_AccessKey = (EditText) findViewById(R.id.edittext_accesskey);
        EditText_SecretKey = (EditText) findViewById(R.id.edittext_secretkey);
        text_AccessKey = (TextView) findViewById(R.id.mypage_accesskey);
        text_SecretKey = (TextView) findViewById(R.id.mypage_secretkey);
        goBackToggle = (ImageButton) findViewById(R.id.mupage_goback);
    }
}