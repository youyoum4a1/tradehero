package com.tradehero.th.utils.metrics;

import com.tradehero.th.utils.metrics.events.AnalyticsEvent;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Analytics
{
    private final Set<Action> pendingActions = new LinkedHashSet<>();
    private final Set<AnalyticsAdapter> analyticsAdapters;
    private final Set<String> builtinDimensions;

    @Inject public Analytics(Set<AnalyticsAdapter> analyticsAdapters, @ForAnalytics Set<String> builtinDimensions)
    {
        this.analyticsAdapters = analyticsAdapters;
        this.builtinDimensions = builtinDimensions;
    }

    public final Analytics addEvent(AnalyticsEvent analyticsEvent)
    {
        pendingActions.add(new AddEventAction(analyticsEvent));
        return this;
    }

    public final Analytics tagScreen(String screenName)
    {
        pendingActions.add(new TagScreenAction(screenName));
        return this;
    }

    public final void fire()
    {
        doPendingActions();
    }

    public final void fireEvent(AnalyticsEvent analyticsEvent)
    {
        // TODO should create a policy for deciding whether to discard or to process pending action
        discardPendingActions();

        openSession();
        doAction(new AddEventAction(analyticsEvent));
        closeSession();
    }

    public void openSession()
    {
        openSession(null);
    }

    public final void openSession(Set<String> customDimensions)
    {
        doAction(new OpenSessionAction(customDimensions));
    }

    public void closeSession()
    {
        closeSession(null);
    }

    public void closeSession(Set<String> customDimensions)
    {
        if (!pendingActions.isEmpty())
        {
            doPendingActions();
        }
        doAction(new CloseSessionAction(customDimensions));
    }

    private void discardPendingActions()
    {
        pendingActions.clear();
    }

    private void doPendingActions()
    {
        for (Action action: pendingActions)
        {
            doAction(action);
        }
        closeSession();
    }

    /** Functional programming can cure this pain **/
    private void doAction(Action action)
    {
        for (AnalyticsAdapter handler: analyticsAdapters)
        {
            action.setHandler(handler);
            action.process();
        }
    }

    //region Action classes
    private interface Action
    {
        void process();

        void setHandler(AnalyticsAdapter handler);
    }

    private abstract class HandlerAction
        implements  Action
    {
        protected AnalyticsAdapter handler;

        @Override public void setHandler(AnalyticsAdapter handler)
        {
            this.handler = handler;
        }
    }

    private final class AddEventAction extends HandlerAction
    {
        private final AnalyticsEvent analyticsEvent;

        public AddEventAction(AnalyticsEvent analyticsEvent)
        {
            this.analyticsEvent = analyticsEvent;
        }

        @Override public void process()
        {
            handler.addEvent(analyticsEvent);
        }
    }

    private abstract class HandlerActionWithDimensions extends HandlerAction
    {
        protected final Set<String> customDimensions;

        public HandlerActionWithDimensions(Set<String> customDimensions)
        {
            HashSet<String> dimensions = new HashSet<>(builtinDimensions);
            if (customDimensions != null)
            {
                dimensions.addAll(customDimensions);
            }
            this.customDimensions = dimensions;
        }
    }

    private final class OpenSessionAction extends HandlerActionWithDimensions
    {

        public OpenSessionAction(Set<String> customDimensions)
        {
            super(customDimensions);
        }

        @Override public void process()
        {
            handler.open(customDimensions);
        }
    }

    private final class CloseSessionAction extends HandlerActionWithDimensions
    {
        public CloseSessionAction(Set<String> customDimensions)
        {
            super(customDimensions);
        }

        @Override public void process()
        {
            handler.close(builtinDimensions);
        }
    }

    private class TagScreenAction extends HandlerAction
    {
        private final String screenName;

        public TagScreenAction(String screenName)
        {
            this.screenName = screenName;
        }

        @Override public void process()
        {
            handler.tagScreen(screenName);
        }
    }
    //endregion
}
