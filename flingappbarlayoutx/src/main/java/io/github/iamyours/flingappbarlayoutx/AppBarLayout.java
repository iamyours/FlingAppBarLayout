//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.github.iamyours.flingappbarlayoutx;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import androidx.annotation.VisibleForTesting;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout.DefaultBehavior;
import androidx.core.math.MathUtils;
import androidx.core.util.ObjectsCompat;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.customview.view.AbsSavedState;

import com.google.android.material.R.attr;
import com.google.android.material.R.style;
import com.google.android.material.R.styleable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@DefaultBehavior(AppBarLayout.Behavior.class)
public class AppBarLayout extends LinearLayout {
    static final int PENDING_ACTION_NONE = 0;
    static final int PENDING_ACTION_EXPANDED = 1;
    static final int PENDING_ACTION_COLLAPSED = 2;
    static final int PENDING_ACTION_ANIMATE_ENABLED = 4;
    static final int PENDING_ACTION_FORCE = 8;
    private static final int INVALID_SCROLL_RANGE = -1;
    private int totalScrollRange;
    private int downPreScrollRange;
    private int downScrollRange;
    private boolean haveChildWithInterpolator;
    private int pendingAction;
    private WindowInsetsCompat lastInsets;
    private List<BaseOnOffsetChangedListener> listeners;
    private boolean liftableOverride;
    private boolean liftable;
    private boolean lifted;
    private boolean liftOnScroll;
    private int[] tmpStatesArray;

    public AppBarLayout(Context context) {
        this(context, (AttributeSet)null);
    }

    public AppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.totalScrollRange = -1;
        this.downPreScrollRange = -1;
        this.downScrollRange = -1;
        this.pendingAction = 0;
        this.setOrientation(1);
        if (VERSION.SDK_INT >= 21) {
            ViewUtilsLollipop.setBoundsViewOutlineProvider(this);
            ViewUtilsLollipop.setStateListAnimatorFromAttrs(this, attrs, 0, style.Widget_Design_AppBarLayout);
        }

        TypedArray a = ThemeEnforcement.obtainStyledAttributes(context, attrs, styleable.AppBarLayout, 0, style.Widget_Design_AppBarLayout, new int[0]);
        ViewCompat.setBackground(this, a.getDrawable(styleable.AppBarLayout_android_background));
        if (a.hasValue(styleable.AppBarLayout_expanded)) {
            this.setExpanded(a.getBoolean(styleable.AppBarLayout_expanded, false), false, false);
        }

        if (VERSION.SDK_INT >= 21 && a.hasValue(styleable.AppBarLayout_elevation)) {
            ViewUtilsLollipop.setDefaultAppBarLayoutStateListAnimator(this, (float)a.getDimensionPixelSize(styleable.AppBarLayout_elevation, 0));
        }

        if (VERSION.SDK_INT >= 26) {
            if (a.hasValue(styleable.AppBarLayout_android_keyboardNavigationCluster)) {
                this.setKeyboardNavigationCluster(a.getBoolean(styleable.AppBarLayout_android_keyboardNavigationCluster, false));
            }

            if (a.hasValue(styleable.AppBarLayout_android_touchscreenBlocksFocus)) {
                this.setTouchscreenBlocksFocus(a.getBoolean(styleable.AppBarLayout_android_touchscreenBlocksFocus, false));
            }
        }

