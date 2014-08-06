package com.tradehero.th.utils.metrics.events;

import com.tradehero.th.fragments.trending.filter.TrendingFilterTypeDTO;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import java.util.Map;

public class TrendingFilterEvent extends AnalyticsEvent
{
    static final String TRENDING_FILTER_CATEGORY_MAP_KEY = "category";

    private final TrendingFilterTypeDTO trendingFilterTypeDTO;

    public TrendingFilterEvent(TrendingFilterTypeDTO trendingFilterTypeDTO)
    {
        super(AnalyticsConstants.TabBar_Trending);
        this.trendingFilterTypeDTO = trendingFilterTypeDTO;
    }

    @Override public Map<String, String> getAttributes()
    {
        Map<String, String> attributes = super.getAttributes();
        if (attributes != null)
        {
            attributes.put(TRENDING_FILTER_CATEGORY_MAP_KEY, trendingFilterTypeDTO.getTrackEventCategory());
        }
        return attributes;
    }
}
