package com.ucas.android.threads;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 3000;
    TextView timerTv;
    Handler handler;
    Runnable runnable;
    int timeLeft = 3;
    boolean stop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        timerTv = findViewById(R.id.timer_tv);
        //constantRedirct();
        redirectWithTime();
    }

    private void redirectWithTime() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (timeLeft > 0) {
                    timeLeft--;
                    timerTv.setText(timeLeft + "");
                } else {
                    stop = true;
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    handler.removeCallbacks(runnable);
                }
                if (!stop)
                    handler.postDelayed(runnable, 1000);
            }
        };
        runnable.run();
    }

    private void constantRedirct() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        }, SPLASH_DELAY);
    }
}