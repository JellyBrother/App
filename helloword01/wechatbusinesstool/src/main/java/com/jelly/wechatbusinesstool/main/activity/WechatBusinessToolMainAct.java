package com.jelly.wechatbusinesstool.main.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.jelly.baselibrary.base.BaseLifecycleActivity;
import com.jelly.baselibrary.utils.PublicUtil;
import com.jelly.baselibrary.utils.StatusBarUtil;
import com.jelly.wechatbusinesstool.home.fragment.HomeFra;
import com.jelly.wechatbusinesstool.main.adapter.WechatBusinessToolMainAdapter;
import com.jelly.wechatbusinesstool.main.entity.WechatBusinessToolMainEntity;
import com.jelly.wechatbusinesstool.main.viewmodel.WechatBusinessToolMainActViewModel;
import com.jelly.wechatbusinesstool.R;
import com.jelly.wechatbusinesstool.me.fragment.MeFra;
import com.jelly.wechatbusinesstool.profit.fragment.ProfitFra;
import com.jelly.wechatbusinesstool.shop.fragment.ShopFra;

import java.util.ArrayList;

public class WechatBusinessToolMainAct extends BaseLifecycleActivity<WechatBusinessToolMainActViewModel> {
    private static final String TAG = "WechatBusinessToolMainAct";
    private ViewPager mVpMain;
    private BottomNavigationView mNvMain;
    private ArrayList<WechatBusinessToolMainEntity> mList;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.wbt_act_main);
        initViews();
        initListeners();
    }

    @Override
    protected WechatBusinessToolMainActViewModel initViewModel() {
        return new WechatBusinessToolMainActViewModel();
    }

    @Override
    protected void observerData() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mList.clear();
    }

    private void initViews() {
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, 0xff000000, 0.1f);
        mVpMain = findViewById(R.id.vp_main);
        mNvMain = findViewById(R.id.nv_main);
    }

    private void initListeners() {
        mNvMain.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (PublicUtil.isEmptyList(mList)) {
                    return false;
                }
                int id = menuItem.getItemId();
                for (WechatBusinessToolMainEntity entity : mList) {
                    if (id == entity.navigationItemId) {
                        mVpMain.setCurrentItem(entity.position);
                    }
                }
                return true;
            }
        });

        mList = new ArrayList<>();
        mList.add(new WechatBusinessToolMainEntity(0, R.string.wbt_home_fra_name, R.id.i_navigation_home, new HomeFra()));
        mList.add(new WechatBusinessToolMainEntity(1, R.string.wbt_shop_act_name, R.id.i_navigation_shop, new ShopFra()));
        mList.add(new WechatBusinessToolMainEntity(2, R.string.wbt_profit_fra_name, R.id.i_navigation_profit, new ProfitFra()));
        mList.add(new WechatBusinessToolMainEntity(3, R.string.wbt_me_fra_name, R.id.i_navigation_me, new MeFra()));
        WechatBusinessToolMainAdapter pagerAdapter = new WechatBusinessToolMainAdapter(mActivity, mList, getSupportFragmentManager());
        mVpMain.setAdapter(pagerAdapter);
        mVpMain.setOffscreenPageLimit(2);
        mVpMain.setCurrentItem(0);
        mVpMain.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (PublicUtil.isEmptyList(mList)) {
                    return;
                }
                if (position >= mList.size()) {
                    return;
                }
                mNvMain.setSelectedItemId(mList.get(position).navigationItemId);
            }
        });
    }
}
