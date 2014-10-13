package com.tradehero.th.fragments.discovery;

import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import com.tradehero.th.R;

enum NewsType
{
    MotleyFool(R.string.motley_fool, R.layout.news_carousel_motleyfool),
    Region(R.string.regional_news, R.layout.news_carousel_regional),
    Global(R.string.global_news, R.layout.news_carousel_global);

    @StringRes public final int titleResourceId;
    @LayoutRes public final int titleViewResourceId;

    NewsType(@StringRes int titleResourceId, @LayoutRes int titleViewResourceId)
    {
        this.titleResourceId = titleResourceId;
        this.titleViewResourceId = titleViewResourceId;
    }
}
