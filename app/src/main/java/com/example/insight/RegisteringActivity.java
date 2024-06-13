package com.example.insight;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisteringActivity extends AppCompatActivity {

    private EditText editTestRegisterFullName,editTestRegisterEmail,editTestRegisterDoB,editTestRegisterMobile,editTestRegisterPwd,editTestRegisterConfirmPwd;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private DatePickerDialog picker;
    private RadioButton radioButtonRegisterGenderSelected;
    private SwipeRefreshLayout swipeContainer;
    private static final String TAG="RegisteringActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registering);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Register yourself");
        swipeToRefresh();

        Toast.makeText(RegisteringActivity.this,"You can register now",Toast.LENGTH_LONG).show();
        progressBar = findViewById(R.id.progressBar);


        editTestRegisterFullName= findViewById(R.id.edit_text_register_full_name);
        editTestRegisterEmail=findViewById(R.id.edit_text_register_email);
        editTestRegisterDoB=findViewById(R.id.edit_text_register_birthday);
        editTestRegisterMobile=findViewById(R.id.edit_text_register_phone);
        editTestRegisterPwd=findViewById(R.id.edit_text_register_password);
        editTestRegisterConfirmPwd=findViewById(R.id.edit_text_register_password_confirm);
        radioGroupRegisterGender=findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();

        editTestRegisterDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year =calendar.get(Calendar.YEAR);


                picker = new DatePickerDialog(RegisteringActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    editTestRegisterDoB.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                picker.show();
            }
        });

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

                String mobileRegex="[0-1][0-9]{9}";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textMobile);


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
                }else if(!mobileMatcher.find()){
                    Toast.makeText(RegisteringActivity.this,"Please enter a valid phone number",Toast.LENGTH_LONG).show();
                    editTestRegisterMobile.setError("Mobile number is not valid");
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

    private void swipeToRefresh() {
        swipeContainer= findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(() -> {
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
            swipeContainer.setRefreshing(false);
        });


        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,android.R.color.holo_green_light,android.R.color.holo_orange_light,android.R.color.holo_red_light);
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
                    UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDoB,textGender,textMobile);
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                firebaseUser.sendEmailVerification();
                                Toast.makeText(RegisteringActivity.this,"User succesfully registered.Please verify your email.",Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(RegisteringActivity.this, UserPageActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(RegisteringActivity.this,"User registration failed.",Toast.LENGTH_LONG).show();


                            }progressBar.setVisibility(View.GONE);

                        }
                    });


                }else{
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        editTestRegisterPwd.setError("Your password is too weak!");
                        editTestRegisterPwd.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        editTestRegisterEmail.setError("Your email is invalid or already in use.");
                        editTestRegisterEmail.requestFocus();
                    }catch(FirebaseAuthUserCollisionException e){
                        editTestRegisterEmail.setError("User is already registered with this email.");
                        editTestRegisterEmail.requestFocus();
                    }catch(Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegisteringActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();

                    }
                }progressBar.setVisibility(View.GONE);
            }
        });
    }
}
