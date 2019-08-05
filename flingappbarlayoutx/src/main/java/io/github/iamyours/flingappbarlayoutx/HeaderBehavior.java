package io.github.iamyours.flingappbarlayoutx;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.math.MathUtils;
import androidx.core.view.ViewCompat;

abstract class HeaderBehavior<V extends View> extends ViewOffsetBehavior<V> {
    private static final int INVALID_POINTER = -1;
    private Runnable mFlingRunnable;
    OverScroller mScroller;
    private boolean mIsBeingDragged;
    private int mActivePointerId = -1;
    private int mLastMotionY;
    private int mTouchSlop = -1;
    private VelocityTracker mVelocityTracker;

    public HeaderBehavior() {
    }

    public HeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent ev) {
        if (this.mTouchSlop < 0) {
            this.mTouchSlop = ViewConfiguration.get(parent.getContext()).getScaledTouchSlop();
        }

        int action = ev.getAction();
        if (action == 2 && this.mIsBeingDragged) {
            return true;
        } else {
            int activePointerId;
            int pointerIndex;
            switch (ev.getActionMasked()) {
                case 0:
                    this.mIsBeingDragged = false;
                    activePointerId = (int) ev.getX();
                    pointerIndex = (int) ev.getY();
                    if (this.canDragView(child) && parent.isPointInChildBounds(child, activePointerId, pointerIndex)) {
                        this.mLastMotionY = pointerIndex;
                        this.mActivePointerId = ev.getPointerId(0);
                        this.ensureVelocityTracker();
                    }
                    break;
                case 1:
                case 3:
                    this.mIsBeingDragged = false;
                    this.mActivePointerId = -1;
                    if (this.mVelocityTracker != null) {
                        this.mVelocityTracker.recycle();
                        this.mVelocityTracker = null;
                    }
                    break;
                case 2:
                    activePointerId = this.mActivePointerId;
                    if (activePointerId != -1) {
                        pointerIndex = ev.findPointerIndex(activePointerId);
                        if (pointerIndex != -1) {
                            int y = (int) ev.getY(pointerIndex);
                            int yDiff = Math.abs(y - this.mLastMotionY);
                            if (yDiff > this.mTouchSlop) {
                                this.mIsBeingDragged = true;
                                this.mLastMotionY = y;
                            }
                        }
                    }
            }

            if (this.mVelocityTracker != null) {
                this.mVelocityTracker.addMovement(ev);
            }

            return this.mIsBeingDragged;
        }
    }

    public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent ev) {
        if (this.mTouchSlop < 0) {
            this.mTouchSlop = ViewConfiguration.get(parent.getContext()).getScaledTouchSlop();
        }

        int activePointerIndex;
        int y;
        switch (ev.getActionMasked()) {
            case 0:
                activePointerIndex = (int) ev.getX();
                y = (int) ev.getY();
                if (!parent.isPointInChildBounds(child, activePointerIndex, y) || !this.canDragView(child)) {
                    return false;
                }

                this.mLastMotionY = y;
                this.mActivePointerId = ev.getPointerId(0);
                this.ensureVelocityTracker();
                break;
            case 1:
                if (this.mVelocityTracker != null) {
                    this.mVelocityTracker.addMovement(ev);
                    this.mVelocityTracker.computeCurrentVelocity(1000);
                    float yvel = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
                    this.fling(parent, child, -this.getScrollRangeForDragFling(child), 0, yvel);
                }
            case 3:
                this.mIsBeingDragged = false;
                this.mActivePointerId = -1;
                if (this.mVelocityTracker != null) {
                    this.mVelocityTracker.recycle();
                    this.mVelocityTracker = null;
                }
                break;
            case 2:
                activePointerIndex = ev.findPointerIndex(this.mActivePointerId);
                if (activePointerIndex == -1) {
                    return false;
                }

                y = (int) ev.getY(activePointerIndex);
                int dy = this.mLastMotionY - y;
                if (!this.mIsBeingDragged && Math.abs(dy) > this.mTouchSlop) {
                    this.mIsBeingDragged = true;
                    if (dy > 0) {
                        dy -= this.mTouchSlop;
                    } else {
                        dy += this.mTouchSlop;
                    }
                }

                if (this.mIsBeingDragged) {
                    this.mLastMotionY = y;
                    this.scroll(parent, child, dy, this.getMaxDragOffset(child), 0);
                }
        }

        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.addMovement(ev);
        }

        return true;
    }

    int setHeaderTopBottomOffset(CoordinatorLayout parent, V header, int newOffset) {
        return this.setHeaderTopBottomOffset(parent, header, newOffset, -2147483648, 2147483647);
    }

    int setHeaderTopBottomOffset(CoordinatorLayout parent, V header, int newOffset, int minOffset, int maxOffset) {
        int curOffset = this.getTopAndBottomOffset();
        int consumed = 0;
        if (minOffset != 0 && curOffset >= minOffset && curOffset <= maxOffset) {
            newOffset = MathUtils.clamp(newOffset, minOffset, maxOffset);
            if (curOffset != newOffset) {
                this.setTopAndBottomOffset(newOffset);
                consumed = curOffset - newOffset;
            }
        }

        return consumed;
    }

    int getTopBottomOffsetForScrollingSibling() {
        return this.getTopAndBottomOffset();
    }

    final int scroll(CoordinatorLayout coordinatorLayout, V header, int dy, int minOffset, int maxOffset) {
        return this.setHeaderTopBottomOffset(coordinatorLayout, header, this.getTopBottomOffsetForScrollingSibling() - dy, minOffset, maxOffset);
    }

    final boolean fling(CoordinatorLayout coordinatorLayout, V layout, int minOffset, int maxOffset, float velocityY) {
        if (this.mFlingRunnable != null) {
            layout.removeCallbacks(this.mFlingRunnable);
            this.mFlingRunnable = null;
        }

        if (this.mScroller == null) {
            this.mScroller = new OverScroller(layout.getContext());
        }
        int fixedMin = velocityY < 0 ? minOffset - 5000 : minOffset;

        this.mScroller.fling(0, this.getTopAndBottomOffset(), 0, Math.round(velocityY), 0, 0, fixedMin, maxOffset);
        if (this.mScroller.computeScrollOffset()) {
            this.mFlingRunnable = new HeaderBehavior.FlingRunnable(coordinatorLayout, layout, minOffset);
            ViewCompat.postOnAnimation(layout, this.mFlingRunnable);
            return true;
        } else {
            this.onFlingFinished(coordinatorLayout, layout);
            return false;
        }
    }

    void onFlingFinished(CoordinatorLayout parent, V layout) {
    }

    boolean canDragView(V view) {
        return false;
    }

    int getMaxDragOffset(V view) {
        return -view.getHeight();
    }

    int getScrollRangeForDragFling(V view) {
        return view.getHeight();
    }

    private void ensureVelocityTracker() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }

    }

    private class FlingRunnable implements Runnable {
        private final CoordinatorLayout mParent;
        private final V mLayout;
        private final int minOffset;
        private ScrollItem scrollItem;

        FlingRunnable(CoordinatorLayout parent, V layout, int min) {
            this.mParent = parent;
            this.mLayout = layout;
            this.minOffset = min;
            initNextScrollView(parent);
        }

        private void initNextScrollView(CoordinatorLayout parent) {
            int count = parent.getChildCount();
            for (int i = 0; i < count; i++) {
                View v = parent.getChildAt(i);
                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) v.getLayoutParams();
                if (lp.getBehavior() instanceof AppBarLayout.ScrollingViewBehavior) {
                    scrollItem = new ScrollItem(v);
                }
            }
        }

        public void run() {
            if (this.mLayout != null && HeaderBehavior.this.mScroller != null) {
                if (HeaderBehavior.this.mScroller.computeScrollOffset()) {
                    int currY = mScroller.getCurrY();
                    if (currY < 0 && currY < minOffset) {
                        scrollNext(minOffset - currY);
                        setHeaderTopBottomOffset(mParent, mLayout, minOffset);
                    } else {
                        setHeaderTopBottomOffset(mParent, mLayout, currY);
                    }
                    ViewCompat.postOnAnimation(this.mLayout, this);
                } else {
                    HeaderBehavior.this.onFlingFinished(this.mParent, this.mLayout);
                }
            }

        }

        private void scrollNext(int dy) {
            scrollItem.scroll(dy);
        }
    }
}