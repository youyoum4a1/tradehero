package com.tradehero.th.fragments.discovery;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.fragments.discovery.newsfeed.DiscoveryNewsfeedFragment;
import com.tradehero.th.fragments.education.VideoCategoriesFragment;

enum DiscoveryTabType
{
    NEWS(R.string.discovery_news, NewsPagerFragment.class),
    DISCUSSION(R.string.discovery_discussions, DiscoveryDiscussionFragment.class),
    ACADEMY(R.string.discovery_learning, VideoCategoriesFragment.class),
    NEW(R.string.discovery, DiscoveryNewsfeedFragment.class);

    @StringRes public final int titleStringResId;
    @NonNull public final Class<? extends Fragment> fragmentClass;

    DiscoveryTabType(@StringRes int titleStringResId, @NonNull Class<? extends Fragment> fragmentClass)
    {
        this.titleStringResId = titleStringResId;
        this.fragmentClass = fragmentClass;
    }
}
