package com.api.slotstracker.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.api.slotstracker.R;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getActionBar().hide();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        ImageView imageView = findViewById(R.id.imageView);
        Glide.with(this).load(R.drawable.splash_gif).into(imageView);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                finish();
            }
        }, 3000);
    }
    
}