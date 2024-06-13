package com.example.insight;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Objects;
//done
public class LoggingInActivityMainScreen extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Logging In");
        setContentView((R.layout.activity_logging_in));

        //open login activity
        Button buttonLogin=findViewById((R.id.button_login));
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoggingInActivityMainScreen.this,LogInActivity.class);
                startActivity(intent);finish();
            }
        });
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
