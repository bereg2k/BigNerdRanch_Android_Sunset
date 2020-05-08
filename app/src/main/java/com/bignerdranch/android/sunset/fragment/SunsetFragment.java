package com.bignerdranch.android.sunset.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bignerdranch.android.sunset.R;

public class SunsetFragment extends Fragment {
    private static final String TAG = SunsetFragment.class.getSimpleName();

    private View mSceneView;
    private View mSunView;
    private View mSkyView;
    private View mSeaView;

    private View mSunGlowSmallView;
    private View mSunGlowMiddleView;
    private View mSunGlowLargeView;
    private View mSunReflectionView;

    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;
    private int mDaySeaColor;
    private int mNightSeaColor;
    private int mSunsetSeaColor;

    private float mSunYUp;
    private float mSunYDown;
    private float mSunReflectYUp;
    private float mSunReflectYDown;

    private AnimatorSet mAnimatorSet = new AnimatorSet();
    private AnimatorSet mSunGlowMiddleAnimator = new AnimatorSet();
    private AnimatorSet mSunGlowLargeAnimator = new AnimatorSet();

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
        mSeaView = view.findViewById(R.id.sea);

        mSunGlowSmallView = view.findViewById(R.id.sun_glow_small);
        mSunGlowMiddleView = view.findViewById(R.id.sun_glow_middle);
        mSunGlowLargeView = view.findViewById(R.id.sun_glow_large);
        mSunReflectionView = view.findViewById(R.id.sun_reflection);
        mSunView.bringToFront();

        Resources resources = getResources();
        mBlueSkyColor = resources.getColor(R.color.blueSky);
        mSunsetSkyColor = resources.getColor(R.color.sunsetSky);
        mNightSkyColor = resources.getColor(R.color.nightSky);
        mNightSeaColor = resources.getColor(R.color.nightSea);
        mDaySeaColor = resources.getColor(R.color.sea);
        mSunsetSeaColor = resources.getColor(R.color.sunsetSea);

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

            mSunReflectYUp = mSunReflectionView.getTop();
            mSunReflectYDown = -mSunReflectionView.getHeight();
        });

        startSunPulse();
        startSunGlow();

        return view;
    }

    private boolean isSunSet() {
        return mSunView.getY() == mSkyView.getBottom();
    }

    private void startSunGlow() {
        mSunGlowSmallView.setAlpha(0.1F);
        startSunRays(mSunGlowMiddleView, mSunGlowMiddleAnimator);
        startSunRays(mSunGlowLargeView, mSunGlowLargeAnimator);
    }

    private void stopSunGlow() {
        mSunGlowSmallView.setAlpha(0F);
        mSunGlowMiddleAnimator.end();
        mSunGlowLargeAnimator.end();
    }

    private void startSunPulse() {
        ObjectAnimator sunPulseAnimator = ObjectAnimator
                .ofFloat(mSunView, "alpha", 0.95F, 1, 0.95F)
                .setDuration(2000);
        sunPulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        sunPulseAnimator.start();

        ObjectAnimator sunReflectPulseAnimator = ObjectAnimator
                .ofFloat(mSunReflectionView, "alpha",
                        mSunReflectionView.getAlpha(),
                        mSunReflectionView.getAlpha() + 0.1F,
                        mSunReflectionView.getAlpha())
                .setDuration(4000);
        sunReflectPulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        sunReflectPulseAnimator.start();
    }

    private void startSunRays(View view, AnimatorSet animatorSet) {
        int duration = 4000;

        ObjectAnimator pulseAnimator = ObjectAnimator
                .ofFloat(view, "alpha", 0.9F, 0)
                .setDuration(duration);
        pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);

        ObjectAnimator growAnimatorX = ObjectAnimator
                .ofFloat(view, "scaleX", 0.7F, 1)
                .setDuration(duration);
        growAnimatorX.setRepeatCount(ValueAnimator.INFINITE);

        ObjectAnimator growAnimatorY = ObjectAnimator
                .ofFloat(view, "scaleY", 0.7F, 1)
                .setDuration(duration);
        growAnimatorY.setRepeatCount(ValueAnimator.INFINITE);

        animatorSet
                .play(pulseAnimator)
                .with(growAnimatorX)
                .with(growAnimatorY);
        animatorSet.start();
    }

    private void startSunsetAnimation() {
        int duration = 3000;

        ObjectAnimator sunHeightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y",
                        isSunSet() ? mSunYDown : mSunYUp,
                        isSunSet() ? mSunYUp : mSunYDown)
                .setDuration(duration);
        sunHeightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator sunReflectHeightAnimator = ObjectAnimator
                .ofFloat(mSunReflectionView, "y",
                        isSunSet() ? mSunReflectYDown : mSunReflectYUp,
                        isSunSet() ? mSunReflectYUp : mSunReflectYDown)
                .setDuration(duration);
        sunReflectHeightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator sunReflectScaleAnimator = ObjectAnimator
                .ofFloat(mSunReflectionView, "scaleY",
                        isSunSet() ? 1 : 0.8F,
                        isSunSet() ? 0.8F : 1)
                .setDuration(duration);

        ObjectAnimator sunsetSunriseSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor",
                        isSunSet() ? mSunsetSkyColor : mBlueSkyColor,
                        isSunSet() ? mBlueSkyColor : mSunsetSkyColor)
                .setDuration(duration);
        sunsetSunriseSkyAnimator.setEvaluator(new ArgbEvaluator());

        ObjectAnimator sunsetNightSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor",
                        isSunSet() ? mNightSkyColor : mSunsetSkyColor,
                        isSunSet() ? mSunsetSkyColor : mNightSkyColor)
                .setDuration(duration / 2);
        sunsetNightSkyAnimator.setEvaluator(new ArgbEvaluator());

        ObjectAnimator beforeSunsetSeaAnimator = ObjectAnimator
                .ofInt(mSeaView, "backgroundColor",
                        mDaySeaColor, mSunsetSeaColor, mDaySeaColor)
                .setDuration(duration);
        beforeSunsetSeaAnimator.setEvaluator(new ArgbEvaluator());

        ObjectAnimator sunsetNightSeaAnimator = ObjectAnimator
                .ofInt(mSeaView, "backgroundColor",
                        isSunSet() ? mNightSeaColor : mDaySeaColor,
                        isSunSet() ? mDaySeaColor : mNightSeaColor)
                .setDuration(duration / 2);
        sunsetNightSeaAnimator.setEvaluator(new ArgbEvaluator());

        mAnimatorSet = new AnimatorSet();

        if (isSunSet()) {
            mAnimatorSet.play(sunsetNightSkyAnimator)
                    .with(sunsetNightSeaAnimator)
                    .before(sunsetSunriseSkyAnimator)
                    .before(sunHeightAnimator)
                    .before(sunReflectHeightAnimator)
                    .before(sunReflectScaleAnimator)
                    .before(beforeSunsetSeaAnimator);
        } else {
            mAnimatorSet.play(sunHeightAnimator)
                    .with(sunReflectHeightAnimator)
                    .with(sunReflectScaleAnimator)
                    .with(sunsetSunriseSkyAnimator)
                    .with(beforeSunsetSeaAnimator)
                    .before(sunsetNightSkyAnimator)
                    .before(sunsetNightSeaAnimator);
        }

        mAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                stopSunGlow();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isSunSet()) {
                    startSunGlow();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        mAnimatorSet.start();
    }
}
