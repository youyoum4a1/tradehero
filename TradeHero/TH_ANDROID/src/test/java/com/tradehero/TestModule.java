package com.tradehero;

import com.tradehero.common.CommonModule;
import com.ayondo.academy.AppTestModule;
import com.ayondo.academy.base.TestTHApp;
import com.ayondo.academy.models.push.handers.NotificationOpenedHandlerTest;
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
