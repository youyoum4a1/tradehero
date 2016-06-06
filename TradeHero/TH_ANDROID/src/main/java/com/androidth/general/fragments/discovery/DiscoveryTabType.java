package com.androidth.general.fragments.discovery;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import com.androidth.general.R;
import com.androidth.general.fragments.discovery.newsfeed.DiscoveryNewsfeedFragment;
import com.androidth.general.fragments.education.VideoCategoriesFragment;

enum DiscoveryTabType
{
    NEWSFEED(R.string.discovery_newsfeed, DiscoveryNewsfeedFragment.class),
    DISCUSSION(R.string.discovery_discussions, DiscoveryDiscussionFragment.class),
    ACADEMY(R.string.discovery_learning, VideoCategoriesFragment.class);

    //NEWS(R.string.discovery_news, NewsPagerFragment.class) TODO Remove this class

    @StringRes public final int titleStringResId;
    @NonNull public final Class<? extends Fragment> fragmentClass;

    DiscoveryTabType(@StringRes int titleStringResId, @NonNull Class<? extends Fragment> fragmentClass)
    {
        this.titleStringResId = titleStringResId;
        this.fragmentClass = fragmentClass;
    }
}
