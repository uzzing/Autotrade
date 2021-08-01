package com.project.autotrade;

import androidx.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.project.autotrade.chat.model_group.ListViewAdapter;
import com.project.autotrade.chat.model_group.ListViewItem;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    private ImageButton goBackToggle;
    private Toolbar toolbar;
    private ListView listView;
    private ListViewAdapter ListViewAdapter;
    private ArrayList<ListViewItem> listViewItems = new ArrayList<ListViewItem>();
    private DatabaseReference GroupRef;

    private FirebaseAuth auth;
    private DatabaseReference RootRef;

    private static HashMap<String, Integer> userCountMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        auth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        GroupRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        initializeFields();

        retrieveAndDisplayGroups();

        selectGroup();
    }

    private void initializeFields() {

        // toolbar
        toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(false);

        // group list
        ListViewAdapter = new ListViewAdapter(listViewItems);
        listView = (ListView) findViewById(R.id.chat_group_listview);
        listView.setAdapter(ListViewAdapter);

        goBackToggle = (ImageButton) findViewById(R.id.chat_goback);
        goBackToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void retrieveAndDisplayGroups() {

        GroupRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    ArrayList<ListViewItem> items = new ArrayList<>();

                    for (DataSnapshot eachSnapshot : snapshot.getChildren()) {
                        String groupName = eachSnapshot.getKey();
                        String userCount = eachSnapshot.child("User count").getValue().toString();

                        items.add(new ListViewItem(groupName, userCount)); // to update group list view
                        userCountMap.put(groupName, Integer.parseInt(userCount)); // to get user count
                    }

                    // update group list view
                    listViewItems.clear();
                    listViewItems.addAll(items);
                    ListViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void selectGroup() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                ListViewItem item = (ListViewItem) adapterView.getItemAtPosition(position);
                String currentGroupName = item.getName();
                String userCount = item.getUserCount();
                String updateUserCount = String.valueOf(Integer.parseInt(userCount) + 1);

                Intent groupChatIntent = new Intent(ChatActivity.this, ChatRoomActivity.class);
                groupChatIntent.putExtra("groupName", currentGroupName);
                groupChatIntent.putExtra("userCount", updateUserCount);
                startActivity(groupChatIntent);
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