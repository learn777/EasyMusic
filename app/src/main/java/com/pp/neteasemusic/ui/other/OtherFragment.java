package com.pp.neteasemusic.ui.other;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pp.neteasemusic.databinding.FragmentOtherBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtherFragment extends Fragment {
    FragmentOtherBinding binding;

    public OtherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOtherBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              设置0到100的整数变化
                final ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 10, 0);
//              整个事件段是5秒
                valueAnimator.setDuration(10000);
//              数字均匀变化，也可设置其他的变化方式，先快后慢，先慢后快等......
                valueAnimator.setInterpolator(new LinearInterpolator());
//              监听每次改变时的值
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
//                      拿到每一次变化的值
                        Integer value = (Integer) animation.getAnimatedValue();
//                      把只设置到按钮上
                        binding.tvOther.setText(String.valueOf(value));
                    }
                });
                valueAnimator.start();
            }
        });

    }
}
