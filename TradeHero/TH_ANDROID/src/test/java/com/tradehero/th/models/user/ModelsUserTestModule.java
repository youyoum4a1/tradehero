package com.tradehero.th.models.user;

import dagger.Module;

@Module(
        injects = {
                SimplePremiumFollowUserAssistantTest.class,
        },
        complete = false,
        library = true,
        overrides = true
)
public class ModelsUserTestModule
{
}
