package com.example.insight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LogInActivity extends AppCompatActivity {

        private EditText editTextEmail, editTextPwd;
        private ProgressBar progressBar;
        private FirebaseAuth authProfile;
        private final static String TAG="LogInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Log In");
        authProfile=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressBar);


        editTextEmail=findViewById(R.id.edit_text_login_email);
        editTextPwd=findViewById(R.id.edit_text_login_pwd);

        TextView textViewForgotPwd = findViewById(R.id.text_view_forgot_password_link);
        textViewForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LogInActivity.this,"You can now reset your password!",Toast.LENGTH_LONG).show();
                startActivity(new Intent(LogInActivity.this,ForgotPasswordActivity.class));
            }
        });

        TextView textViewRegister=findViewById((R.id.text_view_register_link));
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LogInActivity.this,RegisteringActivity.class);
                startActivity(intent);finish();
            }
        });

        ImageView imageviewShowHidePwd = findViewById(R.id.image_show_hide_pwd);
        imageviewShowHidePwd.setImageResource(R.drawable.hide_pwd);
        imageviewShowHidePwd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(editTextPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editTextPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageviewShowHidePwd.setImageResource(R.drawable.hide_pwd);
                }else{
                    editTextPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageviewShowHidePwd.setImageResource(R.drawable.show_pwd);
                }
            }
        });

        Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail= editTextEmail.getText().toString();
                String textPswd=editTextPwd.getText().toString();

                if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(LogInActivity.this, "Please enter your email!",Toast.LENGTH_SHORT).show();
                    editTextEmail.setError("Email is required");
                    editTextEmail.requestFocus();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(LogInActivity.this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
                    editTextEmail.setError("Valid Email is required");
                    editTextEmail.requestFocus();
                }else if(TextUtils.isEmpty(textPswd)) {
                    Toast.makeText(LogInActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    editTextPwd.setError("Password is required");
                    editTextPwd.requestFocus();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    logInUser(textEmail,textPswd);
                }
            }
        });


    }

    private void logInUser(String Email, String Pswd) {
        authProfile.signInWithEmailAndPassword(Email,Pswd).addOnCompleteListener(LogInActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser= authProfile.getCurrentUser();
                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(LogInActivity.this, "Log in successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LogInActivity.this,UserPageActivity.class));
                        finish();
                    }else{
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();
                        showAlertDialog();
                    }
                }else{
                    try{
                        throw  task.getException();
                    } catch (FirebaseAuthInvalidUserException e){
                        editTextEmail.setError("Invalid user");
                        editTextEmail.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        editTextEmail.setError("invalid credentials");
                        editTextEmail.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LogInActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now.");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
        });
        AlertDialog alertDialog = builder.create();
    alertDialog.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if( authProfile.getCurrentUser() != null){
            Toast.makeText(LogInActivity.this, "Already logged in",Toast.LENGTH_SHORT).show();

            //Start the user page activity
            startActivity(new Intent(LogInActivity.this,UserPageActivity.class));
            finish();
        }else{
            Toast.makeText(LogInActivity.this, "You can now log in",Toast.LENGTH_SHORT).show();
        }

    }
}