package com.example.insight;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivity3 extends AppCompatActivity implements ImageAdapter.OnItemClickListener {
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListner;
    private List<UploadToFirebase> mUploadToFirebases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        mRecyclerView =findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressCircle=findViewById(R.id.progress_circle);

        mUploadToFirebases = new ArrayList<>();

        mAdapter = new ImageAdapter(MainActivity3.this, mUploadToFirebases);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClicListener(MainActivity3.this);

        mStorage=FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("home/raspberry_user/pi");

        mDBListner=mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUploadToFirebases.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UploadToFirebase uploadToFirebase = postSnapshot.getValue(UploadToFirebase.class);
                    uploadToFirebase.setKey(postSnapshot.getKey());
                    mUploadToFirebases.add(uploadToFirebase);
                }

                mAdapter.notifyDataSetChanged();

                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity3.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });


    }

    @Override
    public void onItemClick(int position) {
        // Toast.makeText(this,""+position,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWhatEverClick(int position) {
        //Toast.makeText(this,""+position,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {
        UploadToFirebase selectedItem = mUploadToFirebases.get(position);
        String selectedKey= selectedItem.getKey();

        StorageReference imageRef= mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(MainActivity3.this,"Item Deleted",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void OnDestroy(){
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListner);
    }
}