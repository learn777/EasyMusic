package com.pp.neteasemusic.ui.music.songlist;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pp.neteasemusic.R;
import com.pp.neteasemusic.databinding.FragmentSongListBinding;
import com.pp.neteasemusic.netease.json.NeteaseResult;
import com.pp.neteasemusic.netease.room.RoomManager;
import com.pp.neteasemusic.netease.utils.OperationFrom;

import java.util.Objects;


public class SongListFragment extends Fragment implements View.OnClickListener {
    private FragmentSongListBinding binding;
    private SongsListAdapter adapter;
    private HandleProgress updateProgress;

    public SongListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("onCreate ");
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("onCreateView ");
        binding = FragmentSongListBinding.inflate(inflater, container, false);
        RoomManager.setRefreshLayout(binding.swiperefreshlayout);
        binding.swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SongListViewModel.fetch_Data();
            }
        });
        binding.btnPre.setOnClickListener(this);
        binding.btnPlay.setOnClickListener(this);
        binding.btnNext.setOnClickListener(this);
        binding.progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    if (SongListViewModel.getMusicInfo().getValue() == null) {
                        binding.progress.setProgress(0);
                        return;
                    }
                    RoomManager.getMusicController().progress(seekBar.getProgress());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        binding.locationOn.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        binding.songList.setLayoutManager(linearLayoutManager);
        setScrollTime(requireContext(), linearLayoutManager, 20);
        binding.songList.setHasFixedSize(true);
        binding.songList.setDrawingCacheEnabled(true);
        binding.songList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        adapter = new SongsListAdapter();
        adapter.setHasStableIds(true);
        binding.songList.setAdapter(adapter);
        new ViewModelProvider(this).get(SongListViewModel.class);
        //当前播放列表发生改变后的操作
        SongListViewModel.getSongsList().observe(getViewLifecycleOwner(), new Observer<NeteaseResult>() {
            @Override
            public void onChanged(NeteaseResult neteaseResult) {
                binding.textView.setText(neteaseResult.getResult().getName());
                adapter = new SongsListAdapter();
                adapter.submitList(neteaseResult.getResult().getTracks());
                binding.songList.setAdapter(adapter);
                binding.swiperefreshlayout.setRefreshing(false);
                updateSongList(true, true);
            }
        });
        //更新通知栏
        SongListViewModel.getUpdate().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (!aBoolean) return;
                updateObserver();

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                System.out.println("btn_next");
                SongListViewModel.pressNext(OperationFrom.BOTTOM_BAR);
                break;
            case R.id.btn_pre:
                System.out.println("btn_pre");
                SongListViewModel.pressPre(OperationFrom.BOTTOM_BAR);
                break;
            case R.id.btn_play:
                SongListViewModel.pressPlay(OperationFrom.BOTTOM_BAR);
                break;
            case R.id.location_on:
                if (SongListViewModel.getCurrent() > -1 && SongListViewModel.getSongsList().getValue() != null && SongListViewModel.getMusicInfo().getValue() != null) {
                    if (SongListViewModel.getSongsList().getValue().getResult().getTracks().get(SongListViewModel.getCurrent()).getId().equals(SongListViewModel.getMusicInfo().getValue().getId())) {
                        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.songList.getLayoutManager();
                        if (layoutManager != null && (SongListViewModel.getCurrent() < layoutManager.findFirstVisibleItemPosition() || SongListViewModel.getCurrent() >= layoutManager.findLastVisibleItemPosition())) {
                            int scrollTO = layoutManager.findFirstVisibleItemPosition();
                            scrollTO = scrollTO > SongListViewModel.getCurrent() ? SongListViewModel.getCurrent() - layoutManager.getChildCount() / 2 : SongListViewModel.getCurrent() + layoutManager.getChildCount() / 2;
                            binding.songList.smoothScrollToPosition(scrollTO);
                        }
                    }
                }
        }
    }

    private void updateObserver() {
        System.out.println("更新UI组件");
        boolean flag = SongListViewModel.updateNotification(true);
        if (updateProgress != null) {
            updateProgress.cancel(true);
        }
        if (!flag) {
            //暂停时重置进度条更新任务
            updateProgress = null;
        } else {
            //播放时启动进度条更新任务
            updateProgress = (HandleProgress) new HandleProgress(binding).execute();
        }
        SongListViewModel.getUpdate().setValue(false);
        updateSongList(false, flag);
    }

    /*
     *  更新播放列表信息以及列表UI状态
     * scrollToTop  滚动至列表顶部
     * isPlay   播放状态
     */
    private void updateSongList(boolean scrollToTop, boolean isPlay) {
        if (SongListViewModel.getCurrent() >= 0) {
            //改变原本播放的
            if (adapter.getOldHolder() != null) {
                if (adapter.getAnimator() != null) {
                    adapter.getAnimator().end();
                }
                adapter.getOldHolder().song_name.setTextColor(adapter.getColorStateList());
                adapter.getOldHolder().song_duration.setTextColor(adapter.getColorStateList());
                adapter.getOldHolder().setToys(View.INVISIBLE);
                adapter.getOldHolder().order.setVisibility(View.VISIBLE);
            }
            //改变现在播放的
            System.out.println("SongListViewModel.getCurrent()::" + SongListViewModel.getCurrent());
            SongsListAdapter.ViewHolder holder;
            holder = (SongsListAdapter.ViewHolder) binding.songList.findViewHolderForAdapterPosition(SongListViewModel.getCurrent());
            System.out.println("findViewHolderForLayoutPosition::" + SongListViewModel.getCurrent());
            if (holder != null && Objects.requireNonNull(SongListViewModel.getSongsList().getValue()).getResult().getTracks().get(SongListViewModel.getCurrent()).getId().equals(Objects.requireNonNull(SongListViewModel.getMusicInfo().getValue()).getId())) {
                System.out.println("findViewHolderInIf::" + SongListViewModel.getCurrent());
                holder.order.setVisibility(View.INVISIBLE);
                holder.setToys(View.VISIBLE);
                holder.song_name.setTextColor(Color.RED);
                holder.song_duration.setTextColor(Color.RED);
                adapter.setAnimator(ObjectAnimator.ofFloat(holder.toys, "Rotation", 0f, 360f), isPlay);
                adapter.setOld_holder(holder);
            }
        }
        if (scrollToTop) {
            binding.songList.scrollToPosition(0);
        }
    }

    /*
     * 从后台切换至前台时刷新UI
     */
    @Override
    public void onResume() {
        System.out.println("onResume ");
        SongListViewModel.setSongListFragmentState(true);
        try {
            if (RoomManager.getMusicController() != null && RoomManager.getMusicController().isPlaying()) {
                updateProgress = (HandleProgress) new HandleProgress(binding).execute();
                updateSongList(false, true);
            } else {
                updateSongList(false, false);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (SongListViewModel.getMusicInfo().getValue() != null) {
            binding.songName.setText(Objects.requireNonNull(SongListViewModel.getMusicInfo().getValue()).getName());
        }

        super.onResume();
    }

    /*
     *切换至后台时清理UI刷新，节省资源
     */
    @Override
    public void onPause() {
        System.out.println("onPause ");
        SongListViewModel.setSongListFragmentState(false);
        if (adapter.getOldHolder() != null) {
            adapter.getOldHolder().song_name.setTextColor(adapter.getColorStateList());
            adapter.getOldHolder().song_duration.setTextColor(adapter.getColorStateList());
            adapter.getOldHolder().setToys(View.INVISIBLE);
            adapter.getOldHolder().order.setVisibility(View.VISIBLE);
        }

        if (updateProgress != null) {
            updateProgress.cancel(true);
            updateProgress = null;
        }
        adapter.setOld_holder(null);
        if (adapter.getAnimator() != null) {
            adapter.getAnimator().pause();
            adapter.getAnimator().end();
            adapter.setAnimator(null, false);
        }
        super.onPause();
    }

    private void setScrollTime(Context context, RecyclerView.LayoutManager layoutManager, final int scale) {
        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(context) {
            @Override
            protected int calculateTimeForScrolling(int dx) {
                return super.calculateTimeForScrolling(dx) * scale;
            }
        };
        smoothScroller.setTargetPosition(0);
        layoutManager.startSmoothScroll(smoothScroller);
    }
}