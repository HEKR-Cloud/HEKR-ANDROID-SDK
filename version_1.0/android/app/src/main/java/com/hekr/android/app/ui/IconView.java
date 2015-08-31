package com.hekr.android.app.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by xubukan on 2015/3/16.
 */
public class IconView extends TextView {
    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        InitFont(context);
    }

    public IconView(Context context) {
        super(context);
        InitFont(context);
    }

    public IconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        InitFont(context);
    }

    private void InitFont(Context context){
        Typeface iconfont = Typeface.createFromAsset(context.getAssets(), "fontawesome.ttf");
        setTypeface(iconfont);
    }
}
