package com.example.fittracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    EditText inputEmail, inputPassword;
    Button btnLogin;
    String validationPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressMessage;

    FirebaseAuth fAuth;
    FirebaseUser fUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView btn = findViewById(R.id.SignupTextview);

        inputEmail = findViewById(R.id.idEmail);
        inputPassword = findViewById(R.id.idPasswordL);
        btnLogin = findViewById(R.id.btnLogin);
        progressMessage = new ProgressDialog(this);

        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkLoginAuthentication();
            }
        });

    }

    private void checkLoginAuthentication() {
        String emailId= inputEmail.getText().toString();
        String password= inputPassword.getText().toString();

        if (!emailId.matches(validationPattern)){
            inputEmail.setError("Please enter a valid email address");
            inputEmail.requestFocus();
        }else if (password.isEmpty() || password.length()<8){
            inputPassword.setError("Password should atleast contain 8 characters");
        }else {
            progressMessage.setMessage("Login in progress");
            progressMessage.setTitle("Login");
            progressMessage.setCanceledOnTouchOutside(false);
            progressMessage.show();

            fAuth.signInWithEmailAndPassword(emailId,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        progressMessage.dismiss();
                        Toast.makeText(LoginActivity.this,"Successfully logged in", Toast.LENGTH_SHORT).show();
                        advanceToNextActivity();
                    }else {
                        progressMessage.dismiss();
                        Toast.makeText(LoginActivity.this,"" +task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }



    private void advanceToNextActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}