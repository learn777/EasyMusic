package com.pp.neteasemusic.ui.songlist;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.RemoteException;

import com.pp.neteasemusic.R;
import com.pp.neteasemusic.databinding.FragmentSongListBinding;
import com.pp.neteasemusic.netease.room.RoomManager;
import com.pp.neteasemusic.netease.utils.TimeUtils;

import java.util.Objects;

public class HandleProgress extends AsyncTask<Void, Void, Void> {
    @SuppressLint("StaticFieldLeak")
    private FragmentSongListBinding binding;
    private int max;

    HandleProgress(FragmentSongListBinding binding) {
        this.binding = binding;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        binding.songName.setText(Objects.requireNonNull(SongListViewModel.getMusicInfo().getValue()).getName());
        max = SongListViewModel.getMusicInfo().getValue().getDuration();
        binding.songMax.setText(TimeUtils.timeFormat(max));
        binding.progress.setMax(max);
        try {
            binding.progress.setProgress(RoomManager.getMusicController().getCurrentTime());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        binding.songNow.setText(R.string.TIME_ZORE);
        binding.songMax.setText(TimeUtils.timeFormat(max));
//        binding.btnPlay.setImageResource(R.drawable.ic_pause_green_36dp);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while (--max >= 0 && !isCancelled()) {
            try {
                publishProgress();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    @Override
    protected void onProgressUpdate(Void... voids) {
        try {
            int progress = RoomManager.getMusicController().getCurrentTime();
            binding.songNow.setText(TimeUtils.timeFormat(progress));
            binding.progress.setProgress(progress);
            binding.btnPlay.setImageResource(R.drawable.ic_pause_green_36dp);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        binding.btnPlay.setImageResource(R.drawable.ic_play_arrow_red_36dp);
    }
}
