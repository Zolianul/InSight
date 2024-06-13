package com.example.insight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.Objects;

public class ChangePasswordActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;

    private ProgressBar progressBar;
    private TextView textViewAuthenticatedMessage,textviewEmail;
    private String userPwdCurr,userEmail;
    private Button buttonChangePwd, buttonAuthenticateChangePwd;
    private EditText editTextChangeCurrentPwd, editTextChangePwdNew, editTextConfirmNewPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Change Password");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setContentView(R.layout.activity_change_passworg);

        progressBar=findViewById(R.id.progressBar);
        editTextChangeCurrentPwd =findViewById(R.id.edit_text_change_current_pwd);
        editTextChangePwdNew = findViewById(R.id.edit_text_change_pwd_new);
        editTextConfirmNewPwd =findViewById(R.id.edit_text_confirm_new_pwd);
        textViewAuthenticatedMessage =findViewById(R.id.text_view_authenticated_message);

        buttonChangePwd=findViewById(R.id.button_change_pwd);
        buttonAuthenticateChangePwd =findViewById(R.id.button_authenticate_change_pwd);

        editTextChangePwdNew.setEnabled(false);
        editTextConfirmNewPwd.setEnabled(false);
        buttonChangePwd.setEnabled(false);
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();

        userEmail=firebaseUser.getEmail();
        TextView textviewEmail = findViewById(R.id.text_view_usremail);
        textviewEmail.setText(userEmail);

        if(firebaseUser.equals("")){
            Toast.makeText(ChangePasswordActivity.this,"Something went wrong",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ChangePasswordActivity.this, UserPageActivity.class);
            startActivity(intent);
            finish();
        }else {
            AuthenticateUser(firebaseUser);
        }



    }

    private void AuthenticateUser(FirebaseUser firebaseUser) {
        buttonAuthenticateChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwdCurr= editTextChangeCurrentPwd.getText().toString();

                if(TextUtils.isEmpty(userPwdCurr)){
                    Toast.makeText(ChangePasswordActivity.this,"Password is required",Toast.LENGTH_LONG).show();
                    editTextChangeCurrentPwd.setError("Please enter your password");
                    editTextChangeCurrentPwd.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);


                    AuthCredential credential= EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwdCurr);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                editTextChangeCurrentPwd.setEnabled(false);
                                editTextChangePwdNew.setEnabled(true);
                                editTextConfirmNewPwd.setEnabled(true);

                                buttonAuthenticateChangePwd.setEnabled(false);
                                buttonChangePwd.setEnabled(true);

                                textViewAuthenticatedMessage.setText("You are now authenticated"+"You can change your password now");
                                Toast.makeText(ChangePasswordActivity.this,"You can now change your password",Toast.LENGTH_LONG).show();

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    buttonChangePwd.setBackgroundTintList(ContextCompat.getColorStateList(ChangePasswordActivity.this,R.color.dark_green));
                                }

                                buttonChangePwd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changePwd(firebaseUser);
                                    }
                                });

                            }else{
                                try {
                                    throw task.getException();
                                }catch (Exception e){
                                    Toast.makeText(ChangePasswordActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();

                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }

            }
        });
    }

    private void changePwd(FirebaseUser firebaseUser) {

        String userPwdNew = editTextChangePwdNew.getText().toString();
        String userPwdConfirmNew = editTextConfirmNewPwd.getText().toString();
        if(TextUtils.isEmpty(userPwdNew)){
            Toast.makeText(ChangePasswordActivity.this,"Password required",Toast.LENGTH_LONG).show();
            editTextChangePwdNew.setError("Please enter your new password");
            editTextChangePwdNew.requestFocus();
        }else if(TextUtils.isEmpty(userPwdConfirmNew)){
            Toast.makeText(ChangePasswordActivity.this,"Password required",Toast.LENGTH_LONG).show();
            editTextConfirmNewPwd.setError("Please enter your new password");
            editTextConfirmNewPwd.requestFocus();
        } else if (!userPwdNew.matches(userPwdConfirmNew)) {
            Toast.makeText(ChangePasswordActivity.this,"Same password required",Toast.LENGTH_LONG).show();
            editTextConfirmNewPwd.setError("Please enter same new password");
            editTextConfirmNewPwd.requestFocus();

        }else if (userPwdNew.matches(userPwdCurr)) {
            Toast.makeText(ChangePasswordActivity.this,"A new password required",Toast.LENGTH_LONG).show();
            editTextChangePwdNew.setError("Please enter a password, different from the current one.");
            editTextChangePwdNew.requestFocus();

        }else{
            progressBar.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ChangePasswordActivity.this,"Password changed",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ChangePasswordActivity.this, UserPageActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(ChangePasswordActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();

                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }

    }


    @Override
    public boolean onCreateOptionsMenu( Menu menu){
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id =item.getItemId();
        /*if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(ChangePassworgActivity.this);

        }else */if(id==R.id.menu_myProfile){
            Intent intent = new Intent(ChangePasswordActivity.this, UserPageActivity.class);
            startActivity(intent);
            finish();
            //overridePendingTransition(0,0);
        }else if(id==R.id.menu_liveStream){
            Intent intent = new Intent(ChangePasswordActivity.this, StreamView.class);
            startActivity(intent);
            finish();
            //overridePendingTransition(0,0);
        } else if(id==R.id.menu_uploadImg){
            Intent intent = new Intent(ChangePasswordActivity.this, UploadToFirebaseActivity.class);
            startActivity(intent);
            finish();
        }else if( id==R.id.menu_updateProfile){
            Intent intent = new Intent(ChangePasswordActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }else if( id==R.id.menu_updateEmail){
            Intent intent = new Intent(ChangePasswordActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if( id==R.id.menu_changePwd){
            Intent intent = new Intent(ChangePasswordActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        }else if( id==R.id.menu_deleteAcc){
       Intent intent = new Intent(ChangePasswordActivity.this, DeleteAccountActivity.class);
        startActivity(intent);finish();
        }else if( id==R.id.menu_Logout){
            authProfile.signOut();
            Toast.makeText(ChangePasswordActivity.this, "Logged out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ChangePasswordActivity.this, LoggingInActivityMainScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }else{
            Toast.makeText(ChangePasswordActivity.this, "Something went wrong",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}