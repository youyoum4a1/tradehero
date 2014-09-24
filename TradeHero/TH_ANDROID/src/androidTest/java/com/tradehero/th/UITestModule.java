package com.tradehero.th;

import com.tradehero.th.activities.ActivityUITestModule;
import com.tradehero.th.fragments.FragmentUITestModule;
import com.tradehero.th.models.ModelsUITestModule;
import com.tradehero.th.utils.UtilsUITestModule;
import dagger.Module;

@Module(
        includes = {
                ActivityUITestModule.class,
                FragmentUITestModule.class,
                ModelsUITestModule.class,
                UtilsUITestModule.class,
        },
        complete = false,
        library = true
)
public class UITestModule
{
}
