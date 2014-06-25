package com.tradehero.th.api;

import com.tradehero.th.api.competition.ApiCompetitionTestModule;
import com.tradehero.th.api.discussion.ApiDiscussionTestModule;
import com.tradehero.th.api.security.ApiSecurityTestModule;
import dagger.Module;

@Module(
        includes = {
                ApiDiscussionTestModule.class,
                ApiSecurityTestModule.class,
                ApiCompetitionTestModule.class,
        },
        complete = false,
        library = true
)
public class ApiTestModule
{
}
