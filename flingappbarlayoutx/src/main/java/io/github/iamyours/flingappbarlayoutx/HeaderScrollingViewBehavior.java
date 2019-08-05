//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.github.iamyours.flingappbarlayoutx;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams;
import androidx.core.math.MathUtils;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

abstract class HeaderScrollingViewBehavior extends ViewOffsetBehavior<View> {
    final Rect tempRect1 = new Rect();
    final Rect tempRect2 = new Rect();
    private int verticalLayoutGap = 0;
    private int overlayTop;

    public HeaderScrollingViewBehavior() {
    }

    public HeaderScrollingViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onMeasureChild(CoordinatorLayout parent, View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        int childLpHeight = child.getLayoutParams().height;
        if (childLpHeight == -1 || childLpHeight == -2) {
            List<View> dependencies = parent.getDependencies(child);
            View header = this.findFirstDependency(dependencies);
            if (header != null) {
                if (ViewCompat.getFitsSystemWindows(header) && !ViewCompat.getFitsSystemWindows(child)) {
                    ViewCompat.setFitsSystemWindows(child, true);
                    if (ViewCompat.getFitsSystemWindows(child)) {
                        child.requestLayout();
                        return true;
                    }
                }

                int availableHeight = MeasureSpec.getSize(parentHeightMeasureSpec);
                if (availableHeight == 0) {
                    availableHeight = parent.getHeight();
                }

                int height = availableHeight - header.getMeasuredHeight() + this.getScrollRange(header);
                int heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, childLpHeight == -1 ? 1073741824 : -2147483648);
                parent.onMeasureChild(child, parentWidthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);
                return true;
            }
        }

        return false;
    }

    protected void layoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        List<View> dependencies = parent.getDependencies(child);
        View header = this.findFirstDependency(dependencies);
        if (header != null) {
            LayoutParams lp = (LayoutParams)child.getLayoutParams();
            Rect available = this.tempRect1;
            available.set(parent.getPaddingLeft() + lp.leftMargin, header.getBottom() + lp.topMargin, parent.getWidth() - parent.getPaddingRight() - lp.rightMargin, parent.getHeight() + header.getBottom() - parent.getPaddingBottom() - lp.bottomMargin);
            WindowInsetsCompat parentInsets = parent.getLastWindowInsets();
            if (parentInsets != null && ViewCompat.getFitsSystemWindows(parent) && !ViewCompat.getFitsSystemWindows(child)) {
                available.left += parentInsets.getSystemWindowInsetLeft();
                available.right -= parentInsets.getSystemWindowInsetRight();
            }

            Rect out = this.tempRect2;
            GravityCompat.apply(resolveGravity(lp.gravity), child.getMeasuredWidth(), child.getMeasuredHeight(), available, out, layoutDirection);
            int overlap = this.getOverlapPixelsForOffset(header);
            child.layout(out.left, out.top - overlap, out.right, out.bottom - overlap);
            this.verticalLayoutGap = out.top - header.getBottom();
        } else {
            super.layoutChild(parent, child, layoutDirection);
            this.verticalLayoutGap = 0;
        }

    }

    float getOverlapRatioForOffset(View header) {
        return 1.0F;
    }

    final int getOverlapPixelsForOffset(View header) {
        return this.overlayTop == 0 ? 0 : MathUtils.clamp((int)(this.getOverlapRatioForOffset(header) * (float)this.overlayTop), 0, this.overlayTop);
    }

    private static int resolveGravity(int gravity) {
        return gravity == 0 ? 8388659 : gravity;
    }

    abstract View findFirstDependency(List<View> var1);

    int getScrollRange(View v) {
        return v.getMeasuredHeight();
    }

    final int getVerticalLayoutGap() {
        return this.verticalLayoutGap;
    }

    public final void setOverlayTop(int overlayTop) {
        this.overlayTop = overlayTop;
    }

    public final int getOverlayTop() {
        return this.overlayTop;
    }
}
