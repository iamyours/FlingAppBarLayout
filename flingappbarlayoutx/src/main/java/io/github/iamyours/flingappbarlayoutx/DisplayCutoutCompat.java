package io.github.iamyours.flingappbarlayoutx;

import android.graphics.Rect;
import android.os.Build.VERSION;
import android.view.DisplayCutout;

import java.util.List;

public final class DisplayCutoutCompat {
    private final Object mDisplayCutout;

    public DisplayCutoutCompat(Rect safeInsets, List<Rect> boundingRects) {
        this(VERSION.SDK_INT >= 28 ? new DisplayCutout(safeInsets, boundingRects) : null);
    }

    private DisplayCutoutCompat(Object displayCutout) {
        this.mDisplayCutout = displayCutout;
    }

    public int getSafeInsetTop() {
        return VERSION.SDK_INT >= 28 ? ((DisplayCutout)this.mDisplayCutout).getSafeInsetTop() : 0;
    }

    public int getSafeInsetBottom() {
        return VERSION.SDK_INT >= 28 ? ((DisplayCutout)this.mDisplayCutout).getSafeInsetBottom() : 0;
    }

    public int getSafeInsetLeft() {
        return VERSION.SDK_INT >= 28 ? ((DisplayCutout)this.mDisplayCutout).getSafeInsetLeft() : 0;
    }

    public int getSafeInsetRight() {
        return VERSION.SDK_INT >= 28 ? ((DisplayCutout)this.mDisplayCutout).getSafeInsetRight() : 0;
    }

    public List<Rect> getBoundingRects() {
        return VERSION.SDK_INT >= 28 ? ((DisplayCutout)this.mDisplayCutout).getBoundingRects() : null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            DisplayCutoutCompat other = (DisplayCutoutCompat)o;
            return this.mDisplayCutout == null ? other.mDisplayCutout == null : this.mDisplayCutout.equals(other.mDisplayCutout);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.mDisplayCutout == null ? 0 : this.mDisplayCutout.hashCode();
    }

    public String toString() {
        return "DisplayCutoutCompat{" + this.mDisplayCutout + "}";
    }

    static DisplayCutoutCompat wrap(Object displayCutout) {
        return displayCutout == null ? null : new DisplayCutoutCompat(displayCutout);
    }
}
