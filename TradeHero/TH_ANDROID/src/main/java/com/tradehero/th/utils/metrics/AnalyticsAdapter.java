package com.tradehero.th.utils.metrics;

import com.tradehero.th.utils.metrics.events.AnalyticsEvent;
import java.util.Set;

public interface AnalyticsAdapter
{
    void open(Set<String> customDimensions);

    void addEvent(AnalyticsEvent analyticsEvent);
    void tagScreen(String screenName);

    /** unlike Localytics, this close method will not only close active session but also upload data to tracker server **/
    void close(Set<String> customDimensions);
}
