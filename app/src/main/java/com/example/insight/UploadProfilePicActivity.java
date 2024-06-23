package com.example.insight;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UploadProfilePicActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private ImageView imageViewUploadPic;
    private FirebaseAuth authProfile;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private SwipeRefreshLayout swipeContainer;
    private static final int PICK_IMAGE_REQUEST =1;
    private Uri uriImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_pic);
        progressBar=findViewById(R.id.progressBar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("UploadProfilePic");
        swipeToRefresh();
//
        Button buttonChoosePic = findViewById(R.id.button_choose_picture);
        Button buttonUploadPic=findViewById(R.id.upload_pic_button);
        imageViewUploadPic=findViewById(R.id.image_view_profile_pic);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser =authProfile.getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference("DisplayPics");

        Uri uri= firebaseUser.getPhotoUrl();


        Picasso.with(UploadProfilePicActivity.this).load(uri).into(imageViewUploadPic);

        buttonChoosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        buttonUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                UploadPic();
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

    private void UploadPic() {
        if(uriImage!=null){
            StorageReference fileReference = storageReference.child(authProfile.getCurrentUser().getUid()+"/displaypic."+getFileExtension(uriImage));

            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri= uri;
                            firebaseUser = authProfile.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build();
                            firebaseUser.updateProfile(profileUpdates);
                        }
                    });
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UploadProfilePicActivity.this,"Successfully uploaded",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(UploadProfilePicActivity.this,UserPageActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadProfilePicActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }else{
            Toast.makeText(UploadProfilePicActivity.this,"no file selected",Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private String getFileExtension(Uri uriImage) {
        ContentResolver cr= getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uriImage));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data!=null && data.getData()!=null){
            uriImage= data.getData();
            imageViewUploadPic.setImageURI(uriImage);
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
            NavUtils.navigateUpFromSameTask(UploadProfilePicActivity.this);

        }else*/ if(id==R.id.menu_myProfile){
            Intent intent = new Intent(UploadProfilePicActivity.this, UserPageActivity.class);
            startActivity(intent);
            finish();
            //overridePendingTransition(0,0);
        } else if( id==R.id.menu_updateProfile){
        Intent intent = new Intent(UploadProfilePicActivity.this, UpdateProfileActivity.class);
        startActivity(intent);
        finish();
        }else if(id==R.id.menu_uploadImg){
            Intent intent = new Intent(UploadProfilePicActivity.this, UploadToFirebaseActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.menu_liveStream){
            Intent intent = new Intent(UploadProfilePicActivity.this, StreamView.class);
            startActivity(intent);
            finish();
            //overridePendingTransition(0,0);
        }else if( id==R.id.menu_updateEmail){
        Intent intent = new Intent(UploadProfilePicActivity.this, UpdateEmailActivity.class);
        startActivity(intent);
        finish();
        }else if( id==R.id.menu_changePwd){
        Intent intent = new Intent(UploadProfilePicActivity.this, ChangePasswordActivity.class);
        startActivity(intent);finish();
        }else if( id==R.id.menu_deleteAcc){
       Intent intent = new Intent(UploadProfilePicActivity.this, DeleteAccountActivity.class);
        startActivity(intent);finish();
        }else if( id==R.id.menu_Logout){
            authProfile.signOut();
            Toast.makeText(UploadProfilePicActivity.this, "Logged out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UploadProfilePicActivity.this, LoggingInActivityMainScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }else{
            Toast.makeText(UploadProfilePicActivity.this, "Something went wrong",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}