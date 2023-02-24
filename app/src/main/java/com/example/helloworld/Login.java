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

import com.example.helloworld.Models.Message;
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

public class Login extends AppCompatActivity {
    private static final String TAG = "Login";
    TextInputEditText email,pass;
    TextView register;
    MaterialButton btn;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private FirebaseDatabase database;
    private DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        btn= findViewById(R.id.logging);
        register= findViewById(R.id.signin);

        firebaseAuth = FirebaseAuth.getInstance();


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginMethod();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Registration.class));
            }
        });
    }
    private void loginMethod() {
        user = firebaseAuth.getCurrentUser();
        if (user == null){
            String lEmail = email.getText().toString().trim();
            String lPassword = pass.getText().toString().trim();

            if (TextUtils.isEmpty(lEmail)){
                email.setError("Enter Email");
            }
            else if (TextUtils.isEmpty(lPassword)){
                pass.setError("Enter Password");
            }
            else{
                btn.setEnabled(false);
                firebaseAuth.signInWithEmailAndPassword(lEmail, lPassword)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                btn.setEnabled(true);
                                if (task.isSuccessful()){
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    finish();
                                }
                                else {
                                    Toast.makeText(Login.this, "Confirm Email and Password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(Login.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                btn.setEnabled(true);
                                Log.i(TAG, "onFailure: "+ e);
                            }
                        });
            }

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        if (user != null){
            startActivity(new Intent(Login.this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }
}