package com.tradehero.th;

import com.tradehero.th.activities.ActivityTestModule;
import com.tradehero.th.api.ApiTestModule;
import com.tradehero.th.auth.AuthenticationTestModule;
import com.tradehero.th.fragments.FragmentTestModule;
import com.tradehero.th.models.ModelsTestModule;
import com.tradehero.th.network.retrofit.RetrofitTestModule;
import com.tradehero.th.persistence.PersistenceTestModule;
import com.tradehero.th.ui.GraphicTestModule;
import com.tradehero.th.ui.UxTestModule;
import com.tradehero.th.utils.AppUtilsTestModule;
import dagger.Module;

@Module(
        includes = {
                ApiTestModule.class,
                ModelsTestModule.class,
                ActivityTestModule.class,
                FragmentTestModule.class,
                PersistenceTestModule.class,
                AppUtilsTestModule.class,
                AuthenticationTestModule.class,
                UxTestModule.class,
                GraphicTestModule.class,
                RetrofitTestModule.class
        },
        complete = false,
        library = true
)
public class AppTestModule
{
}
