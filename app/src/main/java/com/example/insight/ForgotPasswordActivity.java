package com.example.insight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {
private Button buttonPwdReset;
private EditText editTextPwdResetEmail;
private ProgressBar progressBar;
private FirebaseAuth authProfile;
private final static String TAG ="ForgotPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Forgot Password");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_forgot_password);


        editTextPwdResetEmail=findViewById(R.id.edit_text_password_reset);
        buttonPwdReset=findViewById(R.id.button_password_reset);
        progressBar=findViewById(R.id.progressBar);

        buttonPwdReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextPwdResetEmail.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your email",Toast.LENGTH_LONG).show();
                    editTextPwdResetEmail.setError("Email is required");
                    editTextPwdResetEmail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter a valid email",Toast.LENGTH_LONG).show();
                    editTextPwdResetEmail.setError("Valid email is required");
                    editTextPwdResetEmail.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    resetPwd(email);
                }
            }
        });
    }

    private void resetPwd(String email) {
        authProfile = FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this, "Please check your email inbox",Toast.LENGTH_LONG).show();

                Intent intent = new Intent(ForgotPasswordActivity.this, LoggingInActivityMainScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                }else {
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        editTextPwdResetEmail.setError("User does not exist");
                        editTextPwdResetEmail.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(ForgotPasswordActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
                    }

                }
                progressBar.setVisibility(View.GONE);
            }

        });
    }


}