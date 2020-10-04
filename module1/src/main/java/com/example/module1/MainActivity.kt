package com.example.module1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.base.Config
import com.example.xroute_core.XRouter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            XRouter.sInstance.build(Config.Module1Activity).navigation()
        }
        button2.setOnClickListener {
            XRouter.sInstance.build(Config.Module2Activity).navigation()
        }
    }
}