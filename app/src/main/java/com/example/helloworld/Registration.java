package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helloworld.Models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Registration extends AppCompatActivity {
    private static final String TAG = "Registration";
    TextInputEditText fullnames,emaill,phonenumbers,passwad,confirm;
    MaterialButton submitbtn;
    TextView login;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        fullnames = findViewById(R.id.fullnames);
        emaill = findViewById(R.id.emailing);
        phonenumbers = findViewById(R.id.phonenumbers);
        passwad = findViewById(R.id.passwad);
        confirm = findViewById(R.id.confirm);
        submitbtn =findViewById(R.id.submiting);
        login =findViewById(R.id.log);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseAuth.getCurrentUser() == null){
                    registerUser();
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registration.this, Login.class));
            }
        });
    }

    private void registerUser() {
        user = firebaseAuth.getCurrentUser();
        if (user == null){
            String fullname = fullnames.getText().toString().trim();
            String lEmail = emaill.getText().toString().trim();
            String phonenumber = phonenumbers.getText().toString().trim();
            String lpassword = passwad.getText().toString().trim();
            String lconfirmation = confirm.getText().toString().trim();
             if (TextUtils.isEmpty(fullname)){
                fullnames.setError("Enter Fullname");
            }
             else if (TextUtils.isEmpty(lEmail)){
                emaill.setError("Enter Email");
            }
             else if (TextUtils.isEmpty(phonenumber)){
                 phonenumbers.setError("Enter Phonenumber");
             }
            else if (TextUtils.isEmpty(lpassword)){
                passwad.setError("Enter Password");
            }
            else if (TextUtils.isEmpty(lconfirmation)){
                confirm.setError("Confirm Password");
            }
             else if (!lconfirmation.equalsIgnoreCase(lpassword)){
                 confirm.setError("Passwords do not match");
             }
             else {
                 submitbtn.setEnabled(false);
                 FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

                 DatabaseReference checkreference = FirebaseDatabase.getInstance().getReference("blacklist");
                 checkreference.orderByChild("email").equalTo(lEmail).addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                         if (snapshot.exists()){
                             submitbtn.setEnabled(true);
                             Toast.makeText(Registration.this, "This email has been blacklisted", Toast.LENGTH_SHORT).show();
                         }
                         else {
                             saveInfo(lEmail,lpassword);
                         }

                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError error) {
                         submitbtn.setEnabled(true);

                     }
                 });


            }
        }

    }

    private void saveInfo(String lEmail,String lpassword) {

        firebaseAuth.createUserWithEmailAndPassword(lEmail, lpassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            sendData();

                        }
                        else {
                            submitbtn.setEnabled(true);
                            Toast.makeText(Registration.this, "Registration Unsuccessful, Check network Connection", Toast.LENGTH_SHORT).show();
                        }
                        task.addOnFailureListener(Registration.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                submitbtn.setEnabled(true);
                                Log.i(TAG, "onFailure: "+ e.getMessage());
                            }
                        });
                    }

                });
    }

    private void sendData() {
        String fullname = fullnames.getText().toString().trim();
        String lEmail = emaill.getText().toString().trim();
        String phonenumber = phonenumbers.getText().toString().trim();

        if (TextUtils.isEmpty(fullname)){
            fullnames.setError("Enter Fullname");
        }
        else if (TextUtils.isEmpty(lEmail)){
            emaill.setError("Enter Email");
        }
        else if (TextUtils.isEmpty(phonenumber)){
            phonenumbers.setError("Enter Phone number");
        }
        else {
            databaseReference = firebaseDatabase.getReference().child("Users").child(firebaseAuth.getUid());
            Users users = new Users(fullname,lEmail,phonenumber);
            databaseReference.setValue(users);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    submitbtn.setEnabled(true);
                    if(snapshot.exists()){
                        startActivity(new Intent(Registration.this, MainActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                    else {
                        Toast.makeText(Registration.this,"User not Created",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    submitbtn.setEnabled(true);
                    Log.i(TAG, "onCancelled: " + error);
                }
            });

        }
    }



}