package com.api.slotstracker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.api.slotstracker.R;

public class KnowYourApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_know_your_app);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}