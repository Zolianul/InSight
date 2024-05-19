package com.example.insight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserPageActivity extends AppCompatActivity {

    private TextView textViewWelcome,textViewFullName,textViewEmail,textViewDoB,textViewGender,textViewMobile;
    private ProgressBar progressBar;
    private String fullName,email,dob,gender,mobile;
    private ImageView imageView;
    private FirebaseAuth authProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        textViewWelcome=findViewById(R.id.textView_show_welcome);
        textViewFullName=findViewById(R.id.textView_show_full_name);
        textViewEmail=findViewById(R.id.textView_show_email);
        textViewDoB=findViewById(R.id.textView_show_dob);
        textViewGender=findViewById(R.id.textView_show_gender);
        textViewMobile=findViewById(R.id.textView_show_mobile);
        progressBar=findViewById(R.id.progress_bar);

        authProfile= FirebaseAuth.getInstance();
        FirebaseUser  firebaseUser= authProfile.getCurrentUser();
        if(firebaseUser == null){
            Toast.makeText(UserPageActivity.this, "Something went wrong! User not available",Toast.LENGTH_LONG).show();
        }else{
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }


    }

    private void showUserProfile(FirebaseUser  firebaseUser) {
        String userID= firebaseUser.getUid();

        //extract data from the db

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails != null){
                    fullName=firebaseUser.getDisplayName();
                    email= firebaseUser.getEmail();
                    dob=readUserDetails.dateOfBirth;
                    gender=readUserDetails.gender;
                    mobile=readUserDetails.phone;

                    textViewWelcome.setText("Welcome"+fullName+"!");
                    textViewFullName.setText(fullName);
                    textViewEmail.setText(email);
                    textViewDoB.setText(dob);
                    textViewGender.setText(gender);
                    textViewMobile.setText(mobile);
                } else {
                    System.out.println("EROARE");
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserPageActivity.this, "Something went wrong when fetching from the db!",Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }
}