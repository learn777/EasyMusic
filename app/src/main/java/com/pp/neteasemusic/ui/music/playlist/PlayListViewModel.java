package com.pp.neteasemusic.ui.music.playlist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class PlayListViewModel extends AndroidViewModel {

    public PlayListViewModel(@NonNull Application application) {
        super(application);
        //        DataRequest.getPlayListResult(application.getApplicationContext(), result, "722767010");
        //我的最爱：https://music.163.com/api/playlist/detail?id=722767010
        //银临50：https://music.163.com/api/playlist/detail?id=2366891430
        //任然50：https://music.163.com/api/playlist/detail?id=2366866560
    }

}
