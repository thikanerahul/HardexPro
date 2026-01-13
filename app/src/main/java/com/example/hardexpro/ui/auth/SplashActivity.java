package com.example.hardexpro.ui.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hardexpro.MainActivity;
import com.example.hardexpro.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Entrance Animations
        findViewById(R.id.cardLogo).setAlpha(0f);
        findViewById(R.id.tvAppName).setAlpha(0f);
        findViewById(R.id.tvTagline).setAlpha(0f);

        findViewById(R.id.cardLogo).animate().alpha(1f).setDuration(1000).start();
        findViewById(R.id.tvAppName).animate().alpha(1f).setDuration(1000).setStartDelay(500).start();
        findViewById(R.id.tvTagline).animate().alpha(0.8f).setDuration(1000).setStartDelay(800).start();

        // Pulsing Animation for Logo
        android.view.View logo = findViewById(R.id.ivLogo);
        android.view.animation.Animation pulse = new android.view.animation.AlphaAnimation(0.6f, 1.0f);
        pulse.setDuration(800);
        pulse.setRepeatMode(android.view.animation.Animation.REVERSE);
        pulse.setRepeatCount(android.view.animation.Animation.INFINITE);
        logo.startAnimation(pulse);

        new Handler().postDelayed(() -> {
            com.google.firebase.auth.FirebaseAuth mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() != null) {
                startActivity(new Intent(SplashActivity.this, com.example.hardexpro.MainActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, 3500);
    }
}
