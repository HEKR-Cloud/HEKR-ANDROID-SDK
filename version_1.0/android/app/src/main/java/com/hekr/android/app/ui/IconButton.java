package com.hekr.android.app.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by xubukan on 2015/3/16.
 */
public class IconButton extends Button {
    public IconButton(Context context) {
        super(context);
        InitFont(context);
    }

    public IconButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        InitFont(context);
    }

    public IconButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        InitFont(context);
    }

    private void InitFont(Context context){
        Typeface iconfont = Typeface.createFromAsset(context.getAssets(), "fontawesome.ttf");
        setTypeface(iconfont);
    }
}
