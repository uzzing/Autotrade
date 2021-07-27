package com.project.autotrade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.autotrade.chat.Adapter.GroupListAdapter;
import com.project.autotrade.chat.Adapter.GroupListItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private static GroupListAdapter groupListAdapter;
    private ArrayList<String> groupList = new ArrayList<>();
    private DatabaseReference GroupRef;
    private ActionBarDrawerToggle toggle;

    private FirebaseAuth auth;
    private DatabaseReference RootRef;

    private static int userCount;
    private HashMap<String, Integer> userCountMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        GroupRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        // get account data
        auth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();

        initializeFields();

        retrieveAndDisplayGroups();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String currentGroupName = adapterView.getItemAtPosition(position).toString();
                String userCount = String.valueOf(userCountMap.get(currentGroupName) + 1);

                Intent groupChatIntent = new Intent(ChatActivity.this, ChatRoomActivity.class);
                groupChatIntent.putExtra("groupName", currentGroupName);
                groupChatIntent.putExtra("userCount", userCount);
                startActivity(groupChatIntent);
            }
        });

    }

    private void initializeFields() {
        toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        listView = (ListView) findViewById(R.id.chat_grouplist);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(false);
//        arrayAdapter = new ArrayAdapter<>(ChatActivity.this, android.R.layout.simple_list_item_1, groupList);
//        listView.setAdapter(arrayAdapter);

        groupListAdapter = new GroupListAdapter();
        listView.setAdapter(groupListAdapter);
    }

    private void retrieveAndDisplayGroups() {

        GroupRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Set<String> set = new HashSet<>();
//                Iterator iterator = snapshot.getChildren().iterator();
//                while (iterator.hasNext()) {
//                    set.add(((DataSnapshot) iterator.next()).getKey());
//                }
//
//                groupList.clear();
//                groupList.addAll(set);
//                arrayAdapter.notifyDataSetChanged();

                if (snapshot.exists()) {
                    for (DataSnapshot eachSnapshot : snapshot.getChildren()) {
                        String groupName = eachSnapshot.getKey();
                        String userCount = eachSnapshot.child("User count").getValue().toString();
                        userCountMap.put(groupName, Integer.parseInt(userCount));
                        groupListAdapter.addItem(new GroupListItem(groupName, userCount));
                    }
                    groupListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter group name : ");

        final EditText groupNameField = new EditText(ChatActivity.this);
        groupNameField.setHint("Group chat name");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(groupName))
                    Toast.makeText(ChatActivity.this, "Please enter group Name", Toast.LENGTH_SHORT).show();
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
        RootRef.child("Groups").child(groupName).child("User count").setValue("0")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Toast.makeText(ChatActivity.this, groupName + " group is created successfully", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(ChatActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

}