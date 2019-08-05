package io.github.iamyours.flingappbarlayoutx;

import android.view.View;

import androidx.core.view.ViewCompat;

class ViewOffsetHelper {
    private final View view;
    private int layoutTop;
    private int layoutLeft;
    private int offsetTop;
    private int offsetLeft;

    public ViewOffsetHelper(View view) {
        this.view = view;
    }

    public void onViewLayout() {
        this.layoutTop = this.view.getTop();
        this.layoutLeft = this.view.getLeft();
        this.updateOffsets();
    }

    private void updateOffsets() {
        ViewCompat.offsetTopAndBottom(this.view, this.offsetTop - (this.view.getTop() - this.layoutTop));
        ViewCompat.offsetLeftAndRight(this.view, this.offsetLeft - (this.view.getLeft() - this.layoutLeft));
    }

    public boolean setTopAndBottomOffset(int offset) {
        if (this.offsetTop != offset) {
            this.offsetTop = offset;
            this.updateOffsets();
            return true;
        } else {
            return false;
        }
    }

    public boolean setLeftAndRightOffset(int offset) {
        if (this.offsetLeft != offset) {
            this.offsetLeft = offset;
            this.updateOffsets();
            return true;
        } else {
            return false;
        }
    }

    public int getTopAndBottomOffset() {
        return this.offsetTop;
    }

    public int getLeftAndRightOffset() {
        return this.offsetLeft;
    }

    public int getLayoutTop() {
        return this.layoutTop;
    }

    public int getLayoutLeft() {
        return this.layoutLeft;
    }
}
