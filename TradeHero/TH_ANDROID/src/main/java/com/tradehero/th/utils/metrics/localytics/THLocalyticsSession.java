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

    @Override public void tagEvent(String event)
    {
        TCAgent.onEvent(context, event);
        super.tagEvent(event);
    }

    public void tagEventMethod(String event, String method)
    {
        this.tagEventCustom(event, LocalyticsConstants.METHOD_MAP_KEY, method);
    }

    public void tagEventType(String event, String type)
    {
        this.tagEventCustom(event, LocalyticsConstants.TYPE_MAP_KEY, type);
    }

    public void tagEventCustom(String event, String key, String type)
    {
        TCAgent.onEvent(context, event, type);
        super.tagEvent(event, Collections.singletonMap(key, type));
    }

    public void tagEventBuySell(boolean isBuy, boolean hasComment, String lastSelectBy,
            boolean shareToFacebook, boolean shareToTwitter, boolean shareToLinkedIn,
            boolean shareToWeChat, boolean shareToWeibo, String symbol, Integer providerId)
    {
        String event = isBuy ? LocalyticsConstants.Trade_Buy : LocalyticsConstants.Trade_Sell;
        //TODO TCAgent need send map
        TCAgent.onEvent(context, event);
        Map<String, String> dic = new HashMap<>();
        dic.put(LocalyticsConstants.HAS_COMMENT_MAP_KEY, hasComment ? "1" : "0");
        dic.put(LocalyticsConstants.LAST_SELECT_BY_MAP_KEY, lastSelectBy);
        dic.put(LocalyticsConstants.SHARE_TO_FACEBOOK_MAP_KEY, shareToFacebook ? "1" : "0");
        dic.put(LocalyticsConstants.SHARE_TO_TWITTER_MAP_KEY, shareToTwitter ? "1" : "0");
        dic.put(LocalyticsConstants.SHARE_TO_LINKEDIN_MAP_KEY, shareToLinkedIn ? "1" : "0");
        dic.put(LocalyticsConstants.SHARE_TO_WECHAT_MAP_KEY, shareToWeChat ? "1" : "0");
        dic.put(LocalyticsConstants.SHARE_TO_WEIBO_MAP_KEY, shareToWeibo ? "1" : "0");
        dic.put(LocalyticsConstants.SECURITY_SYMBOL_MAP_KEY, symbol);
        dic.put(LocalyticsConstants.PROVIDER_ID_MAP_KEY, providerId.toString());
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

    private void populate(Map<String, String> dic, SecurityId securityId)
    {
        if (securityId != null)
        {
            dic.put(LocalyticsConstants.SECURITY_SYMBOL_MAP_KEY,
                    String.format(SECURITY_ID_FORMAT, securityId.getExchange(), securityId.getSecuritySymbol()));
        }
    }

    private List<String> mergeDimensions(List<String> customDimensions)
    {
        List<String> dimensions = new LinkedList<>(customDimensions);
        dimensions.addAll(predefineDimensions);
        return Collections.unmodifiableList(dimensions);
    }
}
