package com.example.insight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public class ViewUploadedImagesUnknownPersons extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private FirebaseAuth authProfile;
    private RecyclerView recyclerView;
    private ProgressBar progressCircle;
    private ArrayList<Image> arrayList;
    private ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_uploaded_images);

        FirebaseApp.initializeApp(this);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Unknown Persons");
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressCircle = findViewById(R.id.progress_circle);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser != null) {
            FirebaseStorage.getInstance().getReference().child("UserUploads/"+firebaseUser.getUid()+"/RPI").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    arrayList = new ArrayList<>();
                    adapter = new ImageAdapter(ViewUploadedImagesUnknownPersons.this, arrayList);
                    adapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
                        @Override
                        public void onClick(Image image) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(image.getUrl()));
                            intent.setDataAndType(Uri.parse(image.getUrl()), "image/*");
                            startActivity(intent);
                        }
                    });
                    adapter.setOnItemLongClickListener(new ImageAdapter.OnItemLongClickListener() {
                        @Override
                        public void onLongClick(Image image) {
                            showDeleteConfirmationDialog(image);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        listResult.getItems().forEach(new Consumer<StorageReference>() {
                            @Override
                            public void accept(StorageReference storageReference) {
                                Image image = new Image();
                                image.setName(storageReference.getName());
                                storageReference.getDownloadUrl().addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        String url = task.getResult().toString();
                                        image.setUrl(url);
                                        arrayList.add(image);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(ViewUploadedImagesUnknownPersons.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                    progressCircle.setVisibility(View.INVISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressCircle.setVisibility(View.INVISIBLE);
                    Toast.makeText(ViewUploadedImagesUnknownPersons.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            progressCircle.setVisibility(View.INVISIBLE);
        }
    }

    private void showDeleteConfirmationDialog(Image image) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteImage(image);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteImage(Image image) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(image.getUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ViewUploadedImagesUnknownPersons.this, "Image deleted", Toast.LENGTH_SHORT).show();
                arrayList.remove(image);
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ViewUploadedImagesUnknownPersons.this, "Failed to delete image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
