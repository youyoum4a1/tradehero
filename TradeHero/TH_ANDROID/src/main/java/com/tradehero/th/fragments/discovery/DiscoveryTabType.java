package com.tradehero.th.fragments.discovery;

import com.tradehero.th.R;

enum DiscoveryTabType
{
    WHAT_HOT(R.string.discovery_whats_hot),
    NEWS(R.string.discovery_news),
    ACTIVITY(R.string.discovery_activity);

    public final int titleStringResId;

    DiscoveryTabType(int titleStringResId)
    {
        this.titleStringResId = titleStringResId;
    }
}
