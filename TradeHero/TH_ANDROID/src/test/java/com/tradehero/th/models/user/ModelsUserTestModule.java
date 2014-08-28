package com.tradehero.th.models.user;

import dagger.Module;

@Module(
        injects = {
                FollowUserAssistantTest.class,
                SimpleFollowUserAssistantTest.class,
                OpenFollowUserAssistant.class,
                OpenSimpleFollowUserAssistant.class,
        },
        complete = false,
        library = true,
        overrides = true
)
public class ModelsUserTestModule
{
}
