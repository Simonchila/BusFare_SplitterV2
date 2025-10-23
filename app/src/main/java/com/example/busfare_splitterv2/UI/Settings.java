package com.example.busfare_splitterv2.UI;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.busfare_splitterv2.R;


public class Settings extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
    }

    public void openSupport(View v){
        Intent open = new Intent(this, SupportDetails.class);
        startActivity(open);
    }

    public void openAboutUS(View v){
        Intent open = new Intent(this, AboutUs.class);
        startActivity(open);
    }

    public void back(View v){
        Intent open = new Intent(this, ProfileActivity.class);
        startActivity(open);
    }
}
