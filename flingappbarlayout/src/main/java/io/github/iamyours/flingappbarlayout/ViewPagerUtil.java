package io.github.iamyours.flingappbarlayout;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ViewPagerUtil {
    public static View findCurrent(ViewPager vp) {
        int position = vp.getCurrentItem();
        PagerAdapter adapter = vp.getAdapter();
        if (adapter instanceof FragmentStatePagerAdapter) {
            FragmentStatePagerAdapter fsp = (FragmentStatePagerAdapter) adapter;
            return fsp.getItem(position).getView();
        } else if (adapter instanceof FragmentPagerAdapter) {
            FragmentPagerAdapter fp = (FragmentPagerAdapter) adapter;
            return fp.getItem(position).getView();
        }
        return null;
    }
}
