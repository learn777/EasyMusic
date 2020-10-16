package com.pp.neteasemusic.netease.compat;

import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pp.neteasemusic.R;

public class MyFloatingActionButton extends FloatingActionButton {

    float lastY, startY;
    float max = 0;
    private AudioManager manager;

    public MyFloatingActionButton(@NonNull Context context) {
        this(context, null);
    }

    public MyFloatingActionButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.floatingActionButtonStyle);
    }

    public MyFloatingActionButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        manager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = ev.getRawY();
                startY = getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float offY = ev.getRawY() - lastY;
                ViewGroup group = (ViewGroup) getParent();
                int height = group.getHeight();
                if (startY + getHeight() + offY <= height && startY + offY >= 0)
                    setY(startY + offY);
                if (offY > 0) max = Math.max(max, offY);
                if (offY < 0) max = Math.min(max, offY);
                return true;
            case MotionEvent.ACTION_UP:
                setY(startY);
                int index = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (max > 10) {
                    index--;
                } else if (max < -10) {
                    index++;
                }
                max = 0;
                index = Math.min(index, manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                manager.setStreamVolume(AudioManager.STREAM_MUSIC, index, AudioManager.FLAG_PLAY_SOUND);
                lastY = ev.getRawY();
                break;
        }
        performClick();
        return super.onTouchEvent(ev);
    }
}
