package com.tradehero.th.models.share;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.translation.bing.BingTranslationToken;
import com.tradehero.th.api.translation.bing.BingUserTranslationSettingDTO;
import com.tradehero.th.persistence.translation.TranslationTokenCache;
import com.tradehero.th.persistence.translation.TranslationTokenKey;
import com.tradehero.th.persistence.translation.UserTranslationSettingPreference;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Provider;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class SocialShareTranslationHelperTest
{
    @Inject TranslationTokenCache translationTokenCache;
    @Inject UserTranslationSettingPreference userTranslationSettingPreference;
    @Inject Provider<SocialShareTranslationHelper> translationHelperProvider;
    private SocialShareTranslationHelper translationHelper;

    @After public void tearDown()
    {
        translationTokenCache.invalidateAll();
        userTranslationSettingPreference.delete();
        if (translationHelper != null)
        {
            translationHelper.onDetach();
        }
    }

    //<editor-fold desc="Lifecycle">
    @Test public void correctLifecycle()
    {
        translationHelper = translationHelperProvider.get();
        assertThat(translationHelper.translationTokenListener).isNotNull();

        translationHelper.onDetach();

        assertThat(translationHelper.translationTokenListener).isNull();
        assertThat(translationHelper.menuClickedListener).isNull();
    }
    //</editor-fold>

    //<editor-fold desc="Can Translate or not">
    @Test public void cannotTranslateNullDiscussion()
    {
        translationHelper = translationHelperProvider.get();
        assertThat(translationHelper.canTranslate(null)).isFalse();
    }

    @Test public void cannotTranslateWhenLangNull()
    {
        translationHelper = translationHelperProvider.get();
        assertThat(translationHelper.canTranslate(new NewsItemCompactDTO())).isFalse();
    }

    @Test public void cannotTranslateWhenLangEmpty()
    {
        AbstractDiscussionCompactDTO discussion = new NewsItemCompactDTO();
        discussion.langCode = "";
        translationHelper = translationHelperProvider.get();
        assertThat(translationHelper.canTranslate(discussion)).isFalse();
    }

    @Test public void cannotTranslateWhenLangIsInvalid()
    {
        AbstractDiscussionCompactDTO discussion = new NewsItemCompactDTO();
        discussion.langCode = "xxx";
        translationHelper = translationHelperProvider.get();
        assertThat(translationHelper.canTranslate(discussion)).isFalse();
    }

    @Test public void cannotTranslateWhenLangIsSameAsTarget()
    {
        translationHelper = translationHelperProvider.get();
        assertThat(translationHelper.getTargetLanguage()).isEqualTo("en");
        AbstractDiscussionCompactDTO discussion = new NewsItemCompactDTO();
        discussion.langCode = "en";

        assertThat(translationHelper.canTranslate(discussion)).isFalse();
    }

    @Test public void cannotTranslateWhenNoTranslationToken()
    {
        translationHelper = translationHelperProvider.get();
        assertThat(translationHelper.getTargetLanguage()).isEqualTo("en");
        AbstractDiscussionCompactDTO discussion = new NewsItemCompactDTO();
        discussion.langCode = "fr";

        assertThat(translationHelper.canTranslate(discussion)).isFalse();
    }

    @Test public void canTranslateWhenTranslationToken()
    {
        translationTokenCache.put(new TranslationTokenKey(), new BingTranslationToken("", "", "2000", ""));
        assertThat(translationTokenCache.get(new TranslationTokenKey())).isNotNull();
        AbstractDiscussionCompactDTO discussion = new NewsItemCompactDTO();
        discussion.langCode = "fr";
        translationHelper = translationHelperProvider.get();

        assertThat(translationHelper.getTargetLanguage()).isEqualTo("en");
        assertThat(translationHelper.canTranslate(discussion)).isTrue();
    }
    //</editor-fold>

    //<editor-fold desc="Target Language">
    @Test public void targetLanguageWhenNoPref() throws IOException
    {
        assertThat(translationTokenCache.get(new TranslationTokenKey())).isNull();
        assertThat(userTranslationSettingPreference.getSettingDTOs().size()).isEqualTo(0);
        translationHelper = translationHelperProvider.get();

        assertThat(translationHelper.getTargetLanguage()).isEqualTo("en");
    }

    @Test public void targetLanguageWhenHasPref() throws IOException
    {
        translationTokenCache.put(new TranslationTokenKey(), new BingTranslationToken("", "", "2000", ""));
        userTranslationSettingPreference.addOrReplaceSettingDTO(new BingUserTranslationSettingDTO("fr", false));
        assertThat(translationTokenCache.get(new TranslationTokenKey())).isNotNull();
        assertThat(userTranslationSettingPreference.getSettingDTOs().size()).isEqualTo(1);
        translationHelper = translationHelperProvider.get();

        assertThat(translationHelper.getTargetLanguage()).isEqualTo("fr");
    }
    //</editor-fold>

    //<editor-fold desc="Auto Translate flag">
    @Test public void autoTranslateFalseWhenNoPref() throws IOException
    {
        assertThat(translationTokenCache.get(new TranslationTokenKey())).isNull();
        assertThat(userTranslationSettingPreference.getSettingDTOs().size()).isEqualTo(0);
        translationHelper = translationHelperProvider.get();

        assertThat(translationHelper.isAutoTranslate()).isFalse();
    }

    @Test public void autoTranslateFalseWhenPrefSaysSo() throws JsonProcessingException
    {
        translationTokenCache.put(new TranslationTokenKey(), new BingTranslationToken("", "", "2000", ""));
        userTranslationSettingPreference.addOrReplaceSettingDTO(new BingUserTranslationSettingDTO("fr", false));
        translationHelper = translationHelperProvider.get();

        assertThat(translationHelper.isAutoTranslate()).isFalse();
    }

    @Test public void autoTranslateTrueWhenPrefSaysSo() throws JsonProcessingException
    {
        translationTokenCache.put(new TranslationTokenKey(), new BingTranslationToken("", "", "2000", ""));
        userTranslationSettingPreference.addOrReplaceSettingDTO(new BingUserTranslationSettingDTO("fr", true));
        translationHelper = translationHelperProvider.get();

        assertThat(translationHelper.isAutoTranslate()).isTrue();
    }
    //</editor-fold>
}
