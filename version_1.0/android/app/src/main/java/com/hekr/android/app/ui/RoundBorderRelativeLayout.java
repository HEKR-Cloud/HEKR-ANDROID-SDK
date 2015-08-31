package com.hekr.android.app.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by xubukan on 2015/3/20.
 */
public class RoundBorderRelativeLayout extends RelativeLayout{

    private Paint borderPaint;
    private int mBorderColor = 0xFFAAAAAA;
    public RoundBorderRelativeLayout(Context context,AttributeSet attrs){
        super(context,attrs);
        init();
    }

    public RoundBorderRelativeLayout(Context context){
        super(context);
        init();
    }

    private void init(){
        borderPaint = new Paint();
        borderPaint.setColor(mBorderColor);
    }

    public void setBorderColor(int color){
        mBorderColor = color;
        borderPaint.setColor(mBorderColor);
        invalidate();
    }



    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        // draw border
        int vw = getWidth();

        borderPaint.setAntiAlias(true);
        borderPaint.setStrokeWidth(2.0f);
        borderPaint.setStyle(Paint.Style.STROKE);

        canvas.drawCircle(vw/2,vw/2+2,vw/2-4,borderPaint);
    }
}
