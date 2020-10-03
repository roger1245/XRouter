package com.example.module1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.base.Config;
import com.example.xrouter_annotations.Route;

@Route(path = Config.Module1ThirdActivity)
public class Module1ThirdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module1_third);
    }
}