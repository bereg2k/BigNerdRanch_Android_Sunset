package com.bignerdranch.android.sunset.fragment;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bignerdranch.android.sunset.R;

public class SunsetFragment extends Fragment {
    private static final String TAG = SunsetFragment.class.getSimpleName();

    private View mSceneView;
    private View mSunView;
    private View mSkyView;

    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;

    private float mSunYUp;
    private float mSunYDown;

    private AnimatorSet mAnimatorSet = new AnimatorSet();

    public static SunsetFragment newInstance() {
        return new SunsetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sunset, container, false);

        mSceneView = view;
        mSunView = view.findViewById(R.id.sun);
        mSkyView = view.findViewById(R.id.sky);

        Resources resources = getResources();
        mBlueSkyColor = resources.getColor(R.color.blueSky);
        mSunsetSkyColor = resources.getColor(R.color.sunsetSky);
        mNightSkyColor = resources.getColor(R.color.nightSky);

        mSceneView.setOnClickListener(v -> {
            if (mAnimatorSet.isPaused()) {
                mAnimatorSet.resume();
            } else if (mAnimatorSet.isRunning()) {
                mAnimatorSet.pause();
            } else {
                startSunsetAnimation();
            }
        });

        mSceneView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            mSunYUp = mSunView.getTop();
            mSunYDown = mSkyView.getBottom();
        });

        return view;
    }

    private boolean isSunSet() {
        return mSunView.getY() == mSkyView.getBottom();
    }

    private void startSunsetAnimation() {
        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y",
                        isSunSet() ? mSunYDown : mSunYUp,
                        isSunSet() ? mSunYUp : mSunYDown)
                .setDuration(3000);
        heightAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator sunsetSunriseSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor",
                        isSunSet() ? mSunsetSkyColor : mBlueSkyColor,
                        isSunSet() ? mBlueSkyColor : mSunsetSkyColor)
                .setDuration(3000);
        sunsetSunriseSkyAnimator.setEvaluator(new ArgbEvaluator());

        ObjectAnimator sunsetNightSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor",
                        isSunSet() ? mNightSkyColor : mSunsetSkyColor,
                        isSunSet() ? mSunsetSkyColor : mNightSkyColor)
                .setDuration(1500);
        sunsetNightSkyAnimator.setEvaluator(new ArgbEvaluator());

        mAnimatorSet = new AnimatorSet();

        if (isSunSet()) {
            mAnimatorSet.play(sunsetNightSkyAnimator)
                    .before(sunsetSunriseSkyAnimator)
                    .before(heightAnimator);
        } else {
            mAnimatorSet.play(heightAnimator)
                    .with(sunsetSunriseSkyAnimator)
                    .before(sunsetNightSkyAnimator);
        }

        mAnimatorSet.start();
    }
}
