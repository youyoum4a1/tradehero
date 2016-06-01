package com.ayondo.academy;

import com.ayondo.academy.activities.ActivityUITestModule;
import com.ayondo.academy.activities.DashboardActivityExtended;
import com.ayondo.academy.fragments.FragmentUITestModule;
import com.ayondo.academy.models.ModelsUITestModule;
import com.ayondo.academy.utils.UtilsUITestModule;
import dagger.Module;

@Module(
        includes = {
                ActivityUITestModule.class,
                FragmentUITestModule.class,
                ModelsUITestModule.class,
                UtilsUITestModule.class,
        },
        injects = {
                DashboardActivityExtended.class,
        },
        complete = false,
        library = true
)
public class UITestModule
{
}
