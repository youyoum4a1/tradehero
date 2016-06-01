package com.ayondo.academy.models.push.handers;

import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.models.push.handlers.NotificationOpenedHandler;
import javax.inject.Inject;
import javax.inject.Provider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class NotificationOpenedHandlerTest
{
    @Inject Provider<NotificationOpenedHandler> notificationOpenedHandlerProvider;

    @Before public void setUp()
    {
    }

    @Test public void handleNotificationOpenedTest()
    {
    }
}
