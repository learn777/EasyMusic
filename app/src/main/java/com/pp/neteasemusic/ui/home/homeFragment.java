package com.pp.neteasemusic.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.pp.neteasemusic.R;
import com.pp.neteasemusic.SectionsPagerAdapter;
import com.pp.neteasemusic.databinding.FragmentHomeBinding;
import com.pp.neteasemusic.netease.notification.NeteaseNotification;
import com.pp.neteasemusic.netease.room.RoomManager;

public class homeFragment extends Fragment {
    private FragmentHomeBinding binding;

    public homeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        int resourcesId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourcesId > 0) {
            int height = getResources().getDimensionPixelSize(resourcesId);
            binding.tabs.setPadding(0, height, 0, 0);
            System.out.println("----------------" + height + "px------------------------");
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //初始化通知栏相关设置
        NeteaseNotification.initInstance();
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(requireContext(), getChildFragmentManager());
        ViewPager viewPager = requireView().findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = requireView().findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        RoomManager.setViewPager(viewPager);
    }
}
