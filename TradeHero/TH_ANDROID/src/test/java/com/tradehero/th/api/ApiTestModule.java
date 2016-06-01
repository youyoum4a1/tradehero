package com.ayondo.academy.api;

import com.ayondo.academy.api.alert.ApiAlertTestModule;
import com.ayondo.academy.api.competition.ApiCompetitionTestModule;
import com.ayondo.academy.api.discussion.ApiDiscussionTestModule;
import com.ayondo.academy.api.i18n.ApiI18nTestModule;
import com.ayondo.academy.api.position.ApiPositionTestModule;
import com.ayondo.academy.api.security.ApiSecurityTestModule;
import com.ayondo.academy.api.social.ApiSocialTestModule;
import com.ayondo.academy.api.translation.ApiTranslationTestModule;
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
