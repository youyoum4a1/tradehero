package com.tradehero.th.fragments.trade;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionFragment;

enum FXMainTabType
{
    INFO(R.string.security_info, BuySellFXFragment.class, false),
    DISCUSSION(R.string.discovery_discussions, SecurityDiscussionFragment.class, false),
    //NEWS(R.string.security_news, NewsHeadlineFragment.class, false),
    //HISTORY(R.string.security_history, DiscoveryFaqWebFragment.class, false),
    ;

    @StringRes public final int titleStringResId;
    @NonNull public final Class<? extends Fragment> fragmentClass;
    public final boolean isNew;

    FXMainTabType(@StringRes int titleStringResId, @NonNull Class<? extends Fragment> fragmentClass, boolean isNew)
    {
        this.titleStringResId = titleStringResId;
        this.fragmentClass = fragmentClass;
        this.isNew = isNew;
    }
}
