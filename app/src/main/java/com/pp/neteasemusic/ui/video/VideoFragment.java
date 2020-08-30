package com.pp.neteasemusic.ui.video;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pp.neteasemusic.databinding.FragmentOtherBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment {
    FragmentOtherBinding binding;

    public VideoFragment() {
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
                //控制点，二阶贝塞尔曲线需要三个点，目前只有两个，所以需要新增一个
                Point startPoint = new Point(0, binding.getRoot().getHeight() / 2);
                Point endPoint = new Point(binding.getRoot().getWidth() - binding.tvOther.getWidth(), binding.getRoot().getHeight() / 2);
                int pointX = (startPoint.x + endPoint.x) / 2;
                int pointY = (int) (startPoint.y / 2);
                Point controlPoint = new Point(pointX, pointY);
                BezierEvaluator bezierEvaluator = new BezierEvaluator(controlPoint);
                ValueAnimator anim = ValueAnimator.ofObject(bezierEvaluator, startPoint, endPoint);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        Point point = (Point) valueAnimator.getAnimatedValue();
                        //              设置相对于屏幕的原点
                        binding.tvOther.setX(point.x);
                        binding.tvOther.setY(point.y);
                        binding.tvOther.invalidate();
                    }
                });
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        int x = (binding.getRoot().getWidth() - binding.tvOther.getWidth()) / 2;
                        binding.tvOther.setX(x);
                    }
                });
                anim.setDuration(1500);
                anim.setInterpolator(new AccelerateDecelerateInterpolator());
                anim.start();
            }
        });

    }
}
