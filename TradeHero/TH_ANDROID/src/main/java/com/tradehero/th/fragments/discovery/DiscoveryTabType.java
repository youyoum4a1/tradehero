package com.tradehero.th.fragments.discovery;

import android.support.v4.app.Fragment;
import com.tradehero.th.R;

enum DiscoveryTabType
{
    NEWS(R.string.discovery_news, NewsPagerFragment.class),
    DISCUSSION(R.string.discovery_discussions, DiscoveryDiscussionFragment.class),
    LEARNING(R.string.discovery_learning, LearningFragment.class);

    public final int titleStringResId;
    public final Class<? extends Fragment> fragmentClass;

    DiscoveryTabType(int titleStringResId, Class<? extends Fragment> fragmentClass)
    {
        this.titleStringResId = titleStringResId;
        this.fragmentClass = fragmentClass;
    }
}
