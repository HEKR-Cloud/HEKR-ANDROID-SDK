package com.hekr.android.app.util;

import android.view.View;
import static com.hekr.android.app.util.AnimatorProxy.wrap;

public final class ViewHelper {
    private ViewHelper() {}

    public static float getAlpha(View view) {
        return wrap(view).getAlpha();
    }

    public static void setAlpha(View view, float alpha) {
        wrap(view).setAlpha(alpha);
    }

    public static float getPivotX(View view) {
        return wrap(view).getPivotX();
    }

    public static void setPivotX(View view, float pivotX) {
        wrap(view).setPivotX(pivotX);
    }

    public static float getPivotY(View view) {
        return wrap(view).getPivotY();
    }

    public static void setPivotY(View view, float pivotY) {
        wrap(view).setPivotY(pivotY);
    }

    public static float getRotation(View view) {
        return wrap(view).getRotation();
    }

    public static void setRotation(View view, float rotation) {
        wrap(view).setRotation(rotation);
    }

    public static float getRotationX(View view) {
        return wrap(view).getRotationX();
    }

    public static void setRotationX(View view, float rotationX) {
        wrap(view).setRotationX(rotationX);
    }

    public static float getRotationY(View view) {
        return wrap(view).getRotationY();
    }

    public static void setRotationY(View view, float rotationY) {
        wrap(view).setRotationY(rotationY);
    }

    public static float getScaleX(View view) {
        return  wrap(view).getScaleX();
    }

    public static void setScaleX(View view, float scaleX) {
            wrap(view).setScaleX(scaleX);
    }

    public static float getScaleY(View view) {
        return wrap(view).getScaleY();
    }

    public static void setScaleY(View view, float scaleY) {
        wrap(view).setScaleY(scaleY);
    }

    public static float getScrollX(View view) {
        return wrap(view).getScrollX();
    }

    public static void setScrollX(View view, int scrollX) {
        wrap(view).setScrollX(scrollX);
    }

    public static float getScrollY(View view) {
        return wrap(view).getScrollY();
    }

    public static void setScrollY(View view, int scrollY) {
        wrap(view).setScrollY(scrollY);
    }

    public static float getTranslationX(View view) {
        return wrap(view).getTranslationX();
    }

    public static void setTranslationX(View view, float translationX) {
        wrap(view).setTranslationX(translationX);
    }

    public static float getTranslationY(View view) {
        return wrap(view).getTranslationY();
    }

    public static void setTranslationY(View view, float translationY) {
            wrap(view).setTranslationY(translationY);
    }

    public static float getX(View view) {
        return wrap(view).getX();
    }

    public static void setX(View view, float x) {
        wrap(view).setX(x);
    }

    public static float getY(View view) {
        return wrap(view).getY();
    }

    public static void setY(View view, float y) {
        wrap(view).setY(y);
    }
}