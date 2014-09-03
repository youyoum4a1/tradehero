package com.tradehero.th.fragments.discovery;

import com.tradehero.th.R;

enum NewsType
{
    Region(R.string.regional_news, R.layout.news_carousel_regional),
    MotleyFool(R.string.motley_fool, R.layout.news_carousel_motleyfool),
    Global(R.string.global_news, R.layout.news_carousel_global);

    public final int titleResourceId;
    public final int titleViewResourceId;

    NewsType(int titleResourceId, int titleViewResourceId)
    {
        this.titleResourceId = titleResourceId;
        this.titleViewResourceId = titleViewResourceId;
    }
}
