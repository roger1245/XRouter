package com.example.module2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.base.Config
import com.example.xrouter_annotations.Route

@Route(path = Config.Module2Activity)
class Module2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_module2)
    }
}