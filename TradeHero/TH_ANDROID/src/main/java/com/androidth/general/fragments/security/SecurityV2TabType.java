package com.androidth.general.fragments.security;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import com.androidth.general.R;
import com.androidth.general.fragments.contestcenter.ContestCenterActiveFragment;
import com.androidth.general.fragments.contestcenter.ContestCenterJoinedFragment;

public enum SecurityV2TabType
{
    BY_EXCHANGE(R.string.competition_security_v2_by_exchange, ProviderSecurityV2RxByExchangeFragment.class),
    BY_TYPE(R.string.competition_security_v2_by_type, ProviderSecurityV2RxByExchangeFragment.class);

    @StringRes public final int titleRes;
    @NonNull public final Class<? extends Fragment> tabClass;

    private SecurityV2TabType(@StringRes int titleRes, @NonNull Class<? extends Fragment> tabClass)
    {
        this.titleRes = titleRes;
        this.tabClass = tabClass;
    }
}
