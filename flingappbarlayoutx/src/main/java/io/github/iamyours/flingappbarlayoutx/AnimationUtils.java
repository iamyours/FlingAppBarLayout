package io.github.iamyours.flingappbarlayoutx;

import android.animation.TimeInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

public class AnimationUtils {
    public static final TimeInterpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
    public static final TimeInterpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new FastOutSlowInInterpolator();
    public static final TimeInterpolator FAST_OUT_LINEAR_IN_INTERPOLATOR = new FastOutLinearInInterpolator();
    public static final TimeInterpolator LINEAR_OUT_SLOW_IN_INTERPOLATOR = new LinearOutSlowInInterpolator();
    public static final TimeInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();

    public AnimationUtils() {
    }

    public static float lerp(float startValue, float endValue, float fraction) {
        return startValue + fraction * (endValue - startValue);
    }

    public static int lerp(int startValue, int endValue, float fraction) {
        return startValue + Math.round(fraction * (float)(endValue - startValue));
    }
}