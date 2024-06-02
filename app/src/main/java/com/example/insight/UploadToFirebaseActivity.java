package com.example.insight;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UploadToFirebaseActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private ImageView imageViewUploadPic;
    private FirebaseAuth authProfile;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private EditText editText_img_name;
    private SwipeRefreshLayout swipeContainer;
    private TextView mTextViewShowUploads,mTextViewShowUnknowns;
    private static final int PICK_IMAGE_REQUEST =1;
    private Uri uriImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_to_firebase);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Upload to Firebase");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipeToRefresh();

        Button buttonUploadPicChoose = findViewById(R.id.button_choose_image);
        Button buttonUploadPic=findViewById(R.id.button_upload);
        progressBar=findViewById(R.id.progressBar);
        imageViewUploadPic=findViewById(R.id.image_view_img_to_upload);
        editText_img_name=findViewById(R.id.edit_text_file_name);
        mTextViewShowUploads = findViewById(R.id.text_view_show_uploads);
        mTextViewShowUnknowns = findViewById(R.id.text_view_show_unknowns);
        authProfile = FirebaseAuth.getInstance();
        firebaseUser =authProfile.getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference("UserUploads");
        //databaseReference= FirebaseDatabase.getInstance().getReference("home/raspberry_user/pi");
        //Uri uri= firebaseUser.getPhotoUrl();



        //Picasso.with(UploadToFirebaseActivity.this).load(uri).into(imageViewUploadPic);

        buttonUploadPicChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadToFirebaseActivity.this, ViewUploadedImagesActivity.class);
                startActivity(intent);
            }
        });
        mTextViewShowUnknowns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadToFirebaseActivity.this, ViewUploadedImagesUnknownPersons.class);
                startActivity(intent);
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
            StorageReference fileReference = storageReference.child(authProfile.getCurrentUser().getUid()+"/usr_upload."+editText_img_name.getText().toString()+System.currentTimeMillis()+getFileExtension(uriImage));
            //ulpoad to storage

            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri= uri;
                            firebaseUser = authProfile.getCurrentUser();


                        }
                    });
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UploadToFirebaseActivity.this,"Succesfully uploaded",Toast.LENGTH_LONG).show();
                    //Intent intent = new Intent(UploadToFirebaseActivity.this,UserPageActivity.class);
                    //startActivity(intent);
                    //finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadToFirebaseActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }else{
            Toast.makeText(UploadToFirebaseActivity.this,"no file selected",Toast.LENGTH_LONG).show();
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
            NavUtils.navigateUpFromSameTask(UploadToFirebaseActivity.this);

        }else*/ if(id==R.id.menu_myProfile){
            Intent intent = new Intent(UploadToFirebaseActivity.this, UserPageActivity.class);
            startActivity(intent);
            finish();
            //overridePendingTransition(0,0);
        } else if( id==R.id.menu_updateProfile){
            Intent intent = new Intent(UploadToFirebaseActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.menu_liveStream){
            Intent intent = new Intent(UploadToFirebaseActivity.this, StreamView.class);
            startActivity(intent);
            finish();
            //overridePendingTransition(0,0);
        }else if(id==R.id.menu_uploadImg){
            Intent intent = new Intent(UploadToFirebaseActivity.this, UploadToFirebaseActivity.class);
            startActivity(intent);
            finish();
        }else if( id==R.id.menu_updateEmail){
            Intent intent = new Intent(UploadToFirebaseActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if( id==R.id.menu_changePwd){
            Intent intent = new Intent(UploadToFirebaseActivity.this, ChangePassworgActivity.class);
            startActivity(intent);finish();
        }else if( id==R.id.menu_deleteAcc){
            Intent intent = new Intent(UploadToFirebaseActivity.this, DeleteAccountActivity.class);
            startActivity(intent);finish();
        }else if( id==R.id.menu_Logout){
            authProfile.signOut();
            Toast.makeText(UploadToFirebaseActivity.this, "Logged out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UploadToFirebaseActivity.this, LoggingInActivityMainScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }else{
            Toast.makeText(UploadToFirebaseActivity.this, "Something went wrong",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}