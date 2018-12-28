package io.github.iamyours.flingappbarlayout.widget;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.impl.RefreshContentWrapper;
import com.scwang.smartrefresh.layout.util.CoordinatorLayoutListener;

public class MyRefreshContentWrapper extends RefreshContentWrapper {
    public MyRefreshContentWrapper(View view) {
        super(view);
    }

    @Override
    protected void findScrollableView(View content, RefreshKernel kernel) {
        mScrollableView = null;
        CoordinatorLayoutListener listener = null;
        boolean isInEditMode = mContentView.isInEditMode();
        while (mScrollableView == null || (mScrollableView instanceof NestedScrollingParent
                && !(mScrollableView instanceof NestedScrollingChild))) {
            content = findScrollableViewInternal(content, mScrollableView == null);
            if (content == mScrollableView) {
                break;
            }
            if (!isInEditMode) {
                if (listener == null) {
                    listener = new CoordinatorLayoutListener() {
                        @Override
                        public void update(boolean enableRefresh, boolean enableLoadMore) {
                            mEnableRefresh = enableRefresh;
                            mEnableLoadMore = enableLoadMore;
                        }
                    };
                }
                checkCoordinatorLayout(content, kernel, listener);
            }
            mScrollableView = content;
        }
    }

    public static void checkCoordinatorLayout(View content, RefreshKernel kernel, CoordinatorLayoutListener listener) {
        try {//try 不能删除，不然会出现兼容性问题
            if (content instanceof CoordinatorLayout) {
                kernel.getRefreshLayout().setEnableNestedScroll(false);
                wrapperCoordinatorLayout(((ViewGroup) content), kernel.getRefreshLayout(), listener);
            }
        } catch (Throwable ignored) {
        }
    }

    private static void wrapperCoordinatorLayout(ViewGroup layout, final RefreshLayout refreshLayout, final CoordinatorLayoutListener listener) {
        for (int i = layout.getChildCount() - 1; i >= 0; i--) {
            View view = layout.getChildAt(i);
            if (view instanceof AppBarLayout) {
                ((AppBarLayout) view).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                        listener.update(
                                verticalOffset >= 0,
                                refreshLayout.isEnableLoadMore() && (appBarLayout.getTotalScrollRange() + verticalOffset) <= 0);
                    }
                });
            }
            if (view instanceof io.github.iamyours.flingappbarlayout.AppBarLayout) {//自定义AppBarLayout
                ((io.github.iamyours.flingappbarlayout.AppBarLayout) view).addOnOffsetChangedListener(new io.github.iamyours.flingappbarlayout.AppBarLayout.OnOffsetChangedListener() {
                    @Override
                    public void onOffsetChanged(io.github.iamyours.flingappbarlayout.AppBarLayout appBarLayout, int verticalOffset) {
                        listener.update(
                                verticalOffset >= 0,
                                refreshLayout.isEnableLoadMore() && (appBarLayout.getTotalScrollRange() + verticalOffset) <= 0);
                    }
                });
            }
        }
    }
}
