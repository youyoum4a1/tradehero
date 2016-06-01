package com.ayondo.academy.models.share;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.activities.DashboardActivityExtended;
import com.ayondo.academy.api.discussion.AbstractDiscussionCompactDTO;
import com.ayondo.academy.api.news.NewsItemCompactDTO;
import com.ayondo.academy.api.translation.bing.BingTranslationToken;
import com.ayondo.academy.api.translation.bing.BingUserTranslationSettingDTO;
import com.ayondo.academy.persistence.translation.TranslationTokenCacheRx;
import com.ayondo.academy.persistence.translation.TranslationTokenKey;
import com.ayondo.academy.persistence.translation.UserTranslationSettingPreference;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Provider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SocialShareTranslationHelperTest
{
    @Inject TranslationTokenCacheRx translationTokenCache;
    @Inject UserTranslationSettingPreference userTranslationSettingPreference;
    @Inject Provider<SocialShareTranslationHelper> translationHelperProvider;
    private SocialShareTranslationHelper translationHelper;

    @Before public void setUp()
    {
        Robolectric.setupActivity(DashboardActivityExtended.class).inject(this);
    }

    @After public void tearDown()
    {
        translationTokenCache.invalidateAll();
        userTranslationSettingPreference.delete();
    }

    //<editor-fold desc="Lifecycle">
    @Test public void correctLifecycle()
    {
        translationHelper = translationHelperProvider.get();
    }
    //</editor-fold>

    //<editor-fold desc="Can Translate or not">
    @Test public void cannotTranslateNullDiscussion()
    {
        translationHelper = translationHelperProvider.get();
        //noinspection ConstantConditions
        assertThat(translationHelper.canTranslate((AbstractDiscussionCompactDTO) null).toBlocking().first()).isFalse();
    }

    @Test public void cannotTranslateWhenLangNull()
    {
        translationHelper = translationHelperProvider.get();
        assertThat(translationHelper.canTranslate(new NewsItemCompactDTO()).toBlocking().first()).isFalse();
    }

    @Test public void cannotTranslateWhenLangEmpty()
    {
        AbstractDiscussionCompactDTO discussion = new NewsItemCompactDTO();
        discussion.langCode = "";
        translationHelper = translationHelperProvider.get();
        assertThat(translationHelper.canTranslate(discussion).toBlocking().first()).isFalse();
    }

    @Test public void cannotTranslateWhenLangIsInvalid()
    {
        AbstractDiscussionCompactDTO discussion = new NewsItemCompactDTO();
        discussion.langCode = "xxx";
        translationHelper = translationHelperProvider.get();
        assertThat(translationHelper.canTranslate(discussion).toBlocking().first()).isFalse();
    }

    @Test public void cannotTranslateWhenLangIsSameAsTarget()
    {
        translationHelper = translationHelperProvider.get();
        assertThat(translationHelper.getTargetLanguage().toBlocking().first()).isEqualTo("en");
        AbstractDiscussionCompactDTO discussion = new NewsItemCompactDTO();
        discussion.langCode = "en";

        assertThat(translationHelper.canTranslate(discussion).toBlocking().first()).isFalse();
    }

    @Test public void cannotTranslateWhenNoTranslationToken()
    {
        translationHelper = translationHelperProvider.get();
        assertThat(translationHelper.getTargetLanguage().toBlocking().first()).isEqualTo("en");
        AbstractDiscussionCompactDTO discussion = new NewsItemCompactDTO();
        discussion.langCode = "fr";

        assertThat(translationHelper.canTranslate(discussion).toBlocking().first()).isFalse();
    }

    @Test public void canTranslateWhenTranslationToken()
    {
        translationTokenCache.onNext(new TranslationTokenKey(), new BingTranslationToken("", "", "2000", ""));
        assertThat(translationTokenCache.get(new TranslationTokenKey())).isNotNull();
        AbstractDiscussionCompactDTO discussion = new NewsItemCompactDTO();
        discussion.langCode = "fr";
        translationHelper = translationHelperProvider.get();

        assertThat(translationHelper.getTargetLanguage().toBlocking().first()).isEqualTo("en");
        assertThat(translationHelper.canTranslate(discussion).toBlocking().first()).isTrue();
    }
    //</editor-fold>

    //<editor-fold desc="Target Language">
    @Test public void targetLanguageWhenNoPref() throws IOException
    {
        assertThat(translationTokenCache.get(new TranslationTokenKey())).isNull();
        assertThat(userTranslationSettingPreference.getSettingDTOs().size()).isEqualTo(0);
        translationHelper = translationHelperProvider.get();

        assertThat(translationHelper.getTargetLanguage().toBlocking().first()).isEqualTo("en");
    }

    @Test public void targetLanguageWhenHasPref() throws IOException
    {
        translationTokenCache.onNext(new TranslationTokenKey(), new BingTranslationToken("", "", "2000", ""));
        userTranslationSettingPreference.addOrReplaceSettingDTO(new BingUserTranslationSettingDTO("fr", false));
        assertThat(translationTokenCache.get(new TranslationTokenKey())).isNotNull();
        assertThat(userTranslationSettingPreference.getSettingDTOs().size()).isEqualTo(1);
        translationHelper = translationHelperProvider.get();

        assertThat(translationHelper.getTargetLanguage().toBlocking().first()).isEqualTo("fr");
    }
    //</editor-fold>

    //<editor-fold desc="Auto Translate flag">
    @Test public void autoTranslateFalseWhenNoPref() throws IOException
    {
        assertThat(translationTokenCache.get(new TranslationTokenKey())).isNull();
        assertThat(userTranslationSettingPreference.getSettingDTOs().size()).isEqualTo(0);
        translationHelper = translationHelperProvider.get();

        assertThat(translationHelper.isAutoTranslate().toBlocking().first()).isFalse();
    }

    @Test public void autoTranslateFalseWhenPrefSaysSo() throws JsonProcessingException
    {
        translationTokenCache.onNext(new TranslationTokenKey(), new BingTranslationToken("", "", "2000", ""));
        userTranslationSettingPreference.addOrReplaceSettingDTO(new BingUserTranslationSettingDTO("fr", false));
        translationHelper = translationHelperProvider.get();

        assertThat(translationHelper.isAutoTranslate().toBlocking().first()).isFalse();
    }

    @Test public void autoTranslateTrueWhenPrefSaysSo() throws JsonProcessingException
    {
        translationTokenCache.onNext(new TranslationTokenKey(), new BingTranslationToken("", "", "2000", ""));
        userTranslationSettingPreference.addOrReplaceSettingDTO(new BingUserTranslationSettingDTO("fr", true));
        translationHelper = translationHelperProvider.get();

        assertThat(translationHelper.isAutoTranslate().toBlocking().first()).isTrue();
    }
    //</editor-fold>
}
