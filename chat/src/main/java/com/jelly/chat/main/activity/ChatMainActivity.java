package com.jelly.chat.main.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.jelly.baselibrary.utils.StatusBarUtil;
import com.jelly.chat.R;
import com.jelly.chat.main.fragment.FriendFragment;
import com.jelly.chat.main.fragment.MeFragment;
import com.jelly.chat.main.fragment.MessageFragment;

public class ChatMainActivity extends AppCompatActivity {

    private enum TabFragment {
        MessageFragment(R.id.i_navigation_message, MessageFragment.class),
        FriendFragment(R.id.i_navigation_friend, FriendFragment.class),
        MeFragment(R.id.i_navigation_me, MeFragment.class),
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_act_main);
        final BottomNavigationView navigation = findViewById(R.id.bn_navigation);
        navigation.setOnNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TabFragment.onDestroy();
    }
}
