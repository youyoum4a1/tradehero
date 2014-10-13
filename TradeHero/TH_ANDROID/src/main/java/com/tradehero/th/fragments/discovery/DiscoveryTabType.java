package com.tradehero.th.fragments.discovery;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import com.tradehero.th.R;
import org.jetbrains.annotations.NotNull;

enum DiscoveryTabType
{
    NEWS(R.string.discovery_news, NewsPagerFragment.class),
    DISCUSSION(R.string.discovery_discussions, DiscoveryDiscussionFragment.class),
    LEARNING(R.string.discovery_learning, LearningFragment.class);

    @StringRes public final int titleStringResId;
    @NotNull public final Class<? extends Fragment> fragmentClass;

    DiscoveryTabType(@StringRes int titleStringResId,
            @NotNull Class<? extends Fragment> fragmentClass)
    {
        this.titleStringResId = titleStringResId;
        this.fragmentClass = fragmentClass;
    }
}
