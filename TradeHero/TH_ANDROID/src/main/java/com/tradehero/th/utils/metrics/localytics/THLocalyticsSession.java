package com.tradehero.th.utils.metrics.localytics;

import android.content.Context;
import com.localytics.android.LocalyticsSession;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.trending.filter.TrendingFilterTypeDTO;
import com.tradehero.th.models.chart.ChartTimeSpan;
import com.tradehero.th.models.chart.ChartTimeSpanMetricsCodeFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xavier on 2014/4/1.
 */
public class THLocalyticsSession extends LocalyticsSession
{
    public static final String SECURITY_ID_FORMAT = "%s:%s";

    protected ChartTimeSpanMetricsCodeFactory chartTimeSpanMetricsCodeFactory;

    //<editor-fold desc="Constructors">
    public THLocalyticsSession(Context context, ChartTimeSpanMetricsCodeFactory chartTimeSpanMetricsCodeFactory)
    {
        super(context);
        this.chartTimeSpanMetricsCodeFactory = chartTimeSpanMetricsCodeFactory;
    }

    public THLocalyticsSession(Context context, String key, ChartTimeSpanMetricsCodeFactory chartTimeSpanMetricsCodeFactory)
    {
        super(context, key);
        this.chartTimeSpanMetricsCodeFactory = chartTimeSpanMetricsCodeFactory;
    }
    //</editor-fold>

    public void tagEvent(String event, SecurityId securityId)
    {
        Map<String, String> dic = new HashMap<>();
        populate(dic, securityId);
        super.tagEvent(event, dic);
    }

    public void tagEvent(String event, ChartTimeSpan chartTimeSpan, SecurityId securityId)
    {
        Map<String, String> dic = new HashMap<>();
        populate(dic, securityId);
        super.tagEvent(String.format(event, chartTimeSpanMetricsCodeFactory.createCode(chartTimeSpan)), dic);
    }

    private void populate(Map<String, String> dic, SecurityId securityId)
    {
        if (securityId != null)
        {
            dic.put(LocalyticsConstants.SECURITY_SYMBOL_MAP_KEY, String.format(SECURITY_ID_FORMAT, securityId.exchange, securityId.securitySymbol));
        }
    }

    public void tagEvent(String event, TrendingFilterTypeDTO trendingFilterTypeDTO)
    {
        Map<String, String> dic = new HashMap<>();
        populate(dic, trendingFilterTypeDTO);
        super.tagEvent(event, dic);
    }

    private void populate(Map<String, String> dic, TrendingFilterTypeDTO trendingFilterTypeDTO)
    {
        if (trendingFilterTypeDTO != null)
        {
            dic.put(LocalyticsConstants.TRENDING_FILTER_CATEGORY_MAP_KEY, trendingFilterTypeDTO.getTrackEventCategory());
        }
    }
}
