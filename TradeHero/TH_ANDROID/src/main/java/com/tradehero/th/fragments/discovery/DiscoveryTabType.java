package com.tradehero.th.fragments.discovery;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import com.tradehero.th.R;

enum DiscoveryTabType
{
    NEWS(R.string.discovery_news, NewsPagerFragment.class, false),
    DISCUSSION(R.string.discovery_discussions, DiscoveryDiscussionFragment.class, true),
    LEARNING(R.string.discovery_learning, LearningFragment.class, false),
    GAMES(R.string.discovery_games, DiscoveryGamesFragment.class, false);

    @StringRes public final int titleStringResId;
    @NonNull public final Class<? extends Fragment> fragmentClass;
    public final boolean showComment;

    DiscoveryTabType(@StringRes int titleStringResId,
            @NonNull Class<? extends Fragment> fragmentClass,
            boolean showComment)
    {
        this.titleStringResId = titleStringResId;
        this.fragmentClass = fragmentClass;
        this.showComment = showComment;
    }
}
