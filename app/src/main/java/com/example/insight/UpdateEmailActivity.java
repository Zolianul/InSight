package com.example.insight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateEmailActivity extends AppCompatActivity {


    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private TextView textViewAuthenticated;
    private String userOldEmail, userNewEmail,userPassword;
    private Button buttonUpdateEmail;
    private EditText editTextNewEmail, editTextPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);


        progressBar=findViewById(R.id.progressBar);
        editTextPwd=findViewById(R.id.editText_update_email_verify_password);
        editTextNewEmail= findViewById(R.id.editText_update_email_new);
        textViewAuthenticated=findViewById(R.id.textView_update_email_authenticated);
        buttonUpdateEmail=findViewById(R.id.button_update_email);

        buttonUpdateEmail.setEnabled(false);
        editTextNewEmail.setEnabled(false);

        authProfile=FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();

        userOldEmail=firebaseUser.getEmail();
        TextView textviewOldEmail = findViewById(R.id.textView_update_email_old);
        textviewOldEmail.setText(userOldEmail);

        if (firebaseUser.equals("")){
            Toast.makeText(UpdateEmailActivity.this,"Something went wrong",Toast.LENGTH_LONG).show();
        }else {
            reAuthenticate(firebaseUser);
        }

    }

    private void reAuthenticate(FirebaseUser firebaseUser) {
        Button buttonVerifyUser = findViewById(R.id.button_authenticate_user);
        buttonVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPassword= editTextPwd.getText().toString();
                if(TextUtils.isEmpty(userPassword)){
                    Toast.makeText(UpdateEmailActivity.this,"Password is needed to continue",Toast.LENGTH_LONG).show();
                    editTextPwd.setError("Please enter your password");
                    editTextPwd.requestFocus();
                }else {
                    progressBar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail,userPassword);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(UpdateEmailActivity.this,"You are now logged in. You can update your password",Toast.LENGTH_LONG).show();


                                textViewAuthenticated.setText("You are now logged in. Please change your password now.");
                                editTextNewEmail.setEnabled(true);
                                editTextPwd.setEnabled(false);
                                buttonVerifyUser.setEnabled(false);
                                buttonUpdateEmail.setEnabled(true);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    buttonUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(UpdateEmailActivity.this,R.color.dark_green));
                                }


                                buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        userNewEmail=editTextNewEmail.getText().toString();
                                        if(TextUtils.isEmpty(userNewEmail)){
                                            Toast.makeText(UpdateEmailActivity.this,"Please enter a new email",Toast.LENGTH_LONG).show();
                                            editTextNewEmail.setError("Please enter your email");
                                            editTextNewEmail.requestFocus();
                                        }else if(!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()){
                                            Toast.makeText(UpdateEmailActivity.this,"Please enter a valid email",Toast.LENGTH_LONG).show();
                                            editTextNewEmail.setError("Please enter your email");
                                            editTextNewEmail.requestFocus();
                                        }else if(userOldEmail.matches(userNewEmail)){
                                            Toast.makeText(UpdateEmailActivity.this,"Please enter a new email, different from the old one!",Toast.LENGTH_LONG).show();
                                            editTextNewEmail.setError("Please enter a different email");
                                            editTextNewEmail.requestFocus();
                                        }else{
                                            progressBar.setVisibility(View.VISIBLE);
                                            updateEmail(firebaseUser);
                                        }
                                    }
                                });
                            }else{
                                try{
                                    throw task.getException();
                                }catch (Exception e){
                                    Toast.makeText(UpdateEmailActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                    String message= e.getMessage();
                                    textViewAuthenticated.setText(message);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void updateEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete()){
                    // verify new email
                    firebaseUser.sendEmailVerification();

                    Toast.makeText(UpdateEmailActivity.this,"Email has been updated.Please verify your email now.",Toast.LENGTH_LONG).show();
                     Intent intent = new Intent(UpdateEmailActivity.this, UserPageActivity.class);
               startActivity(intent);
               finish();

                }else{
                    try{
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(UpdateEmailActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        String message= e.getMessage();
                        textViewAuthenticated.setText(message);
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }


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
            Intent intent = new Intent(UpdateEmailActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }else if( id==R.id.menu_updateEmail){
            Intent intent = new Intent(UpdateEmailActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if( id==R.id.menu_settings){
            Toast.makeText(UpdateEmailActivity.this, "menu settings",Toast.LENGTH_LONG).show();
        }else if( id==R.id.menu_changePwd){
        /*Intent intent = new Intent(UpdateEmailActivity.this, ChangePassworgActivity.class);
        startActivity(intent);*/
        }else if( id==R.id.menu_deleteAcc){
       /* Intent intent = new Intent(UpdateEmailActivity.this, DeleteAccountActivity.class);
        startActivity(intent);*/
        }else if( id==R.id.menu_Logout){
            authProfile.signOut();
            Toast.makeText(UpdateEmailActivity.this, "Logged out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UpdateEmailActivity.this, LoggingInActivityMainScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }else{
            Toast.makeText(UpdateEmailActivity.this, "Something went wrong",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}