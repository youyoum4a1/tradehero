package com.tradehero.th.fragments.discovery;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.fragments.education.VideoCategoriesFragment;

enum DiscoveryTabType
{
    NEWS(R.string.discovery_news, NewsPagerFragment.class, false),
    DISCUSSION(R.string.discovery_discussions, DiscoveryDiscussionFragment.class, false),
    LEARNING(R.string.discovery_learning, VideoCategoriesFragment.class, false),
    GAMES(R.string.discovery_games, DiscoveryGameFragment.class, true),
    ARTICLE(R.string.discovery_articles, DiscoveryArticleFragment.class, true),
    ;

    @StringRes public final int titleStringResId;
    @NonNull public final Class<? extends Fragment> fragmentClass;
    public final boolean isNew;

    DiscoveryTabType(@StringRes int titleStringResId, @NonNull Class<? extends Fragment> fragmentClass, boolean isNew)
    {
        this.titleStringResId = titleStringResId;
        this.fragmentClass = fragmentClass;
        this.isNew = isNew;
    }
}
