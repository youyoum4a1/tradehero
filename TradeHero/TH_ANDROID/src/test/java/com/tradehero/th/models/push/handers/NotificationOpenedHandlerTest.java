package com.tradehero.th.models.push.handers;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.models.push.handlers.NotificationOpenedHandler;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import javax.inject.Provider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(RobolectricMavenTestRunner.class)
public class NotificationOpenedHandlerTest
{
    @Inject Provider<NotificationOpenedHandler> notificationOpenedHandlerProvider;

    @Before public void setUp()
    {
        DaggerUtils.inject(this);
    }

    @Test public void handleNotificationOpenedTest()
    {
    }
}
