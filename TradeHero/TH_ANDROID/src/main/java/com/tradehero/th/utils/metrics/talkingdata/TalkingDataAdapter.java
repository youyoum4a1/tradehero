package com.tradehero.th.utils.metrics.talkingdata;

import android.content.Context;
import com.tendcloud.tenddata.TCAgent;
import com.tradehero.th.utils.metrics.AnalyticsAdapter;
import com.tradehero.th.utils.metrics.events.AnalyticsEvent;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TalkingDataAdapter
    implements AnalyticsAdapter
{
    private final Context context;

    @Inject public TalkingDataAdapter(Context context)
    {
        this.context = context;
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
        /** second string is a tag **/
        TCAgent.onEvent(context, analyticsEvent.getName(), "", analyticsEvent.getAttributes());
    }

    @Override public void tagScreen(String screenName)
    {
        // TODO
    }
}
