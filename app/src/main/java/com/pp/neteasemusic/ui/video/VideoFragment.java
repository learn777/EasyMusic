package com.pp.neteasemusic.ui.video;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModelProvider;

import com.pp.neteasemusic.databinding.FragmentVideoBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment implements View.OnClickListener {
    private FragmentVideoBinding binding;
    private VideoViewModel videoViewModel;

    public VideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVideoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        videoViewModel = new ViewModelProvider(requireActivity()).get(VideoViewModel.class);
        videoViewModel.binding = binding;
        String url = "https://gss3.baidu.com/6LZ0ej3k1Qd3ote6lo7D0j9wehsv/tieba-smallvideo/60_bdd34c3e3e76f86a157cb01c3fa82771.mp4";
        videoViewModel.initPlayer();
        videoViewModel.loadPlayer(url);
        binding.playerView.setPlayer(videoViewModel.player);
        if (binding.playerView.getController().getFullScreen() != null) {
            binding.playerView.getController().getFullScreen().setOnClickListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        videoViewModel.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        videoViewModel.onResume();
        if (requireActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoViewModel.player.release();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void onClick(View v) {
        if (requireActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
}
