package com.pp.neteasemusic.netease.net;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.pp.neteasemusic.netease.json.NeteaseResponse;
import com.pp.neteasemusic.netease.json.NeteaseResult;
import com.pp.neteasemusic.netease.room.RoomManager;
import com.pp.neteasemusic.netease.utils.ToastUtils;
import com.pp.neteasemusic.netease.volley.VolleySingleton;
import com.pp.neteasemusic.ui.music.songlist.SongListViewModel;


/**
 * @author PPM
 * @time 2020/5/13 0:39
 * @class DataRequest
 * @description 请求码
 */
public class DataRequest {
    /*
     * playListID   当前歌单列表ID
     */
    private static String playListID = "722767010";

    public static void getPlayListResult(final Context context, final MutableLiveData<NeteaseResult> songList) {
        String url = "https://music.163.com/api/playlist/detail?id=" + playListID;
        System.out.println(url);
        VolleySingleton.getInstance(context).add(new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                NeteaseResponse neteaseResponse = new Gson().fromJson(response, NeteaseResponse.class);
                if (neteaseResponse.getCode() == 200) {
                    NeteaseResult result = new Gson().fromJson(response, NeteaseResult.class);
                    SongListViewModel.getmCache().put(playListID, result);
                    songList.postValue(result);
                } else {
                    ToastUtils.ToastNoAppName("频繁刷新，暂时无法使用").show();
                    RoomManager.getRefreshLayout().setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.ToastNoAppName("请检查网络").show();
            }
        }));
    }

    public static String getPlayListID() {
        return playListID;
    }

    public static void setPlayListID(String playListID) {
        DataRequest.playListID = playListID;
    }
}
