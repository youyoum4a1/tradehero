package com.tradehero.th.utils.metrics.talkingdata;

import com.tradehero.th.utils.metrics.AnalyticsAdapter;
import com.tradehero.th.utils.metrics.events.AnalyticsEvent;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TalkingDataAdapter
    implements AnalyticsAdapter
{
    @Inject public TalkingDataAdapter()
    {
        super();
    }

    @Override public void open(Set<String> customDimensions)
    {
        // TODO
    }

    @Override public void close(Set<String> customDimensions)
    {
        // TODO
    }

    @Override public void addEvent(AnalyticsEvent analyticsEvent)
    {
        // TODO
    }

    @Override public void tagScreen(String screenName)
    {
        // TODO
    }
}
