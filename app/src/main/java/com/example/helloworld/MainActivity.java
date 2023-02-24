package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helloworld.Adapters.MessageAdapter;
import com.example.helloworld.Models.Message;
import com.example.helloworld.Models.Users;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private EditText message;
    private RelativeLayout send;
    private MaterialTextView title, members;
    private ImageView more;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;

    private ArrayList<Message> messages = new ArrayList<>();
    private MessageAdapter messageAdapter;

    private SharedPreferences sharedPreferences;

    private ArrayList<Users> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerMessage);
        message = findViewById(R.id.editMessage);
        send = findViewById(R.id.btnSend);
        title = findViewById(R.id.groupName);
        members = findViewById(R.id.groupMembers);
        more = findViewById(R.id.moreOptions);
        linearLayoutManager = new LinearLayoutManager(this);
        messageAdapter = new MessageAdapter(this);
        sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);

        getMessages();

        getMembers();

        if(messages.size() > 0){
            recyclerView.scrollToPosition(messages.size() - 1);
        }

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm();
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void getMembers() {
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null){
            database = FirebaseDatabase.getInstance();
            reference = database.getReference().child("Chats");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        for (DataSnapshot snap : snapshot.getChildren()){
                            if(snap.child("userId").getValue() != null){
                                String userId = snap.child("userId").getValue().toString();
                                reference = database.getReference().child("Users").child(userId);
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                        if(snapshot2.exists()){
                                            Users users1 = snapshot2.getValue(Users.class);
                                            users.add(users1);

                                            String names = "";

                                            names.concat(users1.fullname + ", ");

                                            members.setText(users1.fullname);

                                            Log.i(TAG, "onDataChange: User info" + users1.email);
                                        }
                                        else {
                                            Toast.makeText(MainActivity.this,"User not Created",Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.i(TAG, "onCancelled: " + error);
                                    }
                                });
                            }
                        }
                    }
                    else {
                        Log.i(TAG, "onDataChange: Nothing");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.i(TAG, "onCancelled: " + error);
                }
            });
        }
    }

    private void sendMessage() {
        String mess = message.getText().toString().trim();

        DateFormat df = new SimpleDateFormat("h:mm a");
        String date = df.format(Calendar.getInstance().getTime());

        Log.i(TAG, "sendMessage: " + date + "time is " + ServerValue.TIMESTAMP);

        if(TextUtils.isEmpty(mess)){

        }
        else{
            auth = FirebaseAuth.getInstance();
            if(auth.getCurrentUser() != null){
                Message m = new Message(mess, date, ServerValue.TIMESTAMP, auth.getUid());
                database = FirebaseDatabase.getInstance();
                reference = database.getReference().child("Chats").push();
                reference.setValue(m);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
//                            messages.add(m);
//                            messageAdapter.addMessage(m, messages.size() - 1);
//                            recyclerView.scrollToPosition(messages.size() - 1);
                        }
                        else {
                            Toast.makeText(MainActivity.this,"Not sent",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i(TAG, "onCancelled: " + error);
                    }
                });
            }
            else{
                startActivity(new Intent(MainActivity.this, Login.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }
    }

    private void getMessages() {
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null){
            database = FirebaseDatabase.getInstance();
            reference = database.getReference().child("Chats");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        messages.clear();
                        messageAdapter.clearData();
                        for (DataSnapshot snap : snapshot.getChildren()){
                            Message message1 = new Message();
                            Log.i(TAG, "onDataChange: " + snap);
                            message1.setMessage(snap.child("message").getValue().toString());
                            message1.setTime(snap.child("time").getValue().toString());
                            message1.setUserId(snap.child("userId").getValue().toString());
                            messages.add(message1);
                        }
                        messageAdapter.getMessages(messages);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(messageAdapter);
                        recyclerView.scrollToPosition(messages.size() - 1);
                    }
                    else {
                        Log.i(TAG, "onDataChange: Nothing");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.i(TAG, "onCancelled: " + error);
                }
            });
        }

    }

    private void confirm() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme);
        View botView = LayoutInflater.from(this).inflate(R.layout.log_out_options, null);
        LinearLayout logOut = botView.findViewById(R.id.linearLogOut);
        bottomSheetDialog.setContentView(botView);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                auth.signOut();
                startActivity(new Intent(MainActivity.this, Login.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        bottomSheetDialog.show();

    }
}