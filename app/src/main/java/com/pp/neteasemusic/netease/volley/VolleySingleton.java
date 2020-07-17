package com.pp.neteasemusic.netease.volley;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private static RequestQueue instance = null;

    public synchronized static RequestQueue getInstance(Context context) {
        if (instance == null) {
            instance = Volley.newRequestQueue(context.getApplicationContext());
        }
        return instance;
    }
}