        this.liftOnScroll = a.getBoolean(styleable.AppBarLayout_liftOnScroll, false);
        a.recycle();
        ViewCompat.setOnApplyWindowInsetsListener(this, new androidx.core.view.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                return onWindowInsetChanged(insets);
            }
        });
    }

    public void addOnOffsetChangedListener(AppBarLayout.BaseOnOffsetChangedListener listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList();
        }

        if (listener != null && !this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }

    }

    public void addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener listener) {
        this.addOnOffsetChangedListener((AppBarLayout.BaseOnOffsetChangedListener)listener);
    }

    public void removeOnOffsetChangedListener(AppBarLayout.BaseOnOffsetChangedListener listener) {
        if (this.listeners != null && listener != null) {
            this.listeners.remove(listener);
        }

    }

    public void removeOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener listener) {
        this.removeOnOffsetChangedListener((AppBarLayout.BaseOnOffsetChangedListener)listener);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.invalidateScrollRanges();
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.invalidateScrollRanges();
        this.haveChildWithInterpolator = false;
        int i = 0;

        for(int z = this.getChildCount(); i < z; ++i) {
            View child = this.getChildAt(i);
            AppBarLayout.LayoutParams childLp = (AppBarLayout.LayoutParams)child.getLayoutParams();
            Interpolator interpolator = childLp.getScrollInterpolator();
            if (interpolator != null) {
                this.haveChildWithInterpolator = true;
                break;
            }
        }

        if (!this.liftableOverride) {
            this.setLiftableState(this.liftOnScroll || this.hasCollapsibleChild());
        }

    }

    private boolean hasCollapsibleChild() {
        int i = 0;

        for(int z = this.getChildCount(); i < z; ++i) {
            if (((AppBarLayout.LayoutParams)this.getChildAt(i).getLayoutParams()).isCollapsible()) {
                return true;
            }
        }

        return false;
    }

    private void invalidateScrollRanges() {
        this.totalScrollRange = -1;
        this.downPreScrollRange = -1;
        this.downScrollRange = -1;
    }

    public void setOrientation(int orientation) {
        if (orientation != 1) {
            throw new IllegalArgumentException("AppBarLayout is always vertical and does not support horizontal orientation");
        } else {
            super.setOrientation(orientation);
        }
    }

    public void setExpanded(boolean expanded) {
        this.setExpanded(expanded, ViewCompat.isLaidOut(this));
    }

    public void setExpanded(boolean expanded, boolean animate) {
        this.setExpanded(expanded, animate, true);
    }

    private void setExpanded(boolean expanded, boolean animate, boolean force) {
        this.pendingAction = (expanded ? 1 : 2) | (animate ? 4 : 0) | (force ? 8 : 0);
        this.requestLayout();
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof AppBarLayout.LayoutParams;
    }

    protected AppBarLayout.LayoutParams generateDefaultLayoutParams() {
        return new AppBarLayout.LayoutParams(-1, -2);
    }

    public AppBarLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new AppBarLayout.LayoutParams(this.getContext(), attrs);
    }

    protected AppBarLayout.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        if (VERSION.SDK_INT >= 19 && p instanceof LinearLayout.LayoutParams) {
            return new AppBarLayout.LayoutParams((LinearLayout.LayoutParams)p);
        } else {
            return p instanceof MarginLayoutParams ? new AppBarLayout.LayoutParams((MarginLayoutParams)p) : new AppBarLayout.LayoutParams(p);
        }
    }

    boolean hasChildWithInterpolator() {
        return this.haveChildWithInterpolator;
    }

    public final int getTotalScrollRange() {
        if (this.totalScrollRange != -1) {
            return this.totalScrollRange;
        } else {
            int range = 0;
            int i = 0;

            for(int z = this.getChildCount(); i < z; ++i) {
                View child = this.getChildAt(i);
                AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams)child.getLayoutParams();
                int childHeight = child.getMeasuredHeight();
                int flags = lp.scrollFlags;
                if ((flags & 1) == 0) {
                    break;
                }

                range += childHeight + lp.topMargin + lp.bottomMargin;
                if ((flags & 2) != 0) {
                    range -= ViewCompat.getMinimumHeight(child);
                    break;
                }
            }

            return this.totalScrollRange = Math.max(0, range - this.getTopInset());
        }
    }

    boolean hasScrollableChildren() {
        return this.getTotalScrollRange() != 0;
    }

    int getUpNestedPreScrollRange() {
        return this.getTotalScrollRange();
    }

    int getDownNestedPreScrollRange() {
        if (this.downPreScrollRange != -1) {
            return this.downPreScrollRange;
        } else {
            int range = 0;

            for(int i = this.getChildCount() - 1; i >= 0; --i) {
                View child = this.getChildAt(i);
                AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams)child.getLayoutParams();
                int childHeight = child.getMeasuredHeight();
                int flags = lp.scrollFlags;
                if ((flags & 5) == 5) {
                    range += lp.topMargin + lp.bottomMargin;
                    if ((flags & 8) != 0) {
                        range += ViewCompat.getMinimumHeight(child);
                    } else if ((flags & 2) != 0) {
                        range += childHeight - ViewCompat.getMinimumHeight(child);
                    } else {
                        range += childHeight - this.getTopInset();
                    }
                } else if (range > 0) {
                    break;
                }
            }

            return this.downPreScrollRange = Math.max(0, range);
        }
    }

    int getDownNestedScrollRange() {
        if (this.downScrollRange != -1) {
            return this.downScrollRange;
        } else {
            int range = 0;
            int i = 0;

            for(int z = this.getChildCount(); i < z; ++i) {
                View child = this.getChildAt(i);
                AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams)child.getLayoutParams();
                int childHeight = child.getMeasuredHeight();
                childHeight += lp.topMargin + lp.bottomMargin;
                int flags = lp.scrollFlags;
                if ((flags & 1) == 0) {
                    break;
                }

                range += childHeight;
                if ((flags & 2) != 0) {
                    range -= ViewCompat.getMinimumHeight(child) + this.getTopInset();
                    break;
                }
            }

            return this.downScrollRange = Math.max(0, range);
        }
    }

    void dispatchOffsetUpdates(int offset) {
        if (this.listeners != null) {
            int i = 0;

            for(int z = this.listeners.size(); i < z; ++i) {
                AppBarLayout.BaseOnOffsetChangedListener listener = (AppBarLayout.BaseOnOffsetChangedListener)this.listeners.get(i);
                if (listener != null) {
                    listener.onOffsetChanged(this, offset);
                }
            }
        }

    }

    public final int getMinimumHeightForVisibleOverlappingContent() {
        int topInset = this.getTopInset();
        int minHeight = ViewCompat.getMinimumHeight(this);
        if (minHeight != 0) {
            return minHeight * 2 + topInset;
        } else {
            int childCount = this.getChildCount();
            int lastChildMinHeight = childCount >= 1 ? ViewCompat.getMinimumHeight(this.getChildAt(childCount - 1)) : 0;
            return lastChildMinHeight != 0 ? lastChildMinHeight * 2 + topInset : this.getHeight() / 3;
        }
    }

    protected int[] onCreateDrawableState(int extraSpace) {
        if (this.tmpStatesArray == null) {
            this.tmpStatesArray = new int[4];
        }

        int[] extraStates = this.tmpStatesArray;
        int[] states = super.onCreateDrawableState(extraSpace + extraStates.length);
        extraStates[0] = this.liftable ? attr.state_liftable : -attr.state_liftable;
        extraStates[1] = this.liftable && this.lifted ? attr.state_lifted : -attr.state_lifted;
        extraStates[2] = this.liftable ? attr.state_collapsible : -attr.state_collapsible;
        extraStates[3] = this.liftable && this.lifted ? attr.state_collapsed : -attr.state_collapsed;
        return mergeDrawableStates(states, extraStates);
    }

    public boolean setLiftable(boolean liftable) {
        this.liftableOverride = true;
        return this.setLiftableState(liftable);
    }

    private boolean setLiftableState(boolean liftable) {
        if (this.liftable != liftable) {
            this.liftable = liftable;
            this.refreshDrawableState();
            return true;
        } else {
            return false;
        }
    }

    public boolean setLifted(boolean lifted) {
        return this.setLiftedState(lifted);
    }

    boolean setLiftedState(boolean lifted) {
        if (this.lifted != lifted) {
            this.lifted = lifted;
            this.refreshDrawableState();
            return true;
        } else {
            return false;
        }
    }

    public void setLiftOnScroll(boolean liftOnScroll) {
        this.liftOnScroll = liftOnScroll;
    }

    public boolean isLiftOnScroll() {
        return this.liftOnScroll;
    }

    /** @deprecated */
    @Deprecated
    public void setTargetElevation(float elevation) {
        if (VERSION.SDK_INT >= 21) {
            ViewUtilsLollipop.setDefaultAppBarLayoutStateListAnimator(this, elevation);
        }

    }

    /** @deprecated */
    @Deprecated
    public float getTargetElevation() {
        return 0.0F;
    }

    int getPendingAction() {
        return this.pendingAction;
    }

    void resetPendingAction() {
        this.pendingAction = 0;
    }

    @VisibleForTesting
    final int getTopInset() {
        return this.lastInsets != null ? this.lastInsets.getSystemWindowInsetTop() : 0;
    }

    WindowInsetsCompat onWindowInsetChanged(WindowInsetsCompat insets) {
        WindowInsetsCompat newInsets = null;
        if (ViewCompat.getFitsSystemWindows(this)) {
            newInsets = insets;
        }

        if (!ObjectsCompat.equals(this.lastInsets, newInsets)) {
            this.lastInsets = newInsets;
            this.invalidateScrollRanges();
        }

        return insets;
    }

    public static class ScrollingViewBehavior extends HeaderScrollingViewBehavior {
        public ScrollingViewBehavior() {
        }

        public ScrollingViewBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, styleable.ScrollingViewBehavior_Layout);
            this.setOverlayTop(a.getDimensionPixelSize(styleable.ScrollingViewBehavior_Layout_behavior_overlapTop, 0));
            a.recycle();
        }

        public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
            return dependency instanceof AppBarLayout;
        }

        public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
            this.offsetChildAsNeeded(child, dependency);
            this.updateLiftedStateIfNeeded(child, dependency);
            return false;
        }

        public boolean onRequestChildRectangleOnScreen(CoordinatorLayout parent, View child, Rect rectangle, boolean immediate) {
            AppBarLayout header = this.findFirstDependency(parent.getDependencies(child));
            if (header != null) {
                rectangle.offset(child.getLeft(), child.getTop());
                Rect parentRect = this.tempRect1;
                parentRect.set(0, 0, parent.getWidth(), parent.getHeight());
                if (!parentRect.contains(rectangle)) {
                    header.setExpanded(false, !immediate);
                    return true;
                }
            }

            return false;
        }

        private void offsetChildAsNeeded(View child, View dependency) {
            androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior behavior = ((androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams)dependency.getLayoutParams()).getBehavior();
            if (behavior instanceof AppBarLayout.BaseBehavior) {
                AppBarLayout.BaseBehavior ablBehavior = (AppBarLayout.BaseBehavior)behavior;
                ViewCompat.offsetTopAndBottom(child, dependency.getBottom() - child.getTop() + ablBehavior.offsetDelta + this.getVerticalLayoutGap() - this.getOverlapPixelsForOffset(dependency));
            }

        }

        float getOverlapRatioForOffset(View header) {
            if (header instanceof AppBarLayout) {
                AppBarLayout abl = (AppBarLayout)header;
                int totalScrollRange = abl.getTotalScrollRange();
                int preScrollDown = abl.getDownNestedPreScrollRange();
                int offset = getAppBarLayoutOffset(abl);
                if (preScrollDown != 0 && totalScrollRange + offset <= preScrollDown) {
                    return 0.0F;
                }

                int availScrollRange = totalScrollRange - preScrollDown;
                if (availScrollRange != 0) {
                    return 1.0F + (float)offset / (float)availScrollRange;
                }
            }

            return 0.0F;
        }

        private static int getAppBarLayoutOffset(AppBarLayout abl) {
            androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior behavior = ((androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams)abl.getLayoutParams()).getBehavior();
            return behavior instanceof AppBarLayout.BaseBehavior ? ((AppBarLayout.BaseBehavior)behavior).getTopBottomOffsetForScrollingSibling() : 0;
        }

        AppBarLayout findFirstDependency(List<View> views) {
            int i = 0;

            for(int z = views.size(); i < z; ++i) {
                View view = (View)views.get(i);
                if (view instanceof AppBarLayout) {
                    return (AppBarLayout)view;
                }
            }

            return null;
        }

        int getScrollRange(View v) {
            return v instanceof AppBarLayout ? ((AppBarLayout)v).getTotalScrollRange() : super.getScrollRange(v);
        }

        private void updateLiftedStateIfNeeded(View child, View dependency) {
            if (dependency instanceof AppBarLayout) {
                AppBarLayout appBarLayout = (AppBarLayout)dependency;
                if (appBarLayout.isLiftOnScroll()) {
                    appBarLayout.setLiftedState(child.getScrollY() > 0);
                }
            }

        }
    }

    protected static class BaseBehavior<T extends AppBarLayout> extends HeaderBehavior<T> {
        private static final int MAX_OFFSET_ANIMATION_DURATION = 600;
        private static final int INVALID_POSITION = -1;
        private int offsetDelta;
        private int lastStartedType;
        private ValueAnimator offsetAnimator;
        private int offsetToChildIndexOnLayout = -1;
        private boolean offsetToChildIndexOnLayoutIsMinHeight;
        private float offsetToChildIndexOnLayoutPerc;
        private WeakReference<View> lastNestedScrollingChildRef;
        private AppBarLayout.BaseBehavior.BaseDragCallback onDragCallback;

        public BaseBehavior() {
        }

        public BaseBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public boolean onStartNestedScroll(CoordinatorLayout parent, T child, View directTargetChild, View target, int nestedScrollAxes, int type) {
            boolean started = (nestedScrollAxes & 2) != 0 && (child.isLiftOnScroll() || this.canScrollChildren(parent, child, directTargetChild));
            if (started && this.offsetAnimator != null) {
                this.offsetAnimator.cancel();
            }

            this.lastNestedScrollingChildRef = null;
            this.lastStartedType = type;
            return started;
        }

        private boolean canScrollChildren(CoordinatorLayout parent, T child, View directTargetChild) {
            return child.hasScrollableChildren() && parent.getHeight() - directTargetChild.getHeight() <= child.getHeight();
        }

        public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, T child, View target, int dx, int dy, int[] consumed, int type) {
            //处理fling回弹
            if (type == ViewCompat.TYPE_NON_TOUCH) {
                if (mScroller != null && mScroller.computeScrollOffset()) {
                    mScroller.abortAnimation();
                }
                if (getTopAndBottomOffset() == 0) {
                    ViewCompat.stopNestedScroll(target, type);
                }
            }
            if (dy != 0) {
                int min;
                int max;
                if (dy < 0) {
                    min = -child.getTotalScrollRange();
                    max = min + child.getDownNestedPreScrollRange();
                } else {
                    min = -child.getUpNestedPreScrollRange();
                    max = 0;
                }

                if (min != max) {
                    consumed[1] = this.scroll(coordinatorLayout, child, dy, min, max);
                    this.stopNestedScrollIfNeeded(dy, child, target, type);
                }
            }

        }

        public void onNestedScroll(CoordinatorLayout coordinatorLayout, T child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
            if (dyUnconsumed < 0) {
                this.scroll(coordinatorLayout, child, dyUnconsumed, -child.getDownNestedScrollRange(), 0);
                this.stopNestedScrollIfNeeded(dyUnconsumed, child, target, type);
            }

            if (child.isLiftOnScroll()) {
                child.setLiftedState(target.getScrollY() > 0);
            }

        }

        private void stopNestedScrollIfNeeded(int dy, T child, View target, int type) {
            if (type == 1) {
                int curOffset = this.getTopBottomOffsetForScrollingSibling();
                if (dy < 0 && curOffset == 0 || dy > 0 && curOffset == -child.getDownNestedScrollRange()) {
                    ViewCompat.stopNestedScroll(target, 1);
                }
            }

        }

        public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, T abl, View target, int type) {
            if (this.lastStartedType == 0 || type == 1) {
                this.snapToChildIfNeeded(coordinatorLayout, abl);
            }

            this.lastNestedScrollingChildRef = new WeakReference(target);
        }

        public void setDragCallback(@Nullable AppBarLayout.BaseBehavior.BaseDragCallback callback) {
            this.onDragCallback = callback;
        }

        private void animateOffsetTo(CoordinatorLayout coordinatorLayout, T child, int offset, float velocity) {
            int distance = Math.abs(this.getTopBottomOffsetForScrollingSibling() - offset);
            velocity = Math.abs(velocity);
            int duration;
            if (velocity > 0.0F) {
                duration = 3 * Math.round(1000.0F * ((float)distance / velocity));
            } else {
                float distanceRatio = (float)distance / (float)child.getHeight();
                duration = (int)((distanceRatio + 1.0F) * 150.0F);
            }

            this.animateOffsetWithDuration(coordinatorLayout, child, offset, duration);
        }

        private void animateOffsetWithDuration(final CoordinatorLayout coordinatorLayout, final T child, int offset, int duration) {
            int currentOffset = this.getTopBottomOffsetForScrollingSibling();
            if (currentOffset == offset) {
                if (this.offsetAnimator != null && this.offsetAnimator.isRunning()) {
                    this.offsetAnimator.cancel();
                }

            } else {
                if (this.offsetAnimator == null) {
                    this.offsetAnimator = new ValueAnimator();
                    this.offsetAnimator.setInterpolator(AnimationUtils.DECELERATE_INTERPOLATOR);
                    this.offsetAnimator.addUpdateListener(new AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animator) {
                            BaseBehavior.this.setHeaderTopBottomOffset(coordinatorLayout, child, (Integer)animator.getAnimatedValue());
                        }
                    });
                } else {
                    this.offsetAnimator.cancel();
                }

                this.offsetAnimator.setDuration((long)Math.min(duration, 600));
                this.offsetAnimator.setIntValues(new int[]{currentOffset, offset});
                this.offsetAnimator.start();
            }
        }

        private int getChildIndexOnOffset(T abl, int offset) {
            int i = 0;

            for(int count = abl.getChildCount(); i < count; ++i) {
                View child = abl.getChildAt(i);
                int top = child.getTop();
                int bottom = child.getBottom();
                AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams)child.getLayoutParams();
                if (checkFlag(lp.getScrollFlags(), 32)) {
                    top -= lp.topMargin;
                    bottom += lp.bottomMargin;
                }

                if (top <= -offset && bottom >= -offset) {
                    return i;
                }
            }

            return -1;
        }

        private void snapToChildIfNeeded(CoordinatorLayout coordinatorLayout, T abl) {
            int offset = this.getTopBottomOffsetForScrollingSibling();
            int offsetChildIndex = this.getChildIndexOnOffset(abl, offset);
            if (offsetChildIndex >= 0) {
                View offsetChild = abl.getChildAt(offsetChildIndex);
                AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams)offsetChild.getLayoutParams();
                int flags = lp.getScrollFlags();
                if ((flags & 17) == 17) {
                    int snapTop = -offsetChild.getTop();
                    int snapBottom = -offsetChild.getBottom();
                    if (offsetChildIndex == abl.getChildCount() - 1) {
                        snapBottom += abl.getTopInset();
                    }

                    int seam;
                    if (checkFlag(flags, 2)) {
                        snapBottom += ViewCompat.getMinimumHeight(offsetChild);
                    } else if (checkFlag(flags, 5)) {
                        seam = snapBottom + ViewCompat.getMinimumHeight(offsetChild);
                        if (offset < seam) {
                            snapTop = seam;
                        } else {
                            snapBottom = seam;
                        }
                    }

                    if (checkFlag(flags, 32)) {
                        snapTop += lp.topMargin;
                        snapBottom -= lp.bottomMargin;
                    }

                    seam = offset < (snapBottom + snapTop) / 2 ? snapBottom : snapTop;
                    this.animateOffsetTo(coordinatorLayout, abl, MathUtils.clamp(seam, -abl.getTotalScrollRange(), 0), 0.0F);
                }
            }

        }

        private static boolean checkFlag(int flags, int check) {
            return (flags & check) == check;
        }

        public boolean onMeasureChild(CoordinatorLayout parent, T child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
            androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams lp = (androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams)child.getLayoutParams();
            if (lp.height == -2) {
                parent.onMeasureChild(child, parentWidthMeasureSpec, widthUsed, MeasureSpec.makeMeasureSpec(0, 0), heightUsed);
                return true;
            } else {
                return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
            }
        }

        public boolean onLayoutChild(CoordinatorLayout parent, T abl, int layoutDirection) {
            boolean handled = super.onLayoutChild(parent, abl, layoutDirection);
            int pendingAction = abl.getPendingAction();
            int offset;
            if (this.offsetToChildIndexOnLayout >= 0 && (pendingAction & 8) == 0) {
                View child = abl.getChildAt(this.offsetToChildIndexOnLayout);
                offset = -child.getBottom();
                if (this.offsetToChildIndexOnLayoutIsMinHeight) {
                    offset += ViewCompat.getMinimumHeight(child) + abl.getTopInset();
                } else {
                    offset += Math.round((float)child.getHeight() * this.offsetToChildIndexOnLayoutPerc);
                }

                this.setHeaderTopBottomOffset(parent, abl, offset);
            } else if (pendingAction != 0) {
                boolean animate = (pendingAction & 4) != 0;
                if ((pendingAction & 2) != 0) {
                    offset = -abl.getUpNestedPreScrollRange();
                    if (animate) {
                        this.animateOffsetTo(parent, abl, offset, 0.0F);
                    } else {
                        this.setHeaderTopBottomOffset(parent, abl, offset);
                    }
                } else if ((pendingAction & 1) != 0) {
                    if (animate) {
                        this.animateOffsetTo(parent, abl, 0, 0.0F);
                    } else {
                        this.setHeaderTopBottomOffset(parent, abl, 0);
                    }
                }
            }

            abl.resetPendingAction();
            this.offsetToChildIndexOnLayout = -1;
            this.setTopAndBottomOffset(MathUtils.clamp(this.getTopAndBottomOffset(), -abl.getTotalScrollRange(), 0));
            this.updateAppBarLayoutDrawableState(parent, abl, this.getTopAndBottomOffset(), 0, true);
            abl.dispatchOffsetUpdates(this.getTopAndBottomOffset());
            return handled;
        }

        boolean canDragView(T view) {
            if (this.onDragCallback != null) {
                return this.onDragCallback.canDrag(view);
            } else if (this.lastNestedScrollingChildRef == null) {
                return true;
            } else {
                View scrollingView = (View)this.lastNestedScrollingChildRef.get();
                return scrollingView != null && scrollingView.isShown() && !scrollingView.canScrollVertically(-1);
            }
        }

        void onFlingFinished(CoordinatorLayout parent, T layout) {
            this.snapToChildIfNeeded(parent, layout);
        }

        int getMaxDragOffset(T view) {
            return -view.getDownNestedScrollRange();
        }

        int getScrollRangeForDragFling(T view) {
            return view.getTotalScrollRange();
        }

        int setHeaderTopBottomOffset(CoordinatorLayout coordinatorLayout, T appBarLayout, int newOffset, int minOffset, int maxOffset) {
            int curOffset = this.getTopBottomOffsetForScrollingSibling();
            int consumed = 0;
            if (minOffset != 0 && curOffset >= minOffset && curOffset <= maxOffset) {
                newOffset = MathUtils.clamp(newOffset, minOffset, maxOffset);
                if (curOffset != newOffset) {
                    int interpolatedOffset = appBarLayout.hasChildWithInterpolator() ? this.interpolateOffset(appBarLayout, newOffset) : newOffset;
                    boolean offsetChanged = this.setTopAndBottomOffset(interpolatedOffset);
                    consumed = curOffset - newOffset;
                    this.offsetDelta = newOffset - interpolatedOffset;
                    if (!offsetChanged && appBarLayout.hasChildWithInterpolator()) {
                        coordinatorLayout.dispatchDependentViewsChanged(appBarLayout);
                    }

                    appBarLayout.dispatchOffsetUpdates(this.getTopAndBottomOffset());
                    this.updateAppBarLayoutDrawableState(coordinatorLayout, appBarLayout, newOffset, newOffset < curOffset ? -1 : 1, false);
                }
            } else {
                this.offsetDelta = 0;
            }

            return consumed;
        }

        @VisibleForTesting
        boolean isOffsetAnimatorRunning() {
            return this.offsetAnimator != null && this.offsetAnimator.isRunning();
        }

        private int interpolateOffset(T layout, int offset) {
            int absOffset = Math.abs(offset);
            int i = 0;

            for(int z = layout.getChildCount(); i < z; ++i) {
                View child = layout.getChildAt(i);
                AppBarLayout.LayoutParams childLp = (AppBarLayout.LayoutParams)child.getLayoutParams();
                Interpolator interpolator = childLp.getScrollInterpolator();
                if (absOffset >= child.getTop() && absOffset <= child.getBottom()) {
                    if (interpolator != null) {
                        int childScrollableHeight = 0;
                        int flags = childLp.getScrollFlags();
                        if ((flags & 1) != 0) {
                            childScrollableHeight += child.getHeight() + childLp.topMargin + childLp.bottomMargin;
                            if ((flags & 2) != 0) {
                                childScrollableHeight -= ViewCompat.getMinimumHeight(child);
                            }
                        }

                        if (ViewCompat.getFitsSystemWindows(child)) {
                            childScrollableHeight -= layout.getTopInset();
                        }

                        if (childScrollableHeight > 0) {
                            int offsetForView = absOffset - child.getTop();
                            int interpolatedDiff = Math.round((float)childScrollableHeight * interpolator.getInterpolation((float)offsetForView / (float)childScrollableHeight));
                            return Integer.signum(offset) * (child.getTop() + interpolatedDiff);
                        }
                    }
                    break;
                }
            }

            return offset;
        }

        private void updateAppBarLayoutDrawableState(CoordinatorLayout parent, T layout, int offset, int direction, boolean forceJump) {
            View child = getAppBarChildOnOffset(layout, offset);
            if (child != null) {
                AppBarLayout.LayoutParams childLp = (AppBarLayout.LayoutParams)child.getLayoutParams();
                int flags = childLp.getScrollFlags();
                boolean lifted = false;
                if ((flags & 1) != 0) {
                    int minHeight = ViewCompat.getMinimumHeight(child);
                    if (direction > 0 && (flags & 12) != 0) {
                        lifted = -offset >= child.getBottom() - minHeight - layout.getTopInset();
                    } else if ((flags & 2) != 0) {
                        lifted = -offset >= child.getBottom() - minHeight - layout.getTopInset();
                    }
                }

                if (layout.isLiftOnScroll()) {
                    View scrollingChild = this.findFirstScrollingChild(parent);
                    if (scrollingChild != null) {
                        lifted = scrollingChild.getScrollY() > 0;
                    }
                }

                boolean changed = layout.setLiftedState(lifted);
                if (VERSION.SDK_INT >= 11 && (forceJump || changed && this.shouldJumpElevationState(parent, layout))) {
                    layout.jumpDrawablesToCurrentState();
                }
            }

        }

        private boolean shouldJumpElevationState(CoordinatorLayout parent, T layout) {
            List<View> dependencies = parent.getDependents(layout);
            int i = 0;

            for(int size = dependencies.size(); i < size; ++i) {
                View dependency = (View)dependencies.get(i);
                androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams lp = (androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams)dependency.getLayoutParams();
                androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior behavior = lp.getBehavior();
                if (behavior instanceof AppBarLayout.ScrollingViewBehavior) {
                    return ((AppBarLayout.ScrollingViewBehavior)behavior).getOverlayTop() != 0;
                }
            }

            return false;
        }

        private static View getAppBarChildOnOffset(AppBarLayout layout, int offset) {
            int absOffset = Math.abs(offset);
            int i = 0;

            for(int z = layout.getChildCount(); i < z; ++i) {
                View child = layout.getChildAt(i);
                if (absOffset >= child.getTop() && absOffset <= child.getBottom()) {
                    return child;
                }
            }

            return null;
        }

        @Nullable
        private View findFirstScrollingChild(CoordinatorLayout parent) {
            int i = 0;

            for(int z = parent.getChildCount(); i < z; ++i) {
                View child = parent.getChildAt(i);
                if (child instanceof NestedScrollingChild) {
                    return child;
                }
            }

            return null;
        }

        int getTopBottomOffsetForScrollingSibling() {
            return this.getTopAndBottomOffset() + this.offsetDelta;
        }

        public Parcelable onSaveInstanceState(CoordinatorLayout parent, T abl) {
            Parcelable superState = super.onSaveInstanceState(parent, abl);
            int offset = this.getTopAndBottomOffset();
            int i = 0;

            for(int count = abl.getChildCount(); i < count; ++i) {
                View child = abl.getChildAt(i);
                int visBottom = child.getBottom() + offset;
                if (child.getTop() + offset <= 0 && visBottom >= 0) {
                    AppBarLayout.BaseBehavior.SavedState ss = new AppBarLayout.BaseBehavior.SavedState(superState);
                    ss.firstVisibleChildIndex = i;
                    ss.firstVisibleChildAtMinimumHeight = visBottom == ViewCompat.getMinimumHeight(child) + abl.getTopInset();
                    ss.firstVisibleChildPercentageShown = (float)visBottom / (float)child.getHeight();
                    return ss;
                }
            }

            return superState;
        }

        public void onRestoreInstanceState(CoordinatorLayout parent, T appBarLayout, Parcelable state) {
            if (state instanceof AppBarLayout.BaseBehavior.SavedState) {
                AppBarLayout.BaseBehavior.SavedState ss = (AppBarLayout.BaseBehavior.SavedState)state;
                super.onRestoreInstanceState(parent, appBarLayout, ss.getSuperState());
                this.offsetToChildIndexOnLayout = ss.firstVisibleChildIndex;
                this.offsetToChildIndexOnLayoutPerc = ss.firstVisibleChildPercentageShown;
                this.offsetToChildIndexOnLayoutIsMinHeight = ss.firstVisibleChildAtMinimumHeight;
            } else {
                super.onRestoreInstanceState(parent, appBarLayout, state);
                this.offsetToChildIndexOnLayout = -1;
            }

        }

        protected static class SavedState extends AbsSavedState {
            int firstVisibleChildIndex;
            float firstVisibleChildPercentageShown;
            boolean firstVisibleChildAtMinimumHeight;
            public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
                public AppBarLayout.BaseBehavior.SavedState createFromParcel(Parcel source, ClassLoader loader) {
                    return new AppBarLayout.BaseBehavior.SavedState(source, loader);
                }

                public AppBarLayout.BaseBehavior.SavedState createFromParcel(Parcel source) {
                    return new AppBarLayout.BaseBehavior.SavedState(source, (ClassLoader)null);
                }

                public AppBarLayout.BaseBehavior.SavedState[] newArray(int size) {
                    return new AppBarLayout.BaseBehavior.SavedState[size];
                }
            };

            public SavedState(Parcel source, ClassLoader loader) {
                super(source, loader);
                this.firstVisibleChildIndex = source.readInt();
                this.firstVisibleChildPercentageShown = source.readFloat();
                this.firstVisibleChildAtMinimumHeight = source.readByte() != 0;
            }

            public SavedState(Parcelable superState) {
                super(superState);
            }

            public void writeToParcel(Parcel dest, int flags) {
                super.writeToParcel(dest, flags);
                dest.writeInt(this.firstVisibleChildIndex);
                dest.writeFloat(this.firstVisibleChildPercentageShown);
                dest.writeByte((byte)(this.firstVisibleChildAtMinimumHeight ? 1 : 0));
            }
        }

        public abstract static class BaseDragCallback<T extends AppBarLayout> {
            public BaseDragCallback() {
            }

            public abstract boolean canDrag(@NonNull T var1);
        }
    }

    public static class Behavior extends AppBarLayout.BaseBehavior<AppBarLayout> {
        public Behavior() {
        }

        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public abstract static class DragCallback extends AppBarLayout.BaseBehavior.BaseDragCallback<AppBarLayout> {
            public DragCallback() {
            }
        }
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {
        public static final int SCROLL_FLAG_SCROLL = 1;
        public static final int SCROLL_FLAG_EXIT_UNTIL_COLLAPSED = 2;
        public static final int SCROLL_FLAG_ENTER_ALWAYS = 4;
        public static final int SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED = 8;
        public static final int SCROLL_FLAG_SNAP = 16;
        public static final int SCROLL_FLAG_SNAP_MARGINS = 32;
        static final int FLAG_QUICK_RETURN = 5;
        static final int FLAG_SNAP = 17;
        static final int COLLAPSIBLE_FLAGS = 10;
        int scrollFlags = 1;
        Interpolator scrollInterpolator;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, styleable.AppBarLayout_Layout);
            this.scrollFlags = a.getInt(styleable.AppBarLayout_Layout_layout_scrollFlags, 0);
            if (a.hasValue(styleable.AppBarLayout_Layout_layout_scrollInterpolator)) {
                int resId = a.getResourceId(styleable.AppBarLayout_Layout_layout_scrollInterpolator, 0);
                this.scrollInterpolator = android.view.animation.AnimationUtils.loadInterpolator(c, resId);
            }

            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height, weight);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        @RequiresApi(19)
        public LayoutParams(LinearLayout.LayoutParams source) {
            super(source);
        }

        @RequiresApi(19)
        public LayoutParams(AppBarLayout.LayoutParams source) {
            super(source);
            this.scrollFlags = source.scrollFlags;
            this.scrollInterpolator = source.scrollInterpolator;
        }

        public void setScrollFlags(int flags) {
            this.scrollFlags = flags;
        }

        public int getScrollFlags() {
            return this.scrollFlags;
        }

        public void setScrollInterpolator(Interpolator interpolator) {
            this.scrollInterpolator = interpolator;
        }

        public Interpolator getScrollInterpolator() {
            return this.scrollInterpolator;
        }

        boolean isCollapsible() {
            return (this.scrollFlags & 1) == 1 && (this.scrollFlags & 10) != 0;
        }

        @Retention(RetentionPolicy.SOURCE)
        @RestrictTo({Scope.LIBRARY_GROUP})
        public @interface ScrollFlags {
        }
    }

    public interface OnOffsetChangedListener extends AppBarLayout.BaseOnOffsetChangedListener<AppBarLayout> {
        void onOffsetChanged(AppBarLayout var1, int var2);
    }

    public interface BaseOnOffsetChangedListener<T extends AppBarLayout> {
        void onOffsetChanged(T var1, int var2);
    }
}
