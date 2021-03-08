package com.example.module1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        button3.setOnClickListener {
            val hello: HelloService = XRouter.sInstance.build(Config.HELLO_SERVICE).navigation() as HelloService
            hello.sayHello(this)
        }
        button4.setOnClickListener {
            val userService: UserServiceImpl = XRouter.sInstance.build(Config.USER_SERVICE_IMPL).navigation() as UserServiceImpl
            userService.login(this)
        }
        button5.setOnClickListener {
            val userService: IUserService? = XRouter.sInstance.navigation(IUserService::class.java)
            userService?.login(this)
        }
    }
}