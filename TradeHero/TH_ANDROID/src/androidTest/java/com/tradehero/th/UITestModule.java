package com.tradehero.th;

import com.tradehero.th.activities.ActivityTestModule;
import com.tradehero.th.fragments.FragmentTestModule;
import com.tradehero.th.models.ModelsUITestModule;
import com.tradehero.th.utils.UtilsUITestModule;
import dagger.Module;

@Module(
        includes = {
                ActivityTestModule.class,
                FragmentTestModule.class,
                ModelsUITestModule.class,
                UtilsUITestModule.class,
        },
        complete = false,
        library = true
)
public class UITestModule
{
}
