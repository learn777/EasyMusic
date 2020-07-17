package com.pp.neteasemusic.netease.compat;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class AutoScrollTextView extends androidx.appcompat.widget.AppCompatTextView {
    public AutoScrollTextView(Context context) {
        super(context);
    }

    public AutoScrollTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoScrollTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
