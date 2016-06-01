package com.ayondo.academy;

import com.ayondo.academy.api.ApiTestModule;
import com.ayondo.academy.auth.AuthenticationTestModule;
import com.ayondo.academy.base.BaseTestModule;
import com.ayondo.academy.fragments.FragmentAppTestModule;
import com.ayondo.academy.models.ModelsTestModule;
import com.ayondo.academy.network.NetworkTestModule;
import com.ayondo.academy.network.retrofit.RetrofitTestModule;
import com.ayondo.academy.persistence.PersistenceTestModule;
import com.ayondo.academy.ui.GraphicTestModule;
import com.ayondo.academy.utils.UtilsTestModule;
import com.ayondo.academy.utils.metrics.MetricsModule;
import com.ayondo.academy.widget.WidgetTestModule;
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
