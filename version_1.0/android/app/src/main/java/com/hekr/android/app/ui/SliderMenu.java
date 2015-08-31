package com.hekr.android.app.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import com.hekr.android.app.R;
import com.hekr.android.app.util.ScreenUtils;
import com.hekr.android.app.util.ViewHelper;
import android.util.Log;

import java.util.jar.Attributes;

/**
 * Created by xubukan on 2015/3/15.
 */
public class SliderMenu extends HorizontalScrollView{

    /**
     * 屏幕宽度
     */
    private int mScreenWidth;
    /**
     * dp
     */
    private int mMenuRightPadding;
    /**
     * 菜单的宽度
     */
    private int mMenuWidth;
    private int mHalfMenuWidth;

    private boolean isOpen;

    private boolean once;

    private ViewGroup mMenu;
    private ViewGroup mContent;

    private int xSliderDown;
    private int ySliderDown;

    private int xSliderMove;
    private int ySliderMove;

    public SliderMenu(Context context,AttributeSet attrs){
        this(context,attrs,0);
    }

    public SliderMenu(Context context,AttributeSet attrs,int defStyle){
        super(context, attrs, defStyle);
        mScreenWidth = ScreenUtils.getScreenWidth(context);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.SlidingMenu, defStyle, 0);

        int n = a.getIndexCount();
        for (int i = 0; i < n; i++)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.SlidingMenu_leftPadding:
                    // 默认50
                    mMenuRightPadding = a.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP, 50f,
                                    getResources().getDisplayMetrics()));// 默认为10DP
                    break;
            }
        }
        a.recycle();
    }

    public SliderMenu(Context context){
        this(context, null, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        /**
         * 显示的设置一个宽度
         */
        if (!once)
        {
            LinearLayout wrapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) wrapper.getChildAt(0);
            mContent = (ViewGroup) wrapper.getChildAt(1);

            mMenuWidth = mMenuRightPadding;
            mHalfMenuWidth = mMenuWidth / 2;
            mMenu.getLayoutParams().width = mMenuWidth;
            mContent.getLayoutParams().width = mScreenWidth;

        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }
    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        int action = ev.getAction();
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        //Log.i("TestX","测试x坐标："+x);
        //Log.i("TestX","测试y坐标："+y);
        switch (action)
        {
            // Up时，进行判断，如果显示区域大于菜单宽度一半则完全显示，否则隐藏
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                Log.i("TestX","测试scroll坐标："+scrollX);
                if (scrollX > mHalfMenuWidth) {
                    this.smoothScrollTo(mMenuWidth, 0);
                    isOpen = false;
                } else {
                    this.smoothScrollTo(0, 0);
                    isOpen = true;
                }
                return true;
                //return false;
        }
        return super.onTouchEvent(ev);
        //return true;
        //return false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);
        if (changed)
        {
            // 将菜单隐藏
            this.scrollTo(mMenuWidth, 0);
            once = true;
        }
    }

    /**
     * 打开菜单
     */
    public void openMenu()
    {
        if (isOpen)
            return;
        this.smoothScrollTo(0, 0);
        isOpen = true;
    }
    public boolean isOpen(){
        if(isOpen){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu()
    {
        if (isOpen)
        {
            this.smoothScrollTo(mMenuWidth, 0);
            isOpen = false;
        }
    }

    /**
     * 切换菜单状态
     */
    public void toggle()
    {
        if (isOpen)
        {
            closeMenu();
        } else
        {
            openMenu();
        }
    }
//    protected void onScrollChanged(int l, int t, int oldl, int oldt)
//    {
//        super.onScrollChanged(l, t, oldl, oldt);
//        float scale = l * 1.0f / mMenuWidth;
//        float leftScale = 1 - 0.3f * scale;
//        float rightScale = 0.8f + scale * 0.2f;
//
//        ViewHelper.setScaleX(mMenu, leftScale);
//        ViewHelper.setScaleY(mMenu, leftScale);
//        ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
//        ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.7f);
//
//        ViewHelper.setPivotX(mContent, 0);
//        ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
//        ViewHelper.setScaleX(mContent, rightScale);
//        ViewHelper.setScaleY(mContent, rightScale);
//
//    }
}
