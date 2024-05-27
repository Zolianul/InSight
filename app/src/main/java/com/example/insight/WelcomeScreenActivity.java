package com.example.insight;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeScreenActivity extends AppCompatActivity {
    private Button button,button_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        button = (Button) findViewById(R.id.button);
        button_login= (Button) findViewById((R.id.button_test_login));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity2();
            }
        });
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginTest();
            }
        });

    }

    public void openActivity2() {
        Intent intent = new Intent(this, UploadToFirebaseActivity.class);
        startActivity(intent);
    }
    public void openLoginTest() {
        Intent intent = new Intent(this, LoggingInActivityMainScreen.class);
        startActivity(intent);
    }
}