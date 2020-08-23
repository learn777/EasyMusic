package com.pp.neteasemusic.ui.playlist;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.pp.neteasemusic.databinding.FragmentPlayListBinding;
import com.pp.neteasemusic.netease.json.NeteaseResult;
import com.pp.neteasemusic.netease.room.PlayList;
import com.pp.neteasemusic.netease.room.RoomManager;
import com.pp.neteasemusic.netease.utils.NetworkCheckUtils;
import com.pp.neteasemusic.netease.utils.ToastUtils;
import com.pp.neteasemusic.netease.volley.VolleySingleton;

public class PlayListFragment extends Fragment implements View.OnClickListener {
    private PlayListAdapter adapter;
    private FragmentPlayListBinding binding;

    public PlayListFragment() {
        adapter = new PlayListAdapter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPlayListBinding.inflate(inflater, container, false);
        binding.playList.setLayoutManager(new GridLayoutManager(requireActivity().getApplicationContext(), 2, GridLayoutManager.VERTICAL, false));
        binding.playList.setAdapter(adapter);
        binding.fab.setOnClickListener(this);
        binding.fab.getBackgroundTintList();
        binding.refreshPlayList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!NetworkCheckUtils.checkNet()) {
                    ToastUtils.ToastNoAppName("网络不可用,检查后重试").show();
                } else {
                    adapter.submitList(RoomManager.getDao().getAll());
                }
                binding.refreshPlayList.setRefreshing(false);
            }
        });
        if (!NetworkCheckUtils.checkNet()) {
            ToastUtils.ToastNoAppName("网络不可用,检查后重试").show();
        } else {
            adapter.submitList(RoomManager.getDao().getAll());
        }
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(final View v) {
        if (!NetworkCheckUtils.checkNet()) {
            ToastUtils.ToastNoAppName("网络不可用,检查后重试").show();
            return;
        }
        insertPlay(v);
    }

    private String getClipPlaylistID() {
        ClipboardManager clipboardManager = RoomManager.getContext().getSystemService(ClipboardManager.class);
        String clip = null;
        if (clipboardManager != null) {
            ClipData clipData = clipboardManager.getPrimaryClip();
            if (clipData != null) {
                CharSequence charSequence = clipData.getItemAt(0).getText();
                if (charSequence == null || charSequence.equals("")) return null;
                clip = charSequence.toString();
                System.out.println("****************************************");
                System.out.println(clip);
                clip = clip.substring(clip.indexOf("playlist") + 9);
                clip = clip.split("/")[0];
                if (clip.startsWith("id=")) {
                    clip = clip.substring(3);
                    clip = clip.split("&")[0];
                }
                System.out.println(clip);
                System.out.println("****************************************");
            }
        }
        return clip;
    }

    private void insertPlay(final View v) {
        final String id = getClipPlaylistID();
        if (id == null) {
            ToastUtils.ToastNoAppName("内容不正确，请重新复制").show();
        } else if (RoomManager.getDao().getExist(id).size() == 0) {
            String url = "https://music.163.com/api/playlist/detail?id=" + id;
            System.out.println(url);
            VolleySingleton.getInstance(RoomManager.getContext()).add(new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    NeteaseResult result = new Gson().fromJson(response, NeteaseResult.class);
                    if (result.getResult() == null) {
                        ToastUtils.ToastNoAppName("歌单有误，请重新复制").show();
                    } else {
                        String name = result.getResult().getName();
                        System.out.println(name);
                        String cover = result.getResult().getCoverImgUrl();
                        System.out.println(cover);
                        RoomManager.getDao().insert(new PlayList(id, name, cover));
                        adapter.submitList(RoomManager.getDao().getAll());
                        Snackbar.make(v, "已添加", Snackbar.LENGTH_LONG)
                                .setAction("确定", null).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }));
        } else {
            Snackbar.make(v, "歌单已存在", Snackbar.LENGTH_SHORT)
                    .setAction("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    })
                    .setActionTextColor(0xFFFFF)
                    .setBackgroundTint(0xff2287FA)
                    .show();
        }
    }
}
