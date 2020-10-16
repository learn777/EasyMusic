package com.pp.neteasemusic.netease.compat;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;

import androidx.annotation.Nullable;

public class PlayListCover extends androidx.appcompat.widget.AppCompatImageView {
    private static final int TOUCH_SLOP = 2;
    Paint mPaint;
    private final int LONG_PRESS_TIME = 3000;
    private int lastX, lastY;
    private boolean isMove = false;
    private boolean long_press_mask = false;
    private ObjectAnimator animator = null;
    private BitmapShader shader;

    @Override
    public void setAnimation(Animation animation) {

        super.setAnimation(animation);
    }

    public PlayListCover(Context context) {
        this(context, null);
//        mPaint.setFilterBitmap(true);
    }

    public PlayListCover(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
//        mPaint.setFilterBitmap(true);
    }

    public PlayListCover(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
//        mPaint.setFilterBitmap(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float rd = getWidth() * 0.165f;
        mPaint.setAntiAlias(true);
        mPaint.setShader(shader);
        canvas.drawRoundRect(0, 0, getWidth(), getHeight(), rd, rd, mPaint);
//        super.onDraw(canvas);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        shader = new BitmapShader(bm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        super.setImageBitmap(bm);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        return super.onTouchEvent(event);
    }

}
