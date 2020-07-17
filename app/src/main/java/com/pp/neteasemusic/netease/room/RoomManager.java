package com.pp.neteasemusic.netease.room;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.pp.neteasemusic.IMusicController;

/*
 *需要在启动程序时在MainActivity中执行initRoomManager进行初始化操作；
 */
public class RoomManager {
    private static Context context = null;
    private static ViewPager viewPager = null;
    private static SwipeRefreshLayout refreshLayout = null;
    private static IMusicController musicController = null;
    private static Display display;
    private static DisplayMetrics displayMetrics;

    public static DisplayMetrics getDisplayMetrics() {
        return displayMetrics;
    }

    public static Display getDisplay() {
        return display;
    }

    public static void setDisplay(Display display) {
        displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        RoomManager.display = display;
    }

    public static SwipeRefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    public static void setRefreshLayout(SwipeRefreshLayout songListView) {
        RoomManager.refreshLayout = songListView;
    }

    public static Context getContext() {
        return context;
    }

    public static void initRoomManager(Context context) {
        RoomManager.context = context;
    }

    public static ViewPager getViewPager() {
        return viewPager;
    }

    public static void setViewPager(ViewPager viewPager) {
        RoomManager.viewPager = viewPager;
    }

    /**
     * @return 返回一个数据库接口PlayListDao对象
     * @author PPM
     * @time 2020/5/14 18:14
     * @method getInstance
     * @parameters context 用于构建数据的的context
     */
    public static PlayListDao getDao() {
        return PlayListDatabase.getInstance(context).getPlayListDao();
    }

    public static IMusicController getMusicController() {
        return musicController;
    }

    public static void setMusicController(IMusicController musicController) {
        RoomManager.musicController = musicController;
    }
}
