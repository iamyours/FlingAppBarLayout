package io.github.iamyours.flingappbarlayoutx;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.AttrRes;
import androidx.annotation.StyleRes;
import androidx.annotation.StyleableRes;

import com.google.android.material.R.attr;
import com.google.android.material.R.styleable;

public final class ThemeEnforcement {
    private static final int[] APPCOMPAT_CHECK_ATTRS;
    private static final String APPCOMPAT_THEME_NAME = "Theme.AppCompat";
    private static final int[] MATERIAL_CHECK_ATTRS;
    private static final String MATERIAL_THEME_NAME = "Theme.MaterialComponents";

    private ThemeEnforcement() {
    }

    public static TypedArray obtainStyledAttributes(Context context, AttributeSet set, @StyleableRes int[] attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes, @StyleableRes int... textAppearanceResIndices) {
        checkCompatibleTheme(context, set, defStyleAttr, defStyleRes);
        checkTextAppearance(context, set, attrs, defStyleAttr, defStyleRes, textAppearanceResIndices);
        return context.obtainStyledAttributes(set, attrs, defStyleAttr, defStyleRes);
    }


    private static void checkCompatibleTheme(Context context, AttributeSet set, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(set, styleable.ThemeEnforcement, defStyleAttr, defStyleRes);
        boolean enforceMaterialTheme = a.getBoolean(styleable.ThemeEnforcement_enforceMaterialTheme, false);
        a.recycle();
        if (enforceMaterialTheme) {
            checkMaterialTheme(context);
        }

        checkAppCompatTheme(context);
    }

    private static void checkTextAppearance(Context context, AttributeSet set, @StyleableRes int[] attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes, @StyleableRes int... textAppearanceResIndices) {
        TypedArray themeEnforcementAttrs = context.obtainStyledAttributes(set, styleable.ThemeEnforcement, defStyleAttr, defStyleRes);
        boolean enforceTextAppearance = themeEnforcementAttrs.getBoolean(styleable.ThemeEnforcement_enforceTextAppearance, false);
        if (!enforceTextAppearance) {
            themeEnforcementAttrs.recycle();
        } else {
            boolean validTextAppearance;
            if (textAppearanceResIndices != null && textAppearanceResIndices.length != 0) {
                validTextAppearance = isCustomTextAppearanceValid(context, set, attrs, defStyleAttr, defStyleRes, textAppearanceResIndices);
            } else {
                validTextAppearance = themeEnforcementAttrs.getResourceId(styleable.ThemeEnforcement_android_textAppearance, -1) != -1;
            }

            themeEnforcementAttrs.recycle();
            if (!validTextAppearance) {
                throw new IllegalArgumentException("This component requires that you specify a valid TextAppearance attribute. Update your app theme to inherit from Theme.MaterialComponents (or a descendant).");
            }
        }
    }

    private static boolean isCustomTextAppearanceValid(Context context, AttributeSet set, @StyleableRes int[] attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes, @StyleableRes int... textAppearanceResIndices) {
        TypedArray componentAttrs = context.obtainStyledAttributes(set, attrs, defStyleAttr, defStyleRes);
        int[] var7 = textAppearanceResIndices;
        int var8 = textAppearanceResIndices.length;

        for(int var9 = 0; var9 < var8; ++var9) {
            int customTextAppearanceIndex = var7[var9];
            if (componentAttrs.getResourceId(customTextAppearanceIndex, -1) == -1) {
                componentAttrs.recycle();
                return false;
            }
        }

        componentAttrs.recycle();
        return true;
    }

    public static void checkAppCompatTheme(Context context) {
        checkTheme(context, APPCOMPAT_CHECK_ATTRS, "Theme.AppCompat");
    }

    public static void checkMaterialTheme(Context context) {
        checkTheme(context, MATERIAL_CHECK_ATTRS, "Theme.MaterialComponents");
    }

    public static boolean isAppCompatTheme(Context context) {
        return isTheme(context, APPCOMPAT_CHECK_ATTRS);
    }

    public static boolean isMaterialTheme(Context context) {
        return isTheme(context, MATERIAL_CHECK_ATTRS);
    }

    private static boolean isTheme(Context context, int[] themeAttributes) {
        TypedArray a = context.obtainStyledAttributes(themeAttributes);
        boolean success = a.hasValue(0);
        a.recycle();
        return success;
    }

    private static void checkTheme(Context context, int[] themeAttributes, String themeName) {
        if (!isTheme(context, themeAttributes)) {
            throw new IllegalArgumentException("The style on this component requires your app theme to be " + themeName + " (or a descendant).");
        }
    }

    static {
        APPCOMPAT_CHECK_ATTRS = new int[]{attr.colorPrimary};
        MATERIAL_CHECK_ATTRS = new int[]{attr.colorSecondary};
    }
}