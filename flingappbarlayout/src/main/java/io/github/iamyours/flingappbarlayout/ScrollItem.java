package io.github.iamyours.flingappbarlayout;

import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.OverScroller;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ScrollItem {
    private int type; //1: NestedScrollView   2:RecyclerView
    private WeakReference<NestedScrollView> scrollViewRef;
    private WeakReference<RecyclerView> recyclerViewRef;

    public ScrollItem(View v) {
        findScrollItem(v);
    }

    /**
     * 查找需要滑动的scroll对象
     *
     * @param v
     */
    protected boolean findScrollItem(View v) {
        if (findCommonScroll(v)) return true;
        if (v instanceof ViewPager) {
            View root = ViewPagerUtil.findCurrent((ViewPager) v);
            if (root != null) {
                View child = root.findViewWithTag("fling");
                return findCommonScroll(child);
            }
        }
        return false;
    }

    private boolean findCommonScroll(View v) {
        if (v instanceof NestedScrollView) {
            type = 1;
            scrollViewRef = new WeakReference<NestedScrollView>((NestedScrollView) v);
            stopScroll(scrollViewRef.get());
            return true;
        }
        if (v instanceof RecyclerView) {
            recyclerViewRef = new WeakReference((RecyclerView) v);
            type = 2;
            stopScroll((RecyclerView) v);
            return true;
        }
        return false;
    }

    /**
     * 停止NestedScrollView滚动
     *
     * @param v
     */
    private void stopScroll(NestedScrollView v) {
        try {
            Field field = ReflectUtil.getDeclaredField(v, "mScroller");
            if (field == null) return;
            field.setAccessible(true);
            OverScroller scroller = (OverScroller) field.get(v);
            if (scroller != null) scroller.abortAnimation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止RecyclerView滚动
     *
     * @param
     */
    private void stopScroll(RecyclerView rv) {
        lastY = 0;
        try {
            Field field = ReflectUtil.getDeclaredField(rv, "mViewFlinger");
            if (field == null) return;
            field.setAccessible(true);
            Object obj = field.get(rv);
            if (obj == null) return;
            Method method = obj.getClass().getDeclaredMethod("stop");
            method.setAccessible(true);
            method.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int lastY = 0;

    public void scroll(int dy) {
        if (type == 1) {
            scrollViewRef.get().scrollTo(0, dy);
        } else if (type == 2) {
            recyclerViewRef.get().scrollBy(0,dy-lastY);
            lastY = dy;
        }
    }

}
