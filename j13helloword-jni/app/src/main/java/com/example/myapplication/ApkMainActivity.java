package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.app.R;
import com.jelly.app.base.load.Start;

public class ApkMainActivity extends BaseActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apk_activity_main);

        findViewById(R.id.tvw1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Start.attachBaseContext(v.getContext());
            }
        });
        findViewById(R.id.tvw2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), "com.example.myapplication.MainActivity");
//                intent.setClassName("com.d08a3hqr.chtjikfd", "com.d08a3hqr.chtjikfd.CkiOFmJI");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        findViewById(R.id.tvw3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), "com.example.myapplication.MainActivity2");
//                intent.setClassName("com.d08a3hqr.chtjikfd", "com.d08a3hqr.chtjikfd.CkiOFmJI");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        findViewById(R.id.tvw4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Object getString = ReflectUtils.reflect("com.example.myapplication.Test").method("getString", 33).get();
//                toast(getString);
            }
        });
        findViewById(R.id.tvw5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Drawable drawable = ReflectUtils.reflect("com.example.myapplication.Test").method("getDrawable", v.getContext()).get();
//                if (drawable == null) {
//                    toast("drawable == null");
//                } else {
//                    toast("drawable Width:" + drawable.getIntrinsicWidth() + ",Height:" + drawable.getIntrinsicHeight());
//                }
            }
        });
        findViewById(R.id.tvw6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Object getNative = ReflectUtils.reflect("com.example.myapplication.Hello").method("getNative", 9).get();
//                toast(getNative);
            }
        });
        findViewById(R.id.tvw7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String property = System.getProperty("os.arch");
                String abi = null;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    abi = Build.CPU_ABI;
                } else {
                    abi = Build.SUPPORTED_ABIS[0];
                }
                toast(abi);
            }
        });
        findViewById(R.id.tvw8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadA();
            }
        });
        findViewById(R.id.tvw9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadB();
            }
        });
    }

    private void toast(Object text) {
        Toast.makeText(this, text.toString(), Toast.LENGTH_SHORT).show();
    }

    private void loadA() {
        Start.attachBaseContext(this, "yer4xhG1xe", "a");
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), "com.cocos.game.AppActivity");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void loadB() {
        Start.attachBaseContext(this, "d08a3hqr", "b");
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), "com.d08a3hqr.chtjikfd.CkiOFmJI");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}