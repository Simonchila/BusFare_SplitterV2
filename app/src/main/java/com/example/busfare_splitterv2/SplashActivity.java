package com.example.busfare_splitterv2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Button btnGetStarted = findViewById(R.id.btnGetStarted);
        LottieAnimationView lottieView = findViewById(R.id.lottieView);

        // Loop forever
        lottieView.setRepeatCount(LottieDrawable.INFINITE);
        lottieView.playAnimation();

        btnGetStarted.setOnClickListener(v -> {
            lottieView.cancelAnimation();
            startActivity(new Intent(SplashActivity.this, TripListActivity.class));
            finish();
        });
    }
}
