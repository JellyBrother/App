package com.jelly.app;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jelly.chatlibrary.message.activity.MessageMainActivity;
import com.jelly.wxtool.message.activity.WxTest7Activity;
import com.jelly.wxtool.message.activity.WxToolMainActivity;

public class AppMainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] options = {"跳转MessageMainActivity", "跳转微信工具类", "跳转不带辅助功能的微信工具类"};
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent;
        switch (position) {
            default:
            case 0:
                intent = new Intent(this, MessageMainActivity.class);
            case 1:
                intent = new Intent(this, WxToolMainActivity.class);
                break;
            case 2:
                intent = new Intent(this, WxTest7Activity.class);
                break;
        }
        startActivity(intent);
    }
}
