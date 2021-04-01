package com.jelly.othertool.column;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jelly.baselibrary.base.BaseActivity;
import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.othertool.R;
import com.jelly.othertool.column.utils.ColumnSystem;

public class ColumnSystemMainAct extends BaseActivity {
    private static final String TAG = "ColumnSystemMainAct";
    private TextView mTvJumpColumn;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.column_act_main);
        initViews();
        initListeners();
    }

    private void initViews() {
        mTvJumpColumn = findViewById(R.id.tv_jump_column);
    }

    private void initListeners() {
        mTvJumpColumn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColumnSystem columnSystem = new ColumnSystem(ColumnSystemMainAct.this, ColumnSystem.DEFINITION);
                LogUtil.getInstance().d(TAG, "new ColumnSystem");
            }
        });
    }
}
