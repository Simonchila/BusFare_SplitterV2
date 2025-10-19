package com.example.busfare_splitterv2.UI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.busfare_splitterv2.R;

public class GroupActivityMsg extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_msg);

        Button btn_continue = findViewById(R.id.btn_continue);


        btn_continue.setOnClickListener(v -> {
            startActivity(new Intent(GroupActivityMsg.this, LoginActivity.class));
            finish();
        });
    }

}