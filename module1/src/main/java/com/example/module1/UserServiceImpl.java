package com.example.module1;

import android.content.Context;
import android.widget.Toast;

import com.example.base.Config;
import com.example.xrouter_annotations.Route;

@Route(path = Config.USER_SERVICE_IMPL)
public class UserServiceImpl implements IUserService {
    @Override
    public void init(Context context) {
        Toast.makeText(context, "UserServiceImpl init successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void login(Context context) {
        Toast.makeText(context, "UserServiceImpl login successfully", Toast.LENGTH_SHORT).show();

    }
}
