package com.bignerdranch.android.sunset.activity;

import androidx.fragment.app.Fragment;

import com.bignerdranch.android.sunset.fragment.SunsetFragment;

public class SunsetActivity extends SingleFragmentActivity {
    private static final String TAG = SunsetActivity.class.getSimpleName();

    @Override
    public Fragment createFragment() {
        return SunsetFragment.newInstance();
    }
}
