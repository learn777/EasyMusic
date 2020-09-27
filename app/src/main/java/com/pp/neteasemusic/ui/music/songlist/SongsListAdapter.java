package com.pp.neteasemusic.ui.music.songlist;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.pp.neteasemusic.R;
import com.pp.neteasemusic.netease.json.MusicInfo;
import com.pp.neteasemusic.netease.room.RoomManager;
import com.pp.neteasemusic.netease.utils.OperationFrom;
import com.pp.neteasemusic.netease.utils.TimeUtils;

import java.util.Objects;

public class SongsListAdapter extends ListAdapter<MusicInfo, SongsListAdapter.ViewHolder> {
    private ViewHolder old_holder = null;
    private ColorStateList colorStateList = null;
    private ObjectAnimator animator = null;

    ColorStateList getColorStateList() {
        return colorStateList;
    }

    ObjectAnimator getAnimator() {
        return animator;
    }

    void setAnimator(ObjectAnimator animator, boolean isPlay) {
        if (animator != null && isPlay) {
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
        //获取TextView默认文字颜色
        colorStateList = holder.song_name.getTextColors();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SongListViewModel.isClickAble() || !Objects.requireNonNull(SongListViewModel.getMusicInfo().getValue()).getId().equals(Objects.requireNonNull(SongListViewModel.getSongsList().getValue()).getResult().getTracks().get(holder.getAdapterPosition()).getId()) || SongListViewModel.getMusicInfo().getValue() == null) {
                    SongListViewModel.setClickAble(false);
                    if (old_holder != null) {
                        old_holder.song_name.setTextColor(colorStateList);
                        old_holder.song_duration.setTextColor(colorStateList);
                        old_holder.setToys(View.INVISIBLE);
                        old_holder.order.setVisibility(View.VISIBLE);
                    }
                    old_holder = holder;
                    SongListViewModel.setCurrent(holder.getAdapterPosition(), OperationFrom.BOTTOM_BAR);
                } else {
                    if (animator != null && animator.isStarted()) {
                        animator.end();
                    } else {
                        if (animator != null)
                            animator.start();
                    }
                    SongListViewModel.setCurrent(holder.getAdapterPosition(), OperationFrom.BOTTOM_BAR);
                    SongListViewModel.setClickAble(true);
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
        if (SongListViewModel.getCurrent() == position && Objects.requireNonNull(SongListViewModel.getMusicInfo().getValue()).getId().equals(Objects.requireNonNull(SongListViewModel.getSongsList().getValue()).getResult().getTracks().get(holder.getAdapterPosition()).getId())) {
            holder.order.setVisibility(View.INVISIBLE);
            holder.song_name.setTextColor(Color.RED);
            holder.song_duration.setTextColor(Color.RED);
            holder.setToys(View.VISIBLE);
            try {
                setAnimator(ObjectAnimator.ofFloat(holder.toys, "Rotation", 0f, 360f), RoomManager.getMusicController().isPlaying());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            old_holder = holder;
        } else {
            holder.order.setVisibility(View.VISIBLE);
            holder.song_name.setTextColor(colorStateList);
            holder.song_duration.setTextColor(colorStateList);
            holder.setToys(View.INVISIBLE);
        }
    }

    @Override
    public long getItemId(int position) {
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

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            order = itemView.findViewById(R.id.order);
            song_duration = itemView.findViewById(R.id.song_duration);
            song_name = itemView.findViewById(R.id.song_name);
        }

        public ImageView getToys() {
            if (toys == null) {
                toys = itemView.findViewById(R.id.icon_flover);
            }
            return toys;
        }

        void setToys(int visible) {
            if (toys == null) {
                toys = itemView.findViewById(R.id.icon_flover);
            }
            toys.setVisibility(visible);
        }
    }
}
