package com.example.insight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class DeleteAccountActivity extends AppCompatActivity {


    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private EditText editTextUserPwd;
    private ProgressBar progressBar;
    private TextView textViewUserAuthenticated;
    private String userPwd;
    private Button buttonReAuthenticate, buttonDeleteUser;
    private static final String TAG = "DeleteAccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Delete Account");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_delete_account);
        progressBar=findViewById(R.id.progressBar);

        editTextUserPwd = findViewById(R.id.edit_text_delete_user_pwd);
        textViewUserAuthenticated = findViewById(R.id.text_view_delete_authenticated_user);
        buttonReAuthenticate=findViewById(R.id.button_delete_authenticated_user);

        buttonDeleteUser = findViewById(R.id.button_delete_user);
        buttonDeleteUser.setEnabled(false);


        authProfile=FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();

        if(firebaseUser.equals("")){
            Toast.makeText(DeleteAccountActivity.this,"Something went wrong",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(DeleteAccountActivity.this, UserPageActivity.class);
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
                userPwd=editTextUserPwd.getText().toString();

                if(TextUtils.isEmpty(userPwd)){
                    Toast.makeText(DeleteAccountActivity.this,"Password required",Toast.LENGTH_LONG).show();
                    editTextUserPwd.setError("Please enter your password");
                    editTextUserPwd.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);


                    AuthCredential credential= EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwd);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                editTextUserPwd.setEnabled(false);

                                buttonReAuthenticate.setEnabled(false);
                                buttonDeleteUser.setEnabled(true);

                                textViewUserAuthenticated.setText("You are now authenticated"+"You can delete your account now");
                                Toast.makeText(DeleteAccountActivity.this,"You can now delete your account",Toast.LENGTH_LONG).show();

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    buttonDeleteUser.setBackgroundTintList(ContextCompat.getColorStateList(DeleteAccountActivity.this,R.color.dark_green));
                                }

                                buttonDeleteUser.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showAlertDialog();
                                    }
                                });

                            }else{
                                try {
                                    throw task.getException();
                                }catch (Exception e){
                                    Toast.makeText(DeleteAccountActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();

                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }

            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteAccountActivity.this);
        builder.setTitle("Delete Account");
        builder.setMessage("Are you sure you want to delete your account?");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                deleteUserData(firebaseUser);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(DeleteAccountActivity.this, UserPageActivity.class);
                startActivity(intent);
                finish();

            }
        });
        AlertDialog alertDialog = builder.create();
    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
    @Override
    public void onShow(DialogInterface dialog) {
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
    }
});
        alertDialog.show();
    }



    private void deleteUserData(FirebaseUser firebaseUser) {

        if(firebaseUser.getPhotoUrl() != null) {
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReferenceFromUrl(firebaseUser.getPhotoUrl().toString());
            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d(TAG, "OnSuccess: Photos Deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, e.getMessage());
                    Toast.makeText(DeleteAccountActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG,"OnSuccess: User Data Deleted");

                deleteUser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,e.getMessage());
                Toast.makeText(DeleteAccountActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteUser() {

        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    authProfile.signOut();
                    Toast.makeText(DeleteAccountActivity.this,"User deleted",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(DeleteAccountActivity.this,LogInActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    try{
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(DeleteAccountActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
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
        /*if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(DeleteAccountActivity.this);

        }else*/ if(id==R.id.menu_myProfile){
            Intent intent = new Intent(DeleteAccountActivity.this, UserPageActivity.class);
            startActivity(intent);
            finish();
        } else if( id==R.id.menu_updateProfile){
            Intent intent = new Intent(DeleteAccountActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.menu_uploadImg){
            Intent intent = new Intent(DeleteAccountActivity.this, UploadToFirebaseActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.menu_liveStream){
            Intent intent = new Intent(DeleteAccountActivity.this, StreamView.class);
            startActivity(intent);
            finish();
            //overridePendingTransition(0,0);
        }else if( id==R.id.menu_updateEmail){
            Intent intent = new Intent(DeleteAccountActivity.this, UpdateEmailActivity.class);
            startActivity(intent);finish();
        }else if( id==R.id.menu_changePwd){
            Intent intent = new Intent(DeleteAccountActivity.this, ChangePasswordActivity.class);
            startActivity(intent);finish();
        }else if( id==R.id.menu_deleteAcc){
            Intent intent = new Intent(DeleteAccountActivity.this, DeleteAccountActivity.class);
            startActivity(intent);
            finish();

        }else if( id==R.id.menu_Logout){
            authProfile.signOut();
            Toast.makeText(DeleteAccountActivity.this, "Logged out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(DeleteAccountActivity.this, LoggingInActivityMainScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }else{
            Toast.makeText(DeleteAccountActivity.this, "Something went wrong",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}