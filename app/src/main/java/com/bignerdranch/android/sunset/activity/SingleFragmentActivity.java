package com.bignerdranch.android.sunset.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bignerdranch.android.sunset.R;

public abstract class SingleFragmentActivity extends AppCompatActivity {
    private static final String TAG = SingleFragmentActivity.class.getSimpleName();

    @LayoutRes
    public static final int ACTIVITY_LAYOUT = R.layout.activity_fragment;
    @IdRes
    public static final int FRAGMENT_CONTAINER = R.id.fragment_container;

    public abstract Fragment createFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ACTIVITY_LAYOUT);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(FRAGMENT_CONTAINER);

        if (fragment == null) {
            fragment = createFragment();

            fm.beginTransaction()
                    .add(FRAGMENT_CONTAINER, fragment)
                    .commit();

            Log.i(TAG, "onCreate: fragment has been posted - " + fragment.toString());
        }
    }
}
