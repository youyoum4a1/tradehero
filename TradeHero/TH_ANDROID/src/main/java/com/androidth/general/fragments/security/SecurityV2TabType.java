package com.androidth.general.fragments.security;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import com.androidth.general.R;

public enum SecurityV2TabType
{
    BY_EXCHANGE(R.string.competition_security_v2_by_exchange, ProviderSecurityV2RxByExchangeFragment.class),
    BY_TYPE(R.string.competition_security_v2_by_type, ProviderSecurityV2RxByTypeFragment.class);

    @StringRes public final int titleRes;
    @NonNull public final Class<? extends Fragment> tabClass;

    SecurityV2TabType(@StringRes int titleRes, @NonNull Class<? extends Fragment> tabClass)
    {
        this.titleRes = titleRes;
        this.tabClass = tabClass;
    }
}
