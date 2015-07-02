package com.tradehero.th.api;

import com.tradehero.th.api.alert.ApiAlertTestModule;
import com.tradehero.th.api.competition.ApiCompetitionTestModule;
import com.tradehero.th.api.discussion.ApiDiscussionTestModule;
import com.tradehero.th.api.i18n.ApiI18nTestModule;
import com.tradehero.th.api.position.ApiPositionTestModule;
import com.tradehero.th.api.security.ApiSecurityTestModule;
import com.tradehero.th.api.social.ApiSocialTestModule;
import com.tradehero.th.api.translation.ApiTranslationTestModule;
import dagger.Module;

@Module(
        includes = {
                ApiAlertTestModule.class,
                ApiCompetitionTestModule.class,
                ApiDiscussionTestModule.class,
                ApiI18nTestModule.class,
                ApiSecurityTestModule.class,
                ApiSocialTestModule.class,
                ApiPositionTestModule.class,
                ApiTranslationTestModule.class,
        },
        injects = {
                ObjectMapperWrapperTest.class,
        },
        complete = false,
        library = true
)
public class ApiTestModule
{
}
