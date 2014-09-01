package com.tradehero.th.fragments.discovery;

import com.tradehero.th.R;

enum NewsType
{
    Region(R.string.regional_news),
    MotleyFool(R.string.motley_fool),
    Global(R.string.global_news);

    public final int titleResourceId;

    NewsType(int titleResourceId)
    {
        this.titleResourceId = titleResourceId;
    }
}
