package com.jelly.othertool.column.utils;


import android.content.Context;
import android.text.TextUtils;

import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.baselibrary.utils.ScreenUtils;
import com.jelly.baselibrary.utils.SizeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColumnSystem {
    private static final String TAG = "ColumnSystem";
    public static final String DEFINITION = "c4m16g8d0-c8m16g8d0-c12m24g12d0";
    /**
     * c是栅格column，m是边距margin，g是间隔gutter，d是偏移deflection
     */
    public int screenWidth;
    public int columnCount;
    public int marginWidth;
    public int gutterWidth;
    public int deflectionWidth;
    public float columnWidth;

    public ColumnSystem(Context context, String definition) {
        if (context == null) {
            LogUtil.getInstance().d(TAG, "Constructor context == null");
            return;
        }
        if (TextUtils.isEmpty(definition)) {
            LogUtil.getInstance().d(TAG, "Constructor TextUtils.isEmpty(definition)");
            return;
        }
        String[] definitionSplit = definition.split("-");
        if (definitionSplit == null || definitionSplit.length < 3) {
            LogUtil.getInstance().d(TAG, "Constructor definitionSplit == null || definitionSplit.length < 3");
            return;
        }
        screenWidth = ScreenUtils.getScreenWidth(context);
        if (screenWidth >= SizeUtils.dp2px(840)) {
            definition = definitionSplit[2];
        } else if (screenWidth >= SizeUtils.dp2px(600)) {
            definition = definitionSplit[1];
        } else {
            definition = definitionSplit[0];
        }
        Pattern pattern = Pattern.compile("^c(\\d+)m(\\d+)g(\\d+)d(\\d+)");
        Matcher matcher = pattern.matcher(definition);
        if (!matcher.find() || matcher.groupCount() < 4) {
            LogUtil.getInstance().d(TAG, "Constructor !matcher.find() || matcher.groupCount() < 4");
            return;
        }
        columnCount = Integer.parseInt(matcher.group(1));
        marginWidth = SizeUtils.dp2px(Integer.parseInt(matcher.group(2)));
        gutterWidth = SizeUtils.dp2px(Integer.parseInt(matcher.group(3)));
        deflectionWidth = SizeUtils.dp2px(Integer.parseInt(matcher.group(4)));
        columnWidth = (screenWidth - deflectionWidth - marginWidth * 2 - gutterWidth * (columnCount - 1)) / (float) (columnCount * 1.0f);
        LogUtil.getInstance().d(TAG, "Constructor columnCount:" + columnCount + ",marginWidth" + marginWidth +
                ",gutterWidth" + gutterWidth + ",deflectionWidth" + deflectionWidth + ",columnWidth" + columnWidth);
    }
}
