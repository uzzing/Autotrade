package com.project.autotrade;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    // ui
    private ImageButton goBackToggle;
    private Toolbar toolbar;
    private TextView userCountView;
    private ImageButton sendMessageButton, sendImageButton;
    private EditText messageInput;
    private ScrollView scrollView;
    private ProgressDialog loadingBar;

    // get user count
    private static String userCount;

    // get user info
    private FirebaseAuth auth;
    private DatabaseReference UserRef, UserCountRef, GroupNameRef, GroupMessageKeyRef;
    private String currentGroupName;
    private String currentUserID;

    // get date and time
    private String currentDate;
    private String currentTime;

    // show messages
    private final List<MessageItem> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView messageListView;

    // send image
    private String checker = "", myUrl = "";
    private UploadTask uploadTask;
    private Uri fileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        // show groupName on alert
        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(this, "You joined " + currentGroupName, Toast.LENGTH_SHORT).show();

        // get user count
        userCount = getIntent().getExtras().get("userCount").toString();

        // set user, group database
        auth = FirebaseAuth.getInstance();
        currentUserID = Arrays.stream(auth.getCurrentUser().getEmail().split("@")).findFirst().get();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        UserCountRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName).child("User count");
        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName).child("Messages");

        initializeFields();

        getUserInfo();

        updateUserCount();

        sendingButtons();

        leaveChatRoom();

        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                messageAdapter = new MessageAdapter(messagesList, currentGroupName);
                MessageItem messages = snapshot.getValue(MessageItem.class);
                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();
                messageListView.setAdapter(messageAdapter);
                messageListView.scrollToPosition(messagesList.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) { }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) { }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) { }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });
    }

    private void initializeFields() {

        // toolbar - toggle that can go chat home back
        goBackToggle = (ImageButton) findViewById(R.id.chatroom_goback);

        // toolbar - name
        toolbar = (Toolbar) findViewById(R.id.chatroom_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentGroupName);

        // toolbar - user count
        userCountView = (TextView) findViewById(R.id.chatroom_people);
        userCountView.setText(userCount);
        UserCountRef.setValue(userCount);

        // show message list
        messageListView = (RecyclerView) findViewById(R.id.all_message_display);
        linearLayoutManager = new LinearLayoutManager(this);
        messageListView.setLayoutManager(linearLayoutManager);

        // the below
        sendImageButton = (ImageButton) findViewById(R.id.chat_send_image);
        messageInput = (EditText) findViewById(R.id.input_message);
        sendMessageButton = (ImageButton) findViewById(R.id.chat_send_message);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);

        // loading bar
        loadingBar = new ProgressDialog(this);

    }

    private void updateUserCount() {

        UserCountRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    String changedUserCount = snapshot.getValue().toString();
                    userCount = changedUserCount;
                    userCountView.setText(userCount);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    // for saveMessageInfoToDatabase()
    private void getUserInfo() {

        UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // didn't program
                if (snapshot.exists()) {
                    currentUserID = snapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendingButtons() {

        // send message button
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMessageInfoToDatabase();
                messageInput.setText("");
            }
        });

        // send image button
        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // select file format
                CharSequence options[] = new CharSequence[] { "Image" };

                // show options in alert
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoomActivity.this);

                builder.setTitle("Select the file");

                // go to next step after selecting file format in alert
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (i == 0) {
                            checker = "image";

                            // go to select image in device
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/");

                            // choose an image in images app
                            startActivityForResult(Intent.createChooser(intent, "Select image"), 438);

                        }
                    }
                });

                builder.show();

            }
        });
    }

    private void leaveChatRoom() {

        goBackToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userCount = String.valueOf(Integer.parseInt(userCount) - 1);
                UserCountRef.setValue(userCount);
                Toast.makeText(getApplicationContext(), "You left " + currentGroupName, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // save new text message data
    private void saveMessageInfoToDatabase() {

        String message = messageInput.getText().toString();
        String messageKey = GroupNameRef.push().getKey(); // the primary key of each message

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Please write message first", Toast.LENGTH_SHORT).show();
        } else {

            // get date and time
            getNow();

            // add new message in group
            HashMap<String, Object> groupMessageKey = new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey); // make new message data in group
            GroupMessageKeyRef = GroupNameRef.child(messageKey); // set the primary key of message

            // add new message info to new group info
            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserID); // getUserInfo()
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);
            messageInfoMap.put("type", "text");
            GroupMessageKeyRef.updateChildren(messageInfoMap);

        }
    }

    // send image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // if file is image
        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            {
                // loading
                loadingBar.setTitle("Sending File");
                loadingBar.setMessage("Please wait");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                // get file address
                fileUri = data.getData();

                // save new image message data
                if (checker.equals("image")) {

                    // set the place where image file will be stored in firebase
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image file");

                    // the primary key of each message
                    final String messageKey = GroupNameRef.push().getKey();

                    // add new message in group
                    HashMap<String, Object> groupMessageKey = new HashMap<>();
                    GroupNameRef.updateChildren(groupMessageKey); // make new message data in group
                    GroupMessageKeyRef = GroupNameRef.child(messageKey); // set the primary key of message

                    // set file type in storageReference
                    StorageReference filePath = storageReference.child(messageKey + ".jpg");

                    // upload file
                    uploadTask = filePath.putFile(fileUri);

                    // task process
                    uploadTask.continueWithTask(new Continuation() {
                        @Override
                        public Object then(@NonNull @NotNull Task task) throws Exception {

                            // task process 1. if task is successful, return file url
                            if (!task.isSuccessful()) {
                                task.getException();
                            }

                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Uri> task) {

                            // task process 2. save new message data in firebase 'group' after step 1
                            if (task.isSuccessful()) {

                                getNow();

                                Uri downloadUrl = task.getResult();
                                myUrl = downloadUrl.toString(); // file url in firebase

                                // save new message data in firebase 'group'
                                HashMap<String, Object> messageInfoMap = new HashMap<>();
                                messageInfoMap.put("name", currentUserID); // getUserInfo()
                                messageInfoMap.put("message", myUrl);
                                messageInfoMap.put("file name", fileUri.getLastPathSegment()); // only for sending files
                                messageInfoMap.put("date", currentDate);
                                messageInfoMap.put("time", currentTime);
                                messageInfoMap.put("type", checker);
                                GroupMessageKeyRef.updateChildren(messageInfoMap);

                                Toast.makeText(ChatRoomActivity.this, "Sent Image successfully", Toast.LENGTH_SHORT).show();
                            }
                            else { // if it's not successful
                                String message = task.getException().toString();
                                Toast.makeText(ChatRoomActivity.this, "Error : " + message, Toast.LENGTH_LONG).show();
                            }
                            loadingBar.dismiss();
                            messageInput.setText(""); // reset message box
                        }
                    });
                }
                else if (!checker.equals("image")) {

                }
                else {
                    loadingBar.dismiss();
                    Toast.makeText(this, "Nothing selected, Error", Toast.LENGTH_SHORT).show();
                }


            }
        }
    }

    private void getNow() {

        // get date and time
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy MM dd");
        currentDate = simpleDateFormat.format(calendar1.getTime());

        Calendar calendar2 = Calendar.getInstance();
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm a");
        currentTime = simpleTimeFormat.format(calendar2.getTime());
    }
}