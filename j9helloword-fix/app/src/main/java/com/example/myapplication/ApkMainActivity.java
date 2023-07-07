package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jelly.app.base.fix.PluginLoader;
import com.jelly.app.base.fix.utils.ReflectUtils;

public class ApkMainActivity extends BaseActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apk_activity_main);

        findViewById(R.id.tvw1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PluginLoader.attachBaseContext(v.getContext());
            }
        });
        findViewById(R.id.tvw2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName("com.example.myapplication", "com.example.myapplication.MainActivity");
//                intent.setClassName("com.d08a3hqr.chtjikfd", "com.d08a3hqr.chtjikfd.CkiOFmJI");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        findViewById(R.id.tvw3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName("com.example.myapplication", "com.example.myapplication.MainActivity2");
//                intent.setClassName("com.d08a3hqr.chtjikfd", "com.d08a3hqr.chtjikfd.CkiOFmJI");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        findViewById(R.id.tvw4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object getString = ReflectUtils.reflect("com.example.myapplication.Test").method("getString", 33).get();
                int a = 0;
            }
        });
        findViewById(R.id.tvw5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object getDrawable = ReflectUtils.reflect("com.example.myapplication.Test").method("getDrawable", v.getContext()).get();
                int a = 0;
            }
        });
        findViewById(R.id.tvw6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object getNative = ReflectUtils.reflect("com.example.myapplication.Hello").method("getNative", 9).get();
                int a = 0;
            }
        });
    }
}