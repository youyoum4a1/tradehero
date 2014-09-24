package com.tradehero.th;

import com.tradehero.th.api.ApiTestModule;
import com.tradehero.th.auth.AuthenticationTestModule;
import com.tradehero.th.base.BaseTestModule;
import com.tradehero.th.fragments.FragmentAppTestModule;
import com.tradehero.th.models.ModelsTestModule;
import com.tradehero.th.network.NetworkTestModule;
import com.tradehero.th.network.retrofit.RetrofitTestModule;
import com.tradehero.th.persistence.PersistenceTestModule;
import com.tradehero.th.ui.GraphicTestModule;
import com.tradehero.th.utils.UtilsTestModule;
import com.tradehero.th.utils.metrics.MetricsModule;
import com.tradehero.th.widget.WidgetTestModule;
import dagger.Module;

@Module(
        includes = {
                ApiTestModule.class,
                BaseTestModule.class,
                FragmentAppTestModule.class,
                ModelsTestModule.class,
                PersistenceTestModule.class,
                UtilsTestModule.class,
                AuthenticationTestModule.class,
                MetricsModule.class,
                GraphicTestModule.class,
                RetrofitTestModule.class,
                WidgetTestModule.class,
                NetworkTestModule.class,
        },
        complete = false,
        library = true
)
public class AppTestModule
{
}
