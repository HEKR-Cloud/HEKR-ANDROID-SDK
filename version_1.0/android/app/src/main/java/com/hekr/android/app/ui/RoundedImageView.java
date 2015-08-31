package com.hekr.android.app.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by xubukan on 2015/3/20.
 */
public class RoundedImageView extends ImageView {
    private Context mContext;
    /* 边框像素 */
    private int mBorderThickness = 1;
    /* 外边框颜色 */
    private int mBorderOutsideColor =  Color.BLACK;
    /* 内边框颜色 */
    private int mBorderInsideColor = 0;
    /* 默认框颜色 */
    private int mDefValueColor = 0;

    private int defaultWidth = 0;
    private int defaultHeight = 0;
    /**
     * 构造方法
     * @param context
     */
    public RoundedImageView(Context context) {
        super(context);
        this.mContext = context;
    }

    /**
     * 构造方法
     * @param context
     * @param attrs
     */
    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    /**
     * 构造方法
     * @param context
     * @param attrs
     */
    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
                /* 获取ImageView图片*/
        Drawable mDrawable = getDrawable();
        if(mDrawable == null){
            return;
        }
        if(getWidth() == 0 || getHeight() == 0){
            return;
        }
        this.measure(0, 0);
                /* 判断自己的包名和Activity是否等于*/
        if(mDrawable.getClass() == NinePatchDrawable.class){
            return;
        }
                /* 获取位图，并且将其复制给它*/
        Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap().copy(Bitmap.Config.ARGB_8888,true);

        if (defaultWidth == 0) {
            defaultWidth = getWidth();
        }
        if (defaultHeight == 0) {
            defaultHeight = getHeight();
        }

                /* 判断默认高度是否等于默认值*/
        if (defaultWidth != 0 && defaultHeight != 0) {
            RelativeLayout.LayoutParams params = new RelativeLayout.
                    LayoutParams(defaultWidth, defaultHeight);
            setLayoutParams(params);
        }
        int radius = 0;
        if (mBorderInsideColor != mDefValueColor
                && mBorderOutsideColor != mDefValueColor) {// 定义画两个边框，分别为外圆边框和内圆边框
            radius = (defaultWidth < defaultHeight ? defaultWidth
                    : defaultHeight) / 2 - 2 * mBorderThickness;
            // 画内圆
            drawCircleBorder(canvas, radius + mBorderThickness / 2,
                    mBorderInsideColor);
            // 画外圆
            drawCircleBorder(canvas, radius + mBorderThickness
                    + mBorderThickness / 2, mBorderOutsideColor);
        } else if (mBorderInsideColor != mDefValueColor
                && mBorderOutsideColor == mDefValueColor) {// 定义画一个边框
            radius = (defaultWidth < defaultHeight ? defaultWidth
                    : defaultHeight) / 2 - mBorderThickness;
            drawCircleBorder(canvas, radius + mBorderThickness / 2,
                    mBorderInsideColor);
        } else if (mBorderInsideColor == mDefValueColor
                && mBorderOutsideColor != mDefValueColor) {// 定义画一个边框
            radius = (defaultWidth < defaultHeight ? defaultWidth
                    : defaultHeight) / 2 - mBorderThickness;
            drawCircleBorder(canvas, radius + mBorderThickness / 2,
                    mBorderOutsideColor);
        } else {// 没有边框
            radius = (defaultWidth < defaultHeight ? defaultWidth
                    : defaultHeight) / 2;
        }
        Bitmap roundBitmap = getCroppedRoundBitmap(mBitmap, radius);
        canvas.drawBitmap(roundBitmap, defaultWidth / 2 - radius, defaultHeight
                / 2 - radius, null);
    }

    /**
     * 获取裁剪后的圆形图片
     *
     * @param radius
     *            半径
     */
    public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;

        // 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        int squareWidth = 0, squareHeight = 0;
        int x = 0, y = 0;
        Bitmap squareBitmap;
        if (bmpHeight > bmpWidth) {// 高大于宽
            squareWidth = squareHeight = bmpWidth;
            x = 0;
            y = (bmpHeight - bmpWidth) / 2;
            // 截取正方形图片
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
                    squareHeight);
        } else if (bmpHeight < bmpWidth) {// 宽大于高
            squareWidth = squareHeight = bmpHeight;
            x = (bmpWidth - bmpHeight) / 2;
            y = 0;
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
                    squareHeight);
        } else {
            squareBitmap = bmp;
        }

        if (squareBitmap.getWidth() != diameter
                || squareBitmap.getHeight() != diameter) {
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,
                    diameter, true);

        } else {
            scaledSrcBmp = squareBitmap;
        }
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
                scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2,
                paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
                /* bitmap回收(recycle导致在布局文件XML看不到效果) */
        bmp.recycle();
        squareBitmap.recycle();
        scaledSrcBmp.recycle();
        bmp = null;
        squareBitmap = null;
        scaledSrcBmp = null;
        return output;
    }

    /**
     * 边缘画圆
     */
    private void drawCircleBorder(Canvas canvas, int radius, int color) {
        Paint paint = new Paint();
                /* 去锯齿 */
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);
                /* 设置paint的　style　为STROKE：空心 */
        paint.setStyle(Paint.Style.STROKE);
                /* 设置paint的外框宽度 */
        paint.setStrokeWidth(mBorderThickness);
        canvas.drawCircle(defaultWidth / 2, defaultHeight / 2, radius, paint);
    }

}
