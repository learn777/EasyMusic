package com.pp.neteasemusic.ui.songlist;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.pp.neteasemusic.R;
import com.pp.neteasemusic.netease.json.MusicInfo;
import com.pp.neteasemusic.netease.utils.OperationFrom;
import com.pp.neteasemusic.netease.utils.TimeUtils;

import java.util.Objects;

public class SongsListAdapter extends ListAdapter<MusicInfo, SongsListAdapter.ViewHolder> {
    private ViewHolder old_holder = null;

    private ObjectAnimator animator = null;

    ObjectAnimator getAnimator() {
        return animator;
    }

    void setAnimator(ObjectAnimator animator) {
        if (animator != null) {
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new LinearInterpolator());
            animator.setDuration(1000);
            this.animator = animator;
            animator.start();
        }
    }

    SongsListAdapter() {
        super(new DiffUtil.ItemCallback<MusicInfo>() {
            @Override
            public boolean areItemsTheSame(@NonNull MusicInfo oldItem, @NonNull MusicInfo newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull MusicInfo oldItem, @NonNull MusicInfo newItem) {
                return oldItem.getName().equals(newItem.getName());
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ViewHolder holder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_songs_list, parent, false));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (animator != null && animator.isRunning()) {
                    animator.end();
                }
                if (SongListViewModel.isClickAble() || !Objects.requireNonNull(SongListViewModel.getMusicInfo().getValue()).getId().equals(Objects.requireNonNull(SongListViewModel.getSongsList().getValue()).getResult().getTracks().get(holder.getAdapterPosition()).getId()) || SongListViewModel.getMusicInfo().getValue() == null) {
                    SongListViewModel.setClickAble(false);
                    if (old_holder != null) {
                        old_holder.viewStub.setVisibility(View.INVISIBLE);
                        old_holder.order.setVisibility(View.VISIBLE);
                    }
                    old_holder = holder;
                    try {
                        holder.viewStub.inflate();// 第二次加载会抛出异常
                    } catch (Exception e) {
                        holder.viewStub.setVisibility(View.VISIBLE);
                    }
                    holder.order.setVisibility(View.INVISIBLE);
                    SongListViewModel.setCurrent(holder.getAdapterPosition(), OperationFrom.BOTTOM_BAR);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        System.out.println("onBindViewHolder:::" + position);
        holder.order.setText(String.valueOf(position + 1));
        holder.song_name.setText(getCurrentList().get(position).getName());
        holder.song_duration.setText(TimeUtils.timeFormat(getCurrentList().get(position).getDuration()));
        if (SongListViewModel.getMusicInfo().getValue() != null && SongListViewModel.getSongsList().getValue() != null && SongListViewModel.getMusicInfo().getValue().getId().equals(SongListViewModel.getSongsList().getValue().getResult().getTracks().get(position).getId())) {
            holder.order.setVisibility(View.INVISIBLE);
            try {
                holder.viewStub.inflate();
            } catch (Exception e) {
                holder.viewStub.setVisibility(View.VISIBLE);
            } finally {
                holder.toys = holder.itemView.findViewById(R.id.stub_icon);
            }
            setAnimator(ObjectAnimator.ofFloat(holder.toys, "Rotation", 0f, 360f));
            old_holder = holder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return getCurrentList().size();
    }

    ViewHolder getOldHolder() {
        return old_holder;
    }

    void setOld_holder(ViewHolder old_holder) {
        this.old_holder = old_holder;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView order, song_duration, song_name;
        ImageView toys;
        ViewStub viewStub;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            order = itemView.findViewById(R.id.order);
            song_name = itemView.findViewById(R.id.song_name);
            song_duration = itemView.findViewById(R.id.song_duration);
            viewStub = itemView.findViewById(R.id.viewStub);
        }
    }
}
