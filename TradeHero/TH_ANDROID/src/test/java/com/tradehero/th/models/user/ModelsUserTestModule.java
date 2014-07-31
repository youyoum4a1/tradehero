package com.tradehero.th.models.user;

import dagger.Module;

@Module(
        injects = {
                PremiumFollowUserAssistantTest.class,
                SimplePremiumFollowUserAssistantTest.class,
        },
        complete = false,
        library = true,
        overrides = true
)
public class ModelsUserTestModule
{
}
