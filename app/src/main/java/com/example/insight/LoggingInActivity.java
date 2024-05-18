package com.example.insight;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoggingInActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((R.layout.activity_logging_in));


        //set the title
        //getSupportActionBar().setTitle("FirebaseApp");

        /*//open long in activity
        Button buttonLogin=findViewById((R.id.button_login));
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginScreen.this,LoginActivity.class);
                startActivity(intent);
            }
        });*/

        //open register activity

        Button buttonRegister=findViewById((R.id.button_register));
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoggingInActivity.this,RegisteringActivity.class);
                startActivity(intent);
            }
        });


    }
}
