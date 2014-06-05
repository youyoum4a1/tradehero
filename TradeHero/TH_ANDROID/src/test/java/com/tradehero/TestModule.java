package com.tradehero;

import com.tradehero.base.TestApplication;
import com.tradehero.th.models.push.handers.NotificationOpenedHandlerTest;
import dagger.Module;

@Module(
        injects = {
                TestApplication.class,
                NotificationOpenedHandlerTest.class,
                SampleTest.class,
        },
        complete = false,
        library = true,
        overrides = true
)
public class TestModule
{
}
