package com.jelly.chat.main.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jelly.baselibrary.base.BaseActivity;
import com.jelly.baselibrary.utils.StatusBarUtil;
import com.jelly.chat.R;
import com.jelly.chat.main.fragment.FriendFra;
import com.jelly.chat.main.fragment.MeFra;
import com.jelly.chat.main.fragment.MessageFra;

public class ChatMainAct extends BaseActivity {

    private enum TabFragment {
        MessageFragment(R.id.i_navigation_message, MessageFra.class),
        FriendFragment(R.id.i_navigation_friend, FriendFra.class),
        MeFragment(R.id.i_navigation_me, MeFra.class),
        ;

        private Fragment fragment;
        private final int menuId;
        private final Class<? extends Fragment> clazz;

        TabFragment(@IdRes int menuId, Class<? extends Fragment> clazz) {
            this.menuId = menuId;
            this.clazz = clazz;
        }

        @NonNull
        public Fragment fragment() {
            if (fragment == null) {
                try {
                    fragment = clazz.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    fragment = new Fragment();
                }
            }
            return fragment;
        }

        public static TabFragment from(int itemId) {
            for (TabFragment fragment : values()) {
                if (fragment.menuId == itemId) {
                    return fragment;
                }
            }
            return MessageFragment;
        }

        public static void onDestroy() {
            for (TabFragment fragment : values()) {
                fragment.fragment = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TabFragment.onDestroy();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.message_act_main);
        final BottomNavigationView navigation = findViewById(R.id.bn_navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                ((ViewPager) findViewById(R.id.vp_content)).setCurrentItem(TabFragment.from(menuItem.getItemId()).ordinal());
                return true;
            }
        });
        ViewPager viewPager = findViewById(R.id.vp_content);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return TabFragment.values().length;
            }

            @Override
            public Fragment getItem(int position) {
                return TabFragment.values()[position].fragment();
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                navigation.setSelectedItemId(TabFragment.values()[position].menuId);
            }
        });
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this, 0xff000000, 0.1f);
    }
}
