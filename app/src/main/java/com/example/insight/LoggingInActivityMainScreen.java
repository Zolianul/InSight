package com.example.insight;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class LoggingInActivityMainScreen extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Logging In");
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView((R.layout.activity_logging_in));


        //set the title
        //getSupportActionBar().setTitle("FirebaseApp");

        //open long in activity
        Button buttonLogin=findViewById((R.id.button_login));
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoggingInActivityMainScreen.this,LogInActivity.class);
                startActivity(intent);finish();
            }
        });

        //open register activity

        TextView textViewRegister=findViewById((R.id.text_view_register_link));
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoggingInActivityMainScreen.this,RegisteringActivity.class);
                startActivity(intent);finish();
            }
        });


    }
}
