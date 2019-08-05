package io.github.iamyours.flingappbarlayoutx;

import android.view.View;

import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

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
