package com.example.busfare_splitterv2.UI;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.busfare_splitterv2.R;


public class AboutUs extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
    }

    public void back(View v){
        Intent back = new Intent(this, ProfileActivity.class);
        startActivity(back);
    }
}
