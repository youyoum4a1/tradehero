package com.tradehero.th.utils.metrics.localytics;

import android.content.Context;
import com.localytics.android.LocalyticsSession;
import com.tendcloud.tenddata.TCAgent;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.trending.filter.TrendingFilterTypeDTO;
import com.tradehero.th.models.chart.ChartTimeSpan;
import com.tradehero.th.models.chart.ChartTimeSpanMetricsCodeFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class THLocalyticsSession extends LocalyticsSession
{
    public static final String SECURITY_ID_FORMAT = "%s:%s";

    @NotNull private final Context context;
    @NotNull protected final ChartTimeSpanMetricsCodeFactory chartTimeSpanMetricsCodeFactory;
    @NotNull private final List<String> predefineDimensions;

    //<editor-fold desc="Constructors">
    @Inject public THLocalyticsSession(
            @NotNull Context context,
            @NotNull @ForLocalytics String appKey,
            @NotNull @ForLocalytics List<String> predefineDimensions,
            @NotNull ChartTimeSpanMetricsCodeFactory chartTimeSpanMetricsCodeFactory)
    {
        super(context, appKey);
        this.context = context;
        this.predefineDimensions = predefineDimensions;
        this.chartTimeSpanMetricsCodeFactory = chartTimeSpanMetricsCodeFactory;
    }
    //</editor-fold>

    @Override public void open()
    {
        super.open(predefineDimensions);
    }

    @Override public void close()
    {
        super.close(predefineDimensions);
    }

    @Override public void open(List<String> customDimensions)
    {
        super.open(mergeDimensions(customDimensions));
    }

    @Override public void close(List<String> customDimensions)
    {
        super.close(mergeDimensions(customDimensions));
    }

    private List<String> mergeDimensions(List<String> customDimensions)
    {
        List<String> dimensions = new LinkedList<>(customDimensions);
        dimensions.addAll(predefineDimensions);
        return Collections.unmodifiableList(dimensions);
    }

    public void tagEvent(String event)
    {
        TCAgent.onEvent(context, event);
        super.tagEvent(event);
    }

    public void tagEventMethod(String event, String method)
    {
        TCAgent.onEvent(context, event, method);
        Map<String, String> dic = new HashMap<>();
        dic.put(LocalyticsConstants.METHOD_MAP_KEY, method);
        super.tagEvent(event, dic);
    }

    public void tagEventType(String event, String type)
    {
        TCAgent.onEvent(context, event, type);
        Map<String, String> dic = new HashMap<>();
        dic.put(LocalyticsConstants.TYPE_MAP_KEY, type);
        super.tagEvent(event, dic);
    }

    public void tagEventCustom(String event, String key, String type)
    {
        TCAgent.onEvent(context, event, type);
        Map<String, String> dic = new HashMap<>();
        dic.put(key, type);
        super.tagEvent(event, dic);
    }

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
            dic.put(LocalyticsConstants.SECURITY_SYMBOL_MAP_KEY,
                    String.format(SECURITY_ID_FORMAT, securityId.getExchange(), securityId.getSecuritySymbol()));
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
