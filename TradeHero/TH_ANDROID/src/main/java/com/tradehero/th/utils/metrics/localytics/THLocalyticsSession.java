package com.tradehero.th.utils.metrics.localytics;

import android.content.Context;
import com.localytics.android.LocalyticsSession;
import com.tendcloud.tenddata.TCAgent;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.trending.filter.TrendingFilterTypeDTO;
import com.tradehero.th.models.chart.ChartTimeSpan;
import com.tradehero.th.models.chart.ChartTimeSpanMetricsCodeFactory;
import com.tradehero.th.utils.Constants;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class THLocalyticsSession extends LocalyticsSession
{
    public static final String SECURITY_ID_FORMAT = "%s:%s";
    @NotNull final Context context;
    @NotNull protected final ChartTimeSpanMetricsCodeFactory chartTimeSpanMetricsCodeFactory;

    //<editor-fold desc="Constructors">
    @Inject public THLocalyticsSession(
            @NotNull Context context,
            @NotNull ChartTimeSpanMetricsCodeFactory chartTimeSpanMetricsCodeFactory)
    {
        super(context);
        this.context = context;
        this.chartTimeSpanMetricsCodeFactory = chartTimeSpanMetricsCodeFactory;
    }

    public THLocalyticsSession(
            @NotNull Context context,
            String key,
            @NotNull ChartTimeSpanMetricsCodeFactory chartTimeSpanMetricsCodeFactory)
    {
        super(context, key);
        this.context = context;
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

    public void tagEvent(String event)
    {
        TCAgent.onEvent(context, event);
        Map<String, String> dic = new HashMap<>();
        dic.put(LocalyticsConstants.CHANNEL_MAP_KEY, Constants.TAP_STREAM_TYPE.name());
        super.tagEvent(event, dic);
    }
}
