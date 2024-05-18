package com.example.insight;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class RegisteringActivity extends AppCompatActivity {

    private EditText editTestRegisterFullName,editTestRegisterEmail,editTestRegisterDoB,editTestRegisterMobile,editTestRegisterPwd,editTestRegisterConfirmPwd;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;

    private RadioButton radioButtonRegisterGenderSelected;
    private static final String TAG="RegisteringActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registering);

        //getSupportActionBar().setTitle("Register");
        Toast.makeText(RegisteringActivity.this,"You can register now",Toast.LENGTH_LONG).show();
        progressBar = findViewById(R.id.progress_bar);
        editTestRegisterFullName= findViewById(R.id.editText_register_full_name);
        editTestRegisterEmail=findViewById(R.id.editText_register_email);
        editTestRegisterDoB=findViewById(R.id.editText_register_dob);
        editTestRegisterMobile=findViewById(R.id.editText_register_mobile);
        editTestRegisterPwd=findViewById(R.id.editText_register_password);
        editTestRegisterConfirmPwd=findViewById(R.id.editText_register_confirm_password);
        progressBar=findViewById(R.id.progress_bar);
        radioGroupRegisterGender=findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();

        Button buttonRegister= findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected = findViewById(selectedGenderId);

                String textFullName = editTestRegisterFullName.getText().toString();
                String textEmail = editTestRegisterEmail.getText().toString();
                String textDob = editTestRegisterDoB.getText().toString();
                String textMobile = editTestRegisterMobile.getText().toString();
                String textPwd = editTestRegisterPwd.getText().toString();
                String textConfirmPwd = editTestRegisterConfirmPwd.getText().toString();
                String textGender;

                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(RegisteringActivity.this, "Please Enter Your name", Toast.LENGTH_LONG).show();
                    editTestRegisterFullName.setError("Full name is required");
                    editTestRegisterFullName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(RegisteringActivity.this, "Please Enter Your Email", Toast.LENGTH_LONG).show();
                    editTestRegisterEmail.setError("Email is required");
                    editTestRegisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(RegisteringActivity.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    editTestRegisterEmail.setError("Valid email is required");
                    editTestRegisterEmail.requestFocus();
                } else if (TextUtils.isEmpty(textDob)) {
                    Toast.makeText(RegisteringActivity.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
                    editTestRegisterDoB.setError("Your date of birth is required");
                    editTestRegisterDoB.requestFocus();
                } else if (radioGroupRegisterGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegisteringActivity.this, "Please enter your gender", Toast.LENGTH_LONG).show();
                    radioButtonRegisterGenderSelected.setError("Your gender is required");
                    radioButtonRegisterGenderSelected.requestFocus();
                } else if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(RegisteringActivity.this, "Please Enter Your Phone", Toast.LENGTH_LONG).show();
                    editTestRegisterMobile.setError("Mobile is required");
                    editTestRegisterMobile.requestFocus();
                } else if( textMobile.length()!=10){
                    Toast.makeText(RegisteringActivity.this,"Please enter a valid phone number",Toast.LENGTH_LONG).show();
                    editTestRegisterMobile.setError("Valid Mobile is required(10 digits)");
                    editTestRegisterMobile.requestFocus();
                }else if (TextUtils.isEmpty(textPwd)){
                    Toast.makeText(RegisteringActivity.this,"Please enter your password",Toast.LENGTH_LONG).show();
                    editTestRegisterPwd.setError("Password is required");
                    editTestRegisterPwd.requestFocus();
                }else if( textPwd.length()<6) {
                    Toast.makeText(RegisteringActivity.this, "Password should be at least 6 digits", Toast.LENGTH_LONG).show();
                    editTestRegisterPwd.setError("Password is too weak");
                    editTestRegisterPwd.requestFocus();
                }else if (TextUtils.isEmpty(textConfirmPwd)){
                    Toast.makeText(RegisteringActivity.this,"Please re-nter your password",Toast.LENGTH_LONG).show();
                    editTestRegisterConfirmPwd.setError("Password confirmation is required");
                    editTestRegisterConfirmPwd.requestFocus();
                }else if( !textPwd.equals(textConfirmPwd) ) {
                    Toast.makeText(RegisteringActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                    editTestRegisterConfirmPwd.setError("Password confirmation is required");
                    editTestRegisterConfirmPwd.requestFocus();
                    editTestRegisterPwd.clearComposingText();
                    editTestRegisterConfirmPwd.clearComposingText();
                }else{

                    textGender=radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName,textEmail,textDob,textGender,textMobile,textPwd);
                }


            }
        });

    }
    //create a user
    private void registerUser(String textFullName, String textEmail, String textDoB, String textGender,String textMobile, String textPwd){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail,textPwd).addOnCompleteListener(RegisteringActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegisteringActivity.this,"User registered succesfully",Toast.LENGTH_LONG).show();
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    //send verification email
                    firebaseUser.sendEmailVerification();
                    /*
                    Intent intent = new Intent(RegisterActivity.this, UserProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();*/
                }else{
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        editTestRegisterPwd.setError("Your password is too weak!");
                        editTestRegisterPwd.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        editTestRegisterPwd.setError("Your email is invalid or already in use.");
                        editTestRegisterPwd.requestFocus();
                    }catch(FirebaseAuthUserCollisionException e){
                        editTestRegisterPwd.setError("User is already registered with this email.");
                        editTestRegisterPwd.requestFocus();
                    }catch(Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegisteringActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();

                    }
                }progressBar.setVisibility(View.GONE);
            }
        });
    }
}
