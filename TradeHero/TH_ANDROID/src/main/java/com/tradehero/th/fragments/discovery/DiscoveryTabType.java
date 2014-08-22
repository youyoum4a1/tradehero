package com.tradehero.th.fragments.discovery;

import android.support.v4.app.Fragment;
import com.tradehero.th.R;

enum DiscoveryTabType
{
    WHAT_HOT(R.string.discovery_whats_hot, WhatsHotFragment.class),
    NEWS(R.string.discovery_news, FeaturedNewsHeadlineFragment.class),
    ACTIVITY(R.string.discovery_activity, DiscoveryActivityFragment.class),
    LEARNING(R.string.discovery_learning, LearningFragment.class);

    public final int titleStringResId;
    public final Class<? extends Fragment> fragmentClass;

    DiscoveryTabType(int titleStringResId, Class<? extends Fragment> fragmentClass)
    {
        this.titleStringResId = titleStringResId;
        this.fragmentClass = fragmentClass;
    }
}
