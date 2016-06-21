package com.androidth.general.fragments.security;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import com.androidth.general.R;
import com.androidth.general.fragments.contestcenter.ContestCenterActiveFragment;
import com.androidth.general.fragments.contestcenter.ContestCenterJoinedFragment;

public enum SecurityV2TabType
{

    @StringRes public final int titleRes;
    @NonNull public final Class<? extends Fragment> tabClass;

    private SecurityV2TabType(@StringRes int titleRes, @NonNull Class<? extends Fragment> tabClass)
    {
        this.titleRes = titleRes;
        this.tabClass = tabClass;
    }
}
