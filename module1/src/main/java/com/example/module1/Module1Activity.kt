package com.example.module1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.base.Config
import com.example.xrouter_annotations.Route

@Route(path = Config.Module1Activity)
class Module1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_module1)
    }
}