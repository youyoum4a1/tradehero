package com.tradehero;

import com.tradehero.common.CommonModule;
import com.tradehero.th.base.TestApplication;
import com.tradehero.th.models.push.handers.NotificationOpenedHandlerTest;
import dagger.Module;

@Module(
        includes = {
                CommonModule.class
        },
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
