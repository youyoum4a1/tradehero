package com.ayondo.academy.models.user;

import com.ayondo.academy.models.user.follow.FollowUserAssistantTest;
import com.ayondo.academy.models.user.follow.SimpleFollowUserAssistantTest;
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
