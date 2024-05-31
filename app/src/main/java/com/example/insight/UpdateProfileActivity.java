package com.example.insight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText editTextUpdateName, editTextUpdatedob, editTextUpdateMobile;
    private RadioGroup radioGroupUpdateGender;
    private RadioButton radioButtonUpdateGenderSelected;
    private String textFullName, textDob,textGender, textMobile;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Update your profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipeToRefresh();

        progressBar = findViewById(R.id.progressBar);
        editTextUpdateName=findViewById(R.id.editText_update_profile_name);
        editTextUpdateMobile=findViewById(R.id.editText_update_profile_mobile);
        editTextUpdatedob =findViewById(R.id.editText_update_profile_dob);

        radioGroupUpdateGender =findViewById(R.id.radio_group_update_profile_gender);

        authProfile= FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        showProfile(firebaseUser);


        //upload profile picture
        TextView textViewUploadProfilePic= findViewById(R.id.textView_profile_upload_pic);
        textViewUploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( UpdateProfileActivity.this, UploadProfilePicActivity.class);
                startActivity(intent);
                finish();
            }
        });

        TextView textViewupdateEmail= findViewById(R.id.textView_profile_update_email);
        textViewupdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( UpdateProfileActivity.this, UpdateEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });



        editTextUpdatedob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textSavedDoB[] = textDob.split("/");

                int day = Integer.parseInt(textSavedDoB[0]);
                int month = Integer.parseInt(textSavedDoB[1])-1;
                int year = Integer.parseInt(textSavedDoB[2]);

                DatePickerDialog picker;

                picker = new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextUpdatedob.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                picker.show();
            }
        });




        Button buttonUpdateProfile = findViewById(R.id.button_update_profile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });


    }

    private void updateProfile(FirebaseUser firebaseUser) {
        int selectedGenderID= radioGroupUpdateGender.getCheckedRadioButtonId();
        radioButtonUpdateGenderSelected = findViewById(selectedGenderID);

        //valid phone no
        String mobileRegex="[0-1][0-9]{9}";
        Matcher mobileMatcher;
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(textMobile);


        if (TextUtils.isEmpty(textFullName)) {
            Toast.makeText(UpdateProfileActivity.this, "Please Enter Your name", Toast.LENGTH_LONG).show();
            editTextUpdateName.setError("Full name is required");
            editTextUpdateName.requestFocus();
        } else if (TextUtils.isEmpty(textDob)) {
            Toast.makeText(UpdateProfileActivity.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
            editTextUpdatedob.setError("Your date of birth is required");
            editTextUpdatedob.requestFocus();
        } else if (TextUtils.isEmpty(radioButtonUpdateGenderSelected.getText())) {
            Toast.makeText(UpdateProfileActivity.this, "Please enter your gender", Toast.LENGTH_LONG).show();
            radioButtonUpdateGenderSelected.setError("Your gender is required");
            radioButtonUpdateGenderSelected.requestFocus();
        } else if (TextUtils.isEmpty(textMobile)) {
            Toast.makeText(UpdateProfileActivity.this, "Please Enter Your Phone", Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile is required");
            editTextUpdateMobile.requestFocus();
        } else if( textMobile.length()!=10){
            Toast.makeText(UpdateProfileActivity.this,"Please enter a valid phone number",Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Valid Mobile is required(10 digits)");
            editTextUpdateMobile.requestFocus();
        }else if(!mobileMatcher.find()){
            Toast.makeText(UpdateProfileActivity.this,"Please enter a valid phone number",Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile number is not valid");
            editTextUpdateMobile.requestFocus();

        }else{

            textGender=radioButtonUpdateGenderSelected.getText().toString();

            textFullName = editTextUpdateName.getText().toString();
            textDob = editTextUpdatedob.getText().toString();
            textMobile=editTextUpdateMobile.getText().toString();

            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDob,textGender,textMobile);

            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
            String userID = firebaseUser.getUid();

            progressBar.setVisibility(View.VISIBLE);
            referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        UserProfileChangeRequest profileUpdates= new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                        firebaseUser.updateProfile(profileUpdates);
                        Toast.makeText(UpdateProfileActivity.this,"Succesfully updated",Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(UpdateProfileActivity.this, UserPageActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else {
                        try{
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(UpdateProfileActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }

                    }
                    progressBar.setVisibility(View.GONE);
                }
            });

        }
    }

    private void showProfile(FirebaseUser firebaseUser) {
        String userIdofRegistered = firebaseUser.getUid();

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        progressBar.setVisibility(View.VISIBLE);

        referenceProfile.child(userIdofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readWriteUserDetails= snapshot.getValue(ReadWriteUserDetails.class);
                if(readWriteUserDetails != null){
                    textFullName= firebaseUser.getDisplayName();
                    textDob= readWriteUserDetails.dateOfBirth;
                    textGender = readWriteUserDetails.gender;
                    textMobile = readWriteUserDetails.phone;

                    editTextUpdateName.setText(textFullName);
                    editTextUpdatedob.setText(textDob);
                    editTextUpdateMobile.setText(textMobile);

                    if(textGender.equals("Male")){
                        radioButtonUpdateGenderSelected = findViewById(R.id.radio_male);
                    }else{
                        radioButtonUpdateGenderSelected =findViewById(R.id.radio_female);
                    }
                    radioButtonUpdateGenderSelected.setChecked(true);
                }else{
                    Toast.makeText(UpdateProfileActivity.this, "Something wento wrong", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, "Something wento wrong", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
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
    @Override
    public boolean onCreateOptionsMenu( Menu menu){
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id =item.getItemId();
        if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(UpdateProfileActivity.this);

        }else if(id==R.id.menu_myProfile){
            Intent intent = new Intent(UpdateProfileActivity.this, UserPageActivity.class);
            startActivity(intent);
            finish();
            //overridePendingTransition(0,0);
        } else if( id==R.id.menu_updateProfile){
            Intent intent = new Intent(UpdateProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }else if( id==R.id.menu_updateEmail){
        Intent intent = new Intent(UpdateProfileActivity.this, UpdateEmailActivity.class);
        startActivity(intent);finish();
        }else if( id==R.id.menu_changePwd){
        Intent intent = new Intent(UpdateProfileActivity.this, ChangePassworgActivity.class);
        startActivity(intent);
        }else if( id==R.id.menu_deleteAcc){
       Intent intent = new Intent(UpdateProfileActivity.this, DeleteAccountActivity.class);
        startActivity(intent);
        finish();

        }else if( id==R.id.menu_Logout){
            authProfile.signOut();
            Toast.makeText(UpdateProfileActivity.this, "Logged out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UpdateProfileActivity.this, LoggingInActivityMainScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }else{
            Toast.makeText(UpdateProfileActivity.this, "Something went wrong",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}