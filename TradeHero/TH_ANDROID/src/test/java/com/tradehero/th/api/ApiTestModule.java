package com.tradehero.th.api;

import com.tradehero.th.api.competition.ApiCompetitionTestModule;
import com.tradehero.th.api.discussion.ApiDiscussionTestModule;
import com.tradehero.th.api.i18n.ApiI18nTestModule;
import com.tradehero.th.api.provider.ApiProviderTestModule;
import com.tradehero.th.api.security.ApiSecurityTestModule;
import com.tradehero.th.api.social.ApiSocialTestModule;
import com.tradehero.th.api.translation.ApiTranslationTestModule;
import dagger.Module;

@Module(
        includes = {
                ApiCompetitionTestModule.class,
                ApiDiscussionTestModule.class,
                ApiI18nTestModule.class,
                ApiProviderTestModule.class,
                ApiSecurityTestModule.class,
                ApiTranslationTestModule.class,
                ApiSocialTestModule.class,
        },
        complete = false,
        library = true
)
public class ApiTestModule
{
}
