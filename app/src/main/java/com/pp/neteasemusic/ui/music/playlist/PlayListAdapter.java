package com.pp.neteasemusic.ui.music.playlist;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.pp.neteasemusic.R;
import com.pp.neteasemusic.netease.compat.PlayListCover;
import com.pp.neteasemusic.netease.net.DataRequest;
import com.pp.neteasemusic.netease.room.PlayList;
import com.pp.neteasemusic.netease.room.RoomManager;
import com.pp.neteasemusic.netease.utils.ScreenSizeUtils;
import com.pp.neteasemusic.netease.volley.VolleySingleton;
import com.pp.neteasemusic.ui.music.songlist.SongListViewModel;

import java.util.List;

public class PlayListAdapter extends ListAdapter<PlayList, PlayListAdapter.PlayListViewHolder> {

    PlayListAdapter() {
        super(new DiffUtil.ItemCallback<PlayList>() {
            @Override
            public boolean areItemsTheSame(@NonNull PlayList oldItem, @NonNull PlayList newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull PlayList oldItem, @NonNull PlayList newItem) {
                return oldItem.getName().equals(newItem.getName()) && oldItem.getCover().equals(newItem.getCover()) && oldItem.getPlayID().equals(newItem.getPlayID());
            }
        });
    }


    @NonNull
    @Override
    public PlayListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final PlayListViewHolder holder = new PlayListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_play_list, parent, false));
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!DataRequest.getPlayListID().equals(getCurrentList().get(holder.getAdapterPosition()).getPlayID())) {
                    //更新歌曲列表并切换页面
                    RoomManager.getViewPager().setCurrentItem(1, true);
                    RoomManager.getRefreshLayout().setRefreshing(true);
                    DataRequest.setPlayListID(getCurrentList().get(holder.getAdapterPosition()).getPlayID());
                    SongListViewModel.setClickAble(true);
                    SongListViewModel.fetch_Data();
                } else {
                    //不更新歌曲列表并切换页面
                    RoomManager.getViewPager().setCurrentItem(1, true);
                }
            }
        };
        holder.itemView.setOnClickListener(listener);
        holder.cover.setOnClick(listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PlayListViewHolder holder, final int position) {
        final int px = (int) (RoomManager.getDisplayMetrics().widthPixels / 2 + 0.5 - ScreenSizeUtils.dip2px(4));
        VolleySingleton.getInstance(RoomManager.getContext()).add(new ImageRequest(getCurrentList().get(position).getCover(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                holder.name.setText(getCurrentList().get(position).getName());
                if (response.getWidth() < px) {
                    float scale = ((float) px) / response.getWidth();
                    Matrix matrix = new Matrix();
                    matrix.postScale(scale, scale);
                    Bitmap tmp = Bitmap.createBitmap(response, 0, 0, response.getWidth(), response.getHeight(), matrix, true);
                    holder.cover.setImageBitmap(tmp);
                    return;
                }
                holder.cover.setImageBitmap(response);
            }
        }, px, px, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, null));
//        holder.cover.setOnLongClick(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RoomManager.getDao().delete(getCurrentList().get(position).getPlayID());
//                submitList(RoomManager.getDao().getAll());
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return getCurrentList().size();
    }

    @Override
    public void submitList(@Nullable List<PlayList> list) {
        super.submitList(list);
    }

    static class PlayListViewHolder extends RecyclerView.ViewHolder {
        PlayListCover cover;
        TextView name;

        PlayListViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            cover = itemView.findViewById(R.id.cover);
        }
    }
}
