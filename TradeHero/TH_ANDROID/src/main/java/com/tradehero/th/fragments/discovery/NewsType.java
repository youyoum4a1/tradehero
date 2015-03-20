package com.tradehero.th.fragments.discovery;

import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.tradehero.th.R;
import com.tradehero.th.utils.metrics.AnalyticsConstants;

enum NewsType
{

    SeekingAlpha(R.string.seeking_alpha, R.layout.news_carousel_seekingalpha, R.color.news_seeking_alpha, AnalyticsConstants.NewsSeekingAlpha),
    MotleyFool(R.string.motley_fool, R.layout.news_carousel_motleyfool, R.color.news_motley_fool, AnalyticsConstants.NewsMotleyFool),
    Region(R.string.regional_news, R.layout.news_carousel_regional, R.color.news_regional, AnalyticsConstants.NewsRegional),
    Global(R.string.global_news, R.layout.news_carousel_global, R.color.news_global, AnalyticsConstants.NewsGlobal);

    @StringRes public final int titleResourceId;
    @LayoutRes public final int titleViewResourceId;
    @ColorRes  public final int colorResourceId;
    @NonNull public final String analyticsName;


    NewsType(@StringRes int titleResourceId, @LayoutRes int titleViewResourceId, @ColorRes int colorResourceId, @NonNull String analyticsName)
    {
        this.titleResourceId = titleResourceId;
        this.titleViewResourceId = titleViewResourceId;
        this.colorResourceId = colorResourceId;
        this.analyticsName = analyticsName;
    }
}
