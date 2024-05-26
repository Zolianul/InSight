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
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.core.view.Change;

public class ChangePassworgActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;

    private ProgressBar progressBar;
    private TextView textViewAuthenticated;
    private String userPwdCurr;
    private Button buttonChangePwd,buttonReAuthenticate;
    private EditText editTextPwdCurr, editTextPwdNew,editTextPwdConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_passworg);

        progressBar=findViewById(R.id.progressBar);
        editTextPwdCurr=findViewById(R.id.editText_change_pwd_current);
        editTextPwdNew= findViewById(R.id.editText_change_pwd_new);
        editTextPwdConfirm=findViewById(R.id.editText_change_pwd_new_confirm);
        textViewAuthenticated=findViewById(R.id.textView_change_pwd_authenticated);

        buttonChangePwd=findViewById(R.id.button_change_pwd);
        buttonReAuthenticate=findViewById(R.id.button_change_pwd_authenticate);

        editTextPwdNew.setEnabled(false);
        editTextPwdConfirm.setEnabled(false);
        buttonChangePwd.setEnabled(false);
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();

        if(firebaseUser.equals("")){
            Toast.makeText(ChangePassworgActivity.this,"Something went wrong",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ChangePassworgActivity.this, UserPageActivity.class);
            startActivity(intent);
            finish();
        }else {
            reAuthenticate(firebaseUser);
        }



    }

    private void reAuthenticate(FirebaseUser firebaseUser) {


        buttonReAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwdCurr=editTextPwdCurr.getText().toString();

                if(TextUtils.isEmpty(userPwdCurr)){
                    Toast.makeText(ChangePassworgActivity.this,"Password required",Toast.LENGTH_LONG).show();
                    editTextPwdCurr.setError("Please enter your password");
                    editTextPwdCurr.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);


                    AuthCredential credential= EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwdCurr);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                editTextPwdCurr.setEnabled(false);
                                editTextPwdNew.setEnabled(true);
                                editTextPwdConfirm.setEnabled(true);

                                buttonReAuthenticate.setEnabled(false);
                                buttonChangePwd.setEnabled(true);

                                textViewAuthenticated.setText("You are now authenticated"+"You can change your password now");
                                Toast.makeText(ChangePassworgActivity.this,"You can now change your password",Toast.LENGTH_LONG).show();

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    buttonChangePwd.setBackgroundTintList(ContextCompat.getColorStateList(ChangePassworgActivity.this,R.color.dark_green));
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
                                    Toast.makeText(ChangePassworgActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();

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

        String userPwdNew = editTextPwdNew.getText().toString();
        String userPwdConfirmNew = editTextPwdConfirm.getText().toString();

        if(TextUtils.isEmpty(userPwdNew)){
            Toast.makeText(ChangePassworgActivity.this,"Password required",Toast.LENGTH_LONG).show();
            editTextPwdNew.setError("Please enter your new password");
            editTextPwdNew.requestFocus();
        }else if(TextUtils.isEmpty(userPwdConfirmNew)){
            Toast.makeText(ChangePassworgActivity.this,"Password required",Toast.LENGTH_LONG).show();
            editTextPwdConfirm.setError("Please enter your new password");
            editTextPwdConfirm.requestFocus();
        } else if (!userPwdNew.matches(userPwdConfirmNew)) {
            Toast.makeText(ChangePassworgActivity.this,"Password required",Toast.LENGTH_LONG).show();
            editTextPwdConfirm.setError("Please enter same new password");
            editTextPwdConfirm.requestFocus();

        }else if (userPwdNew.matches(userPwdCurr)) {
            Toast.makeText(ChangePassworgActivity.this,"Password required",Toast.LENGTH_LONG).show();
            editTextPwdNew.setError("Please enter a different password");
            editTextPwdNew.requestFocus();

        }else{
            progressBar.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ChangePassworgActivity.this,"Password changed",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ChangePassworgActivity.this, UserPageActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(ChangePassworgActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();

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
        if(id==R.id.menu_refresh){
            startActivity(getIntent());
            finish();
            //overridePendingTransition(0,0);
        } else if( id==R.id.menu_updateProfile){
            Intent intent = new Intent(ChangePassworgActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }else if( id==R.id.menu_updateEmail){
            Intent intent = new Intent(ChangePassworgActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if( id==R.id.menu_settings){
            Toast.makeText(ChangePassworgActivity.this, "menu settings",Toast.LENGTH_LONG).show();
        }else if( id==R.id.menu_changePwd){
            Intent intent = new Intent(ChangePassworgActivity.this, ChangePassworgActivity.class);
            startActivity(intent);
            finish();
        }else if( id==R.id.menu_deleteAcc){
       /* Intent intent = new Intent(UpdateEmailActivity.this, DeleteAccountActivity.class);
        startActivity(intent);*/
        }else if( id==R.id.menu_Logout){
            authProfile.signOut();
            Toast.makeText(ChangePassworgActivity.this, "Logged out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ChangePassworgActivity.this, LoggingInActivityMainScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }else{
            Toast.makeText(ChangePassworgActivity.this, "Something went wrong",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}