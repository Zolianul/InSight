package com.example.insight;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class StreamView extends AppCompatActivity {
    private WebView mWebView;
    private Button captureButton;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_view);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Live Stream");
        authProfile= FirebaseAuth.getInstance();
        FirebaseUser  firebaseUser= authProfile.getCurrentUser();
        mWebView= (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        //mWebView.loadUrl("http://192.168.90.205:5000");
        mWebView.loadUrl("http://192.168.252.64:5000");

    }
    public boolean onCreateOptionsMenu( Menu menu){
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id =item.getItemId();
        /*if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(StreamView.this);

        }else*/ if(id==R.id.menu_myProfile){
            Intent intent = new Intent(StreamView.this, UserPageActivity.class);
            startActivity(intent);
            finish();
            //overridePendingTransition(0,0);
        }else if(id==R.id.menu_liveStream){
            Intent intent = new Intent(StreamView.this, StreamView.class);
            startActivity(intent);
            finish();
            //overridePendingTransition(0,0);
        } else if(id==R.id.menu_uploadImg){
            Intent intent = new Intent(StreamView.this, UploadToFirebaseActivity.class);
            startActivity(intent);
            finish();
        }else if( id==R.id.menu_updateProfile){
            Intent intent = new Intent(StreamView.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }else if( id==R.id.menu_updateEmail){
            Intent intent = new Intent(StreamView.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if( id==R.id.menu_changePwd){
            Intent intent = new Intent(StreamView.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        }else if( id==R.id.menu_deleteAcc){
            Intent intent = new Intent(StreamView.this, DeleteAccountActivity.class);
            startActivity(intent);finish();
        }else if( id==R.id.menu_Logout){
            authProfile.signOut();
            Toast.makeText(StreamView.this, "Logged out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(StreamView.this, LoggingInActivityMainScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }else{
            Toast.makeText(StreamView.this, "Something went wrong",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

}