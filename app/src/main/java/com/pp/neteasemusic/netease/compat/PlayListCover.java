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
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;

public class PlayListCover extends androidx.appcompat.widget.AppCompatImageView implements Runnable {
    private static final int TOUCH_SLOP = 2;
    Paint mPaint;
    private int lastX, lastY;
    private boolean isMove = false;
    private OnClickListener onClick = null;
    private OnClickListener onLongClick = null;
    private ObjectAnimator animator = null;
    private BitmapShader shader;

    public PlayListCover(Context context) {
        super(context);
        mPaint = new Paint();
//        mPaint.setFilterBitmap(true);
    }

    public PlayListCover(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
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
                postDelayed(this, ViewConfiguration.getLongPressTimeout());
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("松开了：" + lastX + "," + lastY);
                removeCallbacks(this);
                if (animator == null && onClick != null) {
                    onClick.onClick(this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
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
        //     float px = (float) (0.2*(RoomManager.getDisplayMetrics().widthPixels / 2 + 0.5 - ScreenSizeUtils.dip2px(4)));
        float px = 0.05f * getMeasuredHeight();
        animator = ObjectAnimator.ofFloat(this, "translationY", -px, px, -px, px, -px, 0);
        animator.setDuration(800);
        if (!animator.isRunning()) {
            animator.start();
        }
        if (onLongClick != null) {
            onLongClick.onClick(this);
        }
    }
}
