package com.jelly.wechatbusinesstool.main.adapter;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jelly.baselibrary.utils.PublicUtil;
import com.jelly.wechatbusinesstool.main.entity.WechatBusinessToolMainEntity;

import java.util.ArrayList;

public class WechatBusinessToolMainAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private Resources mResources;
    private ArrayList<WechatBusinessToolMainEntity> mData;

    public WechatBusinessToolMainAdapter(Context context, ArrayList<WechatBusinessToolMainEntity> list, FragmentManager fm) {
        super(fm);
        mContext = context;
        mData = list;
        mResources = mContext.getResources();
    }

    @Override
    public Fragment getItem(int position) {
        if (PublicUtil.isEmptyList(mData)) {
            return null;
        }
        if (position >= mData.size()) {
            return null;
        }
        return mData.get(position).fragment;
    }

    @Override
    public int getCount() {
        if (PublicUtil.isEmptyList(mData)) {
            return 0;
        }
        return mData.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (PublicUtil.isEmptyList(mData)) {
            return null;
        }
        if (position >= mData.size()) {
            return null;
        }
        return mResources.getString(mData.get(position).title);
    }
}
