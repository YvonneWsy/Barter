package com.example.yvonnewu.frontend;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {
    //splash screen time out is set to 4s
    private static int SPLASH_TIMEOUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        //handler to jump to the login page
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent loginIntent = new Intent(SplashScreenActivity.this, LogInPageActivity.class);
                startActivity(loginIntent);
                finish();
            }
        }, SPLASH_TIMEOUT);
    }
}
