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
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class SocialShareTranslationHelperTest
{
    @Inject TranslationTokenCache translationTokenCache;
    @Inject UserTranslationSettingPreference userTranslationSettingPreference;
    @Inject SocialShareTranslationHelper translationHelper;

    @After public void tearDown()
    {
        translationTokenCache.invalidateAll();
        userTranslationSettingPreference.delete();
    }

    //<editor-fold desc="Lifecycle">
    @Test public void correctLifecycle()
    {
        assertThat(translationHelper.translationTokenListener).isNotNull();

        translationHelper.onDetach();

        assertThat(translationHelper.translationTokenListener).isNull();
        assertThat(translationHelper.menuClickedListener).isNull();
    }
    //</editor-fold>

    //<editor-fold desc="Can Translate or not">
    @Test public void cannotTranslateNullDiscussion()
    {
        assertThat(translationHelper.canTranslate(null)).isFalse();
    }

    @Test public void cannotTranslateWhenLangNull()
    {
        assertThat(translationHelper.canTranslate(new NewsItemCompactDTO())).isFalse();
    }

    @Test public void cannotTranslateWhenLangEmpty()
    {
        AbstractDiscussionCompactDTO discussion = new NewsItemCompactDTO();
        discussion.langCode = "";
        assertThat(translationHelper.canTranslate(discussion)).isFalse();
    }

    @Test public void cannotTranslateWhenLangIsInvalid()
    {
        AbstractDiscussionCompactDTO discussion = new NewsItemCompactDTO();
        discussion.langCode = "xxx";
        assertThat(translationHelper.canTranslate(discussion)).isFalse();
    }

    @Test public void cannotTranslateWhenLangIsSameAsTarget()
    {
        assertThat(translationHelper.getTargetLanguage()).isEqualTo("en");
        AbstractDiscussionCompactDTO discussion = new NewsItemCompactDTO();
        discussion.langCode = "en";
        assertThat(translationHelper.canTranslate(discussion)).isFalse();
    }

    @Test public void cannotTranslateWhenNoTranslationToken()
    {
        assertThat(translationHelper.getTargetLanguage()).isEqualTo("en");
        AbstractDiscussionCompactDTO discussion = new NewsItemCompactDTO();
        discussion.langCode = "fr";

        assertThat(translationHelper.canTranslate(discussion)).isFalse();
    }

    @Test public void canTranslateWhenTranslationToken()
    {
        assertThat(translationHelper.getTargetLanguage()).isEqualTo("en");
        translationTokenCache.put(new TranslationTokenKey(), new BingTranslationToken("", "", "2000", ""));
        translationHelper.fetchTranslationToken();
        AbstractDiscussionCompactDTO discussion = new NewsItemCompactDTO();
        discussion.langCode = "fr";

        assertThat(translationHelper.canTranslate(discussion)).isTrue();
    }
    //</editor-fold>

    //<editor-fold desc="Target Language">
    @Test public void targetLanguageWhenNoPref() throws IOException
    {
        assertThat(translationTokenCache.get(new TranslationTokenKey())).isNull();
        assertThat(userTranslationSettingPreference.getSettingDTOs().size()).isEqualTo(0);
        assertThat(translationHelper.getTargetLanguage()).isEqualTo("en");
    }

    @Test public void targetLanguageWhenHasPref() throws IOException
    {
        translationTokenCache.put(new TranslationTokenKey(), new BingTranslationToken("", "", "2000", ""));
        userTranslationSettingPreference.addOrReplaceSettingDTO(new BingUserTranslationSettingDTO("fr", false));
        assertThat(translationTokenCache.get(new TranslationTokenKey())).isNotNull();
        assertThat(userTranslationSettingPreference.getSettingDTOs().size()).isEqualTo(1);
        translationHelper.fetchTranslationToken();

        assertThat(translationHelper.getTargetLanguage()).isEqualTo("fr");
    }
    //</editor-fold>

    //<editor-fold desc="Auto Translate flag">
    @Test public void autoTranslateFalseWhenNoPref() throws IOException
    {
        assertThat(translationTokenCache.get(new TranslationTokenKey())).isNull();
        assertThat(userTranslationSettingPreference.getSettingDTOs().size()).isEqualTo(0);
        assertThat(translationHelper.isAutoTranslate()).isFalse();
    }

    @Test public void autoTranslateFalseWhenPrefSaysSo() throws JsonProcessingException
    {
        translationTokenCache.put(new TranslationTokenKey(), new BingTranslationToken("", "", "2000", ""));
        userTranslationSettingPreference.addOrReplaceSettingDTO(new BingUserTranslationSettingDTO("fr", false));
        translationHelper.fetchTranslationToken();

        assertThat(translationHelper.isAutoTranslate()).isFalse();
    }

    @Test public void autoTranslateTrueWhenPrefSaysSo() throws JsonProcessingException
    {
        translationTokenCache.put(new TranslationTokenKey(), new BingTranslationToken("", "", "2000", ""));
        userTranslationSettingPreference.addOrReplaceSettingDTO(new BingUserTranslationSettingDTO("fr", true));
        translationHelper.fetchTranslationToken();

        assertThat(translationHelper.isAutoTranslate()).isTrue();
    }
    //</editor-fold>
}
