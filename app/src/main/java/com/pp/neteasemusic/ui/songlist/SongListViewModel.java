package com.pp.neteasemusic.ui.songlist;

import android.os.RemoteException;
import android.util.LruCache;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pp.neteasemusic.R;
import com.pp.neteasemusic.netease.json.MusicInfo;
import com.pp.neteasemusic.netease.json.NeteaseResult;
import com.pp.neteasemusic.netease.net.DataRequest;
import com.pp.neteasemusic.netease.notification.NeteaseNotification;
import com.pp.neteasemusic.netease.room.RoomManager;
import com.pp.neteasemusic.netease.utils.NetworkCheckUtils;
import com.pp.neteasemusic.netease.utils.OperationFrom;
import com.pp.neteasemusic.netease.utils.ToastUtils;

import java.util.Objects;

public class SongListViewModel extends ViewModel {
    private static MutableLiveData<NeteaseResult> songList = new MutableLiveData<>();//保存获取到的歌曲列表,每次抓取后更新
    private static MutableLiveData<MusicInfo> musicInfo = new MutableLiveData<>(); //当前播放歌曲的下标
    private static MutableLiveData<Boolean> update = new MutableLiveData<>(); //是否需要更新UI视图
    private static int current = -1; //当前播放歌曲在列表中的坐标
    private static boolean clickAble = true;    //在更新歌曲列表后能否点击更新前的相同选项
    private static boolean SONG_LIST_FRAGMENT_STATE = true; //当前活动状态true为前台，false为后台
    private static LruCache<String, NeteaseResult> mCache = new LruCache<>(10 * 1024);

    public static LruCache<String, NeteaseResult> getmCache() {
        return mCache;
    }

    public SongListViewModel() {
//        自动获取歌单
//        PlayList list = RoomManager.getDao().getAll().get(0);
//        if (list != null) {
//            DataRequest.setPlayListID(list.getPlayID());
//        }
//        fetch_Data();
    }

    public static int getCurrent() {
        return current;
    }

    public static void setId(int id) {
        SongListViewModel.current = id;
    }

    private static boolean isSongListFragmentState() {
        return SONG_LIST_FRAGMENT_STATE;
    }

    static void setSongListFragmentState(boolean songListFragmentState) {
        SONG_LIST_FRAGMENT_STATE = songListFragmentState;
    }

    static MutableLiveData<Boolean> getUpdate() {
        return update;
    }

    public static MutableLiveData<MusicInfo> getMusicInfo() {
        return musicInfo;
    }

    // option 歌曲在列表中的位置
    synchronized static void setCurrent(int position, OperationFrom requestFrom) {
        switch (requestFrom) {
            case BOTTOM_BAR:
                //全部更新
                if (position >= 0) {
                    current = position;
                    musicInfo.setValue(Objects.requireNonNull(songList.getValue()).getResult().getTracks().get(position));
                }
                update.postValue(true);
                break;
            case NOTIFICATION:
            case MUSIC_SERVICE:
                if (position >= 0 && songList.getValue() != null) {
                    current = position;
                    musicInfo.setValue(songList.getValue().getResult().getTracks().get(position));
                }
                if (isSongListFragmentState()) {
                    update.setValue(true);
                } else {
                    updateNotification(position >= 0);
                }
                break;
        }
    }

    public static void pressPlay(OperationFrom requestFrom) {
        if (musicInfo.getValue() != null) {
            setCurrent(-1, requestFrom);
        } else {
            if (songList.getValue() != null) {
                setCurrent(0, requestFrom);
            } else {
                ToastUtils.ToastNoAppName("选中歌单后即可播放").show();
            }
        }

    }

    public static void pressNext(OperationFrom requestFrom) {
        if (getSongsList().getValue() != null) {
            current = current + 1 > getSongsList().getValue().getResult().getTracks().size() - 1 ? 0 : current + 1;
            setCurrent(current, requestFrom);
        } else {
            pressPlay(requestFrom);
        }
    }

    public static void pressPre(OperationFrom requestFrom) {
        if (getSongsList().getValue() != null) {
            current = current - 1 < 0 ? getSongsList().getValue().getResult().getTracks().size() - 1 : current - 1;
            setCurrent(current, requestFrom);
        } else {
            pressPlay(requestFrom);
        }
    }

    public synchronized static MutableLiveData<NeteaseResult> getSongsList() {
        return songList;
    }

    public static void fetch_Data() {
        if (mCache.get(DataRequest.getPlayListID()) != null) {
            getSongsList().postValue(mCache.get(DataRequest.getPlayListID()));
        } else if (!NetworkCheckUtils.checkNet()) {
            ToastUtils.ToastNoAppName("网络不可用").show();
            RoomManager.getRefreshLayout().setRefreshing(false);
        } else {
            DataRequest.getPlayListResult(RoomManager.getContext(), songList);
        }
    }

    static boolean isClickAble() {
        return clickAble;
    }

    public static void setClickAble(boolean clickAble) {
        SongListViewModel.clickAble = clickAble;
    }

    static boolean updateNotification(boolean reload) {
        boolean flag = false;
        try {
            flag = RoomManager.getMusicController().play(Objects.requireNonNull(SongListViewModel.getMusicInfo().getValue()).getId());
            if (reload) {
                NeteaseNotification.update(Objects.requireNonNull(musicInfo.getValue()).getAlbum().getPicUrl()
                        , flag ? R.drawable.ic_pause_green_36dp : R.drawable.ic_play_arrow_red_36dp,
                        true);
            } else {
                NeteaseNotification.update(null, flag ? R.drawable.ic_pause_green_36dp : R.drawable.ic_play_arrow_red_36dp, false);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return flag;
    }
}