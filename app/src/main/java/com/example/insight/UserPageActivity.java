package com.example.insight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import com.squareup.picasso.Picasso;

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

        imageView=findViewById(R.id.imageView_profile_dp);
        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
                    public void onClick(View v){
                Intent intent = new Intent(UserPageActivity.this,UploadProfilePicActivity.class);
                startActivity(intent);
            }

        });

        authProfile= FirebaseAuth.getInstance();
        FirebaseUser  firebaseUser= authProfile.getCurrentUser();
        if(firebaseUser == null){
            Toast.makeText(UserPageActivity.this, "Something went wrong! User not available",Toast.LENGTH_LONG).show();
        }else{
            checkEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }


    }

    private void checkEmailVerified(FirebaseUser firebaseUser) {
        if(!firebaseUser.isEmailVerified()){
            System.out.println("not verified");
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserPageActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now.You can't login without email verification.");
        //open email if user agrees to continue
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        });
//create the alert dialog
        AlertDialog alertDialog = builder.create();

        //show the alert dialog
        alertDialog.show();
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
                    Uri uri=firebaseUser.getPhotoUrl();
                    Picasso.with(UserPageActivity.this).load(uri).into(imageView);
                } else {
                    Toast.makeText(UserPageActivity.this, "Something went wrong when fetching from the db!",Toast.LENGTH_LONG).show();
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


    //side menu
    @Override
    public boolean onCreateOptionsMenu( Menu menu){
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
    int id =item.getItemId();
    if(id==R.id.menu_refresh){
        startActivity(getIntent());
        finish();
        //overridePendingTransition(0,0);
    } else if( id==R.id.menu_updateProfile){
        Intent intent = new Intent(UserPageActivity.this, UpdateProfileActivity.class);
        startActivity(intent);
        finish();
    }else if( id==R.id.menu_updateEmail){
        Intent intent = new Intent(UserPageActivity.this, UpdateEmailActivity.class);
        startActivity(intent);
        finish();
    }else if( id==R.id.menu_settings){
        Toast.makeText(UserPageActivity.this, "menu settings",Toast.LENGTH_LONG).show();
    }else if( id==R.id.menu_changePwd){
        Intent intent = new Intent(UserPageActivity.this, ChangePassworgActivity.class);
        startActivity(intent);finish();
    }else if( id==R.id.menu_deleteAcc){
       /* Intent intent = new Intent(UserPageActivity.this, DeleteAccountActivity.class);
        startActivity(intent);*/
    }else if( id==R.id.menu_Logout){
        authProfile.signOut();
        Toast.makeText(UserPageActivity.this, "Logged out",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(UserPageActivity.this, LoggingInActivityMainScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }else{
        Toast.makeText(UserPageActivity.this, "Something went wrong",Toast.LENGTH_LONG).show();
    }
        return super.onOptionsItemSelected(item);
    }
}