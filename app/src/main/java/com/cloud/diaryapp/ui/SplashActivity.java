package com.cloud.diaryapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.cloud.diaryapp.R;

public class SplashActivity extends AppCompatActivity {
    private final Handler h = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        h.postDelayed(() -> {
            Intent intent;
            SharedPreferences sharedPreferences = SplashActivity.this.getSharedPreferences("isLogout", MODE_PRIVATE);
            if (sharedPreferences.getBoolean("isLogout", true)) {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        }, 2000);
    }
}