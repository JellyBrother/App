/**
 *
 */
package com.example.myapp.base.ui.adapter;

import android.os.Parcelable;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.myapp.base.ui.fragment.BaseFragment;
import com.example.myapp.base.utils.ListUtil;

import java.util.List;

/**
 *  viewPager调用的Adapter，因为移动了tab,fragment也会变换位置所以继承了FragmentStatePagerAdapter
 */
public class BaseViewPagerAdapter extends FragmentStatePagerAdapter {

    /**
     * @param fm
     */
    private final List<BaseFragment> list;

    public BaseViewPagerAdapter(FragmentManager fm, List<BaseFragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public BaseFragment getItem(int position) {
        if (ListUtil.isIndexOut(list, position)) {
            return null;
        }
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        try { //解决中英文切换后，返回主页面，闪退问题
            super.restoreState(state, loader);
        } catch (Exception e) {
            //do nothing
        }
    }

    /**
     * 必须返回POSITION_NONE，不然fragment不会有变换
     */
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
