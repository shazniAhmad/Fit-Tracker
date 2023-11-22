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

public class SignupActivity extends AppCompatActivity {

    EditText inputUsername, inputGender, inputWeight, inputHeight, inputDOB, inputEmail, inputPassword, inputCPassword;
    Button btnSignup;
    String validationPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressMessage;

    FirebaseAuth fAuth;
    FirebaseUser fUser;

    FirebaseDatabase rootDatabase;
    DatabaseReference refDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        TextView btn =findViewById(R.id.idAlreadyHaveAccount);
        inputUsername = findViewById(R.id.idUsername);
        inputGender = findViewById(R.id.idGender);
        inputWeight = findViewById(R.id.idWeight);
        inputHeight = findViewById(R.id.idHeight);
        inputDOB = findViewById(R.id.idDOB);
        inputEmail = findViewById(R.id.idEmailid);
        inputPassword = findViewById(R.id.idPassword);
        inputCPassword = findViewById(R.id.idConfirmPassword);
        btnSignup = findViewById(R.id.btnSignup);
        progressMessage = new ProgressDialog(this);

        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
            }
    });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FirebaseDatabaseService db = new FirebaseDatabaseService(getApplicationContext());
//                db.write( inputUsername + "/gender", String.valueOf(inputGender.getText()));
//                Toast.makeText(getApplicationContext(),"Bank Added Successfully",Toast.LENGTH_LONG).show();
//                finish();

                rootDatabase = FirebaseDatabase.getInstance();
                refDatabase = rootDatabase.getReference("users");

                String username = inputUsername.getText().toString();
                String gender = inputGender.getText().toString();
                String weight = inputWeight.getText().toString();
                String height = inputHeight.getText().toString();
                String dateOfBirth = inputDOB.getText().toString();
                String emailId = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();

                StoreData storeData = new StoreData(username, gender, weight, height, dateOfBirth, emailId, password);

                refDatabase.setValue(storeData);

                checkAuthentication();
            }
        });
}

    private void checkAuthentication() {
        String emailId= inputEmail.getText().toString();
        String password= inputPassword.getText().toString();
        String confirmPassword= inputCPassword.getText().toString();

        if (!emailId.matches(validationPattern)){
            inputEmail.setError("Please enter a valid email address");
            //inputEmail.requestFocus();
        }else if (password.isEmpty() || password.length()<8){
            inputPassword.setError("Password should atleast contain 8 characters");
        }else if (!password.matches(confirmPassword)){
            inputCPassword.setError("Passwords do not match");
        }else {
            progressMessage.setMessage("Registration is in progress");
            progressMessage.setTitle("Registration");
            progressMessage.setCanceledOnTouchOutside(false);
            progressMessage.show();

            fAuth.createUserWithEmailAndPassword(emailId,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        progressMessage.dismiss();
                        Toast.makeText(SignupActivity.this,"Successfully registered", Toast.LENGTH_SHORT).show();
                        advanceToNextActivity();
                    }else {
                        progressMessage.dismiss();
                        Toast.makeText(SignupActivity.this,"" +task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void advanceToNextActivity() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}