package com.example.module1;

import android.content.Context;
import android.widget.Toast;

import com.example.base.Config;
import com.example.xrouter_core.template.IProvider;
import com.example.xrouter_annotations.Route;

@Route(path = Config.HELLO_SERVICE)
public class HelloService implements IProvider {
    @Override
    public void init(Context context) {
        Toast.makeText(context, "HelloService init successfully", Toast.LENGTH_SHORT).show();
    }
    public void sayHello(Context context) {
        Toast.makeText(context, "HelloService: Hello", Toast.LENGTH_SHORT).show();
    }
}
