package com.ayondo.academy.models;

import com.ayondo.academy.models.share.ModelsShareUITestModule;
import com.ayondo.academy.models.user.ModelsUserUITestModule;
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
