package com.tradehero.th.api;

import com.tradehero.th.api.competition.ApiCompetitionTestModule;
import com.tradehero.th.api.discussion.ApiDiscussionTestModule;
import com.tradehero.th.api.security.ApiSecurityTestModule;
import com.tradehero.th.api.translation.ApitTranslationTestModule;
import dagger.Module;

@Module(
        includes = {
                ApiCompetitionTestModule.class,
                ApiDiscussionTestModule.class,
                ApiSecurityTestModule.class,
                ApitTranslationTestModule.class,
        },
        complete = false,
        library = true
)
public class ApiTestModule
{
}
