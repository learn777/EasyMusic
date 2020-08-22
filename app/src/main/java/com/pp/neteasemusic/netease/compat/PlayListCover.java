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

public class PlayListCover extends androidx.appcompat.widget.AppCompatImageView implements Runnable {
    private static final int TOUCH_SLOP = 2;
    Paint mPaint;
    private final int LONG_PRESS_TIME = 3000;
    private int lastX, lastY;
    private boolean isMove = false;
    private OnClickListener onClick = null;
    private OnClickListener onLongClick = null;
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

    public void setOnClick(OnClickListener onClick) {
        this.onClick = onClick;
    }

    public void setOnLongClick(OnClickListener onLongClick) {
        this.onLongClick = onLongClick;
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
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                System.out.println("点击了图片" + lastX + "," + lastY);
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                isMove = false;
                animator = null;
                long_press_mask = true;
                postDelayed(this, LONG_PRESS_TIME);
                break;
            case MotionEvent.ACTION_UP:
                long_press_mask = false;
                System.out.println("松开了：" + lastX + "," + lastY);
                removeCallbacks(this);
                if (animator == null && onClick != null) {
                    onClick.onClick(this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                long_press_mask = false;
                System.out.println("滑动了：" + lastX + "," + lastY);
                if (isMove) {
                    removeCallbacks(this);
                    break;
                }
                if (Math.abs(lastX - event.getX()) > TOUCH_SLOP || Math.abs(lastY - event.getY()) > TOUCH_SLOP) {
                    //移动超过了阈值，表示移动了
                    isMove = true;
                    //移除runnable
                    removeCallbacks(this);
                }
                break;
        }
        return true;
    }

    @Override
    public void run() {
        if (onLongClick != null && long_press_mask) {
            onLongClick.onClick(this);
        }
    }
}
