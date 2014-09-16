package com.tradehero;

import android.app.Activity;
import com.tradehero.common.CommonModule;
import com.tradehero.th.AppTestModule;
import com.tradehero.th.UIModule;
import com.tradehero.th.base.TestTHApp;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.models.push.handers.NotificationOpenedHandlerTest;
import com.tradehero.th.utils.route.THRouter;
import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                CommonModule.class,
                AppTestModule.class,
                UIModule.class, // Cheating
                FlavorAppTestModule.class,
        },
        injects = {
                TestTHApp.class,
                NotificationOpenedHandlerTest.class,
                SampleTest.class,
        },
        complete = false,
        library = true,
        overrides = true
)
public class TestModule
{
    //@Provides Activity dummyProvideActivity()
    //{
    //    throw new IllegalStateException("You should override this provider");
    //}
    //
    //@Provides DashboardNavigator dummyProvideDashboardNavigator()
    //{
    //    throw new IllegalStateException("You should override this provider");
    //}
    //
    //@Provides THRouter dummyProvideTHRouter()
    //{
    //    throw new IllegalStateException("You should override this provider");
    //}
}
