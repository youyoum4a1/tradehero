package com.tradehero.th.api;

import com.tradehero.th.api.discussion.DiscussionModule;
import dagger.Module;

@Module(
        includes = {
                DiscussionModule.class
        },
        complete = false,
        library = true
)
public class ApiModule
{
}
