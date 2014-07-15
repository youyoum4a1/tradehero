package com.tradehero.th.utils.metrics.localytics;

import android.content.Context;
import com.localytics.android.LocalyticsSession;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.AnalyticsEvent;
import java.util.Collections;
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
    @NotNull private final List<String> predefineDimensions;

    //<editor-fold desc="Constructors">
    @Inject public THLocalyticsSession(
            @NotNull Context context,
            @NotNull @ForLocalytics String appKey,
            @NotNull @ForLocalytics List<String> predefineDimensions)
    {
        super(context, appKey);
        this.context = context;
        this.predefineDimensions = predefineDimensions;
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
        super.tagEvent(event);
    }

    @Override public void tagEvent(String event, Map<String, String> attributes)
    {
        super.tagEvent(event, attributes);
    }

    private List<String> mergeDimensions(List<String> customDimensions)
    {
        List<String> dimensions = new LinkedList<>(customDimensions);
        dimensions.addAll(predefineDimensions);
        return Collections.unmodifiableList(dimensions);
    }

    private void tagAnalyticsEvent(AnalyticsEvent analyticsEvent)
    {
        tagEvent(analyticsEvent.getName(), analyticsEvent.getAttributes());
    }

    public void tagEventMethod(String event, String method)
    {
        this.tagSingleEvent(event, AnalyticsConstants.METHOD_MAP_KEY, method);
    }

    public void tagEventType(String event, String type)
    {
        this.tagSingleEvent(event, AnalyticsConstants.TYPE_MAP_KEY, type);
    }

    public void tagSingleEvent(String event, String key, String type)
    {
        super.tagEvent(event, Collections.singletonMap(key, type));
    }
}
