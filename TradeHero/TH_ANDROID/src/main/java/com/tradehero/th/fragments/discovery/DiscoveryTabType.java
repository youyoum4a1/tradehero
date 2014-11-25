package com.tradehero.th.fragments.discovery;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import com.tradehero.th.R;

enum DiscoveryTabType
{
    NEWS(R.string.discovery_news, NewsPagerFragment.class),
    DISCUSSION(R.string.discovery_discussions, DiscoveryDiscussionFragment.class),
    LEARNING(R.string.discovery_learning, LearningFragment.class),
    GAMES(R.string.discovery_games, DiscoveryGameFragment.class),
    ARTICLE(R.string.discovery_articles, DiscoveryArticleFragment.class),
    ;

    @StringRes public final int titleStringResId;
    @NonNull public final Class<? extends Fragment> fragmentClass;

    DiscoveryTabType(@StringRes int titleStringResId, @NonNull Class<? extends Fragment> fragmentClass)
    {
        this.titleStringResId = titleStringResId;
        this.fragmentClass = fragmentClass;
    }
}
