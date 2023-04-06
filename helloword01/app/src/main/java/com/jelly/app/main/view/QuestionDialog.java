package com.jelly.app.main.view;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jelly.app.R;

public class QuestionDialog extends Dialog {

    private TextView mTvTitle;
    private Button mBtnYes;
    private Button mBtnNo;

    public QuestionDialog(@NonNull Context context) {
        super(context);

        setContentView(R.layout.dialog_question);

        mTvTitle = findViewById(R.id.tv_title);
        mBtnYes = findViewById(R.id.btn_yes);
        mBtnNo = findViewById(R.id.btn_no);

    }

    public void show(String title) {
        mTvTitle.setText(title);
        show();
    }
}