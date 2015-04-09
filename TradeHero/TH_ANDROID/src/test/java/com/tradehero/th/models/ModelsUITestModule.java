package com.tradehero.th.models;

import com.tradehero.th.models.share.ModelsShareUITestModule;
import com.tradehero.th.models.user.ModelsUserUITestModule;
import dagger.Module;

@Module(
        injects = {
        },
        includes = {
                ModelsShareUITestModule.class,
                ModelsUserUITestModule.class,
        },
        complete = false,
        library = true
)
public class ModelsUITestModule
{
}
