package com.tradehero.th.models.user;

import com.tradehero.th.models.user.follow.FollowUserAssistantTest;
import com.tradehero.th.models.user.follow.SimpleFollowUserAssistantTest;
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
public class ModelsUserUITestModule
{
}
