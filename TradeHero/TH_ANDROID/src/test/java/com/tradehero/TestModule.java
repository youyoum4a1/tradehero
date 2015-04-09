package com.tradehero;

import com.tradehero.common.CommonModule;
import com.tradehero.th.AppTestModule;
import com.tradehero.th.base.TestTHApp;
import com.tradehero.th.models.push.handers.NotificationOpenedHandlerTest;
import dagger.Module;

@Module(
        includes = {
                CommonModule.class,
                AppTestModule.class,
                FlavorAppTestModule.class,
        },
        injects = {
                TestTHApp.class,
                NotificationOpenedHandlerTest.class,
        },
        complete = false,
        library = true,
        overrides = true
)
public class TestModule
{
}
