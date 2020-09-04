package com.pp.neteasemusic.ui.video;

import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.ViewModel;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.pp.neteasemusic.databinding.FragmentVideoBinding;

import java.util.Formatter;
import java.util.Locale;

public class VideoViewModel extends ViewModel {
    SimpleExoPlayer player;
    FragmentVideoBinding binding;
    private long current_position = 0;

    public VideoViewModel() {
    }

    void initPlayer() {
        TrackSelection.Factory factory = new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
        TrackSelector trackSelector = new DefaultTrackSelector(factory);
        player = ExoPlayerFactory.newSimpleInstance(binding.getRoot().getContext(), trackSelector);
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
                Log.d("hello", "onTimelineChanged");
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.d("hello", "onTracksChanged");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.d("hello", "onLoadingChanged");
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.d("hello", "onPlayerStateChanged: playWhenReady = " + String.valueOf(playWhenReady)
                        + " playbackState = " + playbackState);
                switch (playbackState) {
                    case Player.STATE_ENDED:
                        Log.d("hello", "Playback ended!");
                        //Stop playback and return to start position
                        setPlayPause(false);
                        binding.playerView.setKeepScreenOn(false);
                        player.seekTo(0);
                        break;
                    case Player.STATE_READY:
                        binding.progressBar.setVisibility(View.GONE);
                        Log.d("hello", "ExoPlayer ready! pos: " + player.getCurrentPosition()
                                + " max: " + stringForTime((int) player.getDuration()));
//                        setProgress(0);
                        break;
                    case Player.STATE_BUFFERING:
                        Log.d("hello", "Playback buffering!");
                        binding.progressBar.setVisibility(View.VISIBLE);
                        break;
                    case Player.STATE_IDLE:
                        Log.d("hello", "ExoPlayer idle!");
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.d("hello", "onPlaybackError: " + error.getMessage());
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                Log.d("hello", "onPositionDiscontinuity");
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Log.d("hello", "MainActivity.onPlaybackParametersChanged." + playbackParameters.toString());
            }

            @Override
            public void onSeekProcessed() {

            }
        });
    }

    void loadPlayer(String url) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory factory = new DefaultDataSourceFactory(binding.getRoot().getContext(), Util.getUserAgent(binding.getRoot().getContext(), "myExoPlayer"), bandwidthMeter);
        Uri uri = Uri.parse(url);
        MediaSource source = new ExtractorMediaSource.Factory(factory).createMediaSource(uri);
        player.prepare(source);
    }

    void onResume() {
        player.seekTo(current_position);
        setPlayPause(true);
    }

    void onPause() {
        current_position = player.getCurrentPosition();
        setPlayPause(false);
    }

    private String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder;
        Formatter mFormatter;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private void setPlayPause(boolean _play) {
        player.setPlayWhenReady(_play);
        binding.playerView.setKeepScreenOn(_play);
    }
}
