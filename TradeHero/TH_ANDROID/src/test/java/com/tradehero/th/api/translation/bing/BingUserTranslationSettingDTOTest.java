package com.tradehero.th.api.translation.bing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.TestConstants;
import com.tradehero.th.api.i18n.LanguageDTO;
import com.tradehero.th.api.translation.UserTranslationSettingDTO;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

@RunWith(RobolectricMavenTestRunner.class)
public class BingUserTranslationSettingDTOTest
{
    @Inject ObjectMapper objectMapper;

    //<editor-fold desc="De/Serialisation">
    @Test public void canSerialiseDTO() throws JsonProcessingException
    {
        UserTranslationSettingDTO settingDTO = new BingUserTranslationSettingDTO("en", true);
        String serialised = objectMapper.writeValueAsString(settingDTO);
        String expected = "{\"translatorType\":\"MicrosoftTranslator\",\"languageCode\":\"en\",\"autoTranslate\":true}";
        assertThat(serialised)
            .isEqualTo(expected);
    }

    @Test public void canDeserialiseDTO() throws IOException
    {
        String serialised = "{\"translatorType\":\"MicrosoftTranslator\",\"languageCode\":\"de\",\"autoTranslate\":false}";
        UserTranslationSettingDTO settingDTO = objectMapper.readValue(serialised, UserTranslationSettingDTO.class);

        assertThat(settingDTO).isExactlyInstanceOf(BingUserTranslationSettingDTO.class);
        assertThat(settingDTO.languageCode).isEqualTo("de");
        assertThat(settingDTO.autoTranslate).isFalse();
    }
    //</editor-fold>

    @Test public void canOnlyHaveFirstOnePerHashSet()
    {
        Set<UserTranslationSettingDTO> set = new HashSet<>();

        set.add(new BingUserTranslationSettingDTO("en"));
        assertThat(set.size()).isEqualTo(1);
        assertThat(set.iterator().next().languageCode).isEqualTo("en");

        set.add(new BingUserTranslationSettingDTO("de"));
        assertThat(set.size()).isEqualTo(1);
        assertThat(set.iterator().next().languageCode).isEqualTo("en");

        set.add(new BingUserTranslationSettingDTO("fr"));
        assertThat(set.size()).isEqualTo(1);
        assertThat(set.iterator().next().languageCode).isEqualTo("en");
    }

    //<editor-fold desc="Clone">
    @Test(expected = IllegalArgumentException.class)
    public void ifIntelliJCloneWithNullThrowsIllegal()
    {
        assumeTrue(TestConstants.IS_INTELLIJ);
        //noinspection ConstantConditions
        new BingUserTranslationSettingDTO("en", false).cloneForLanguage(null);
    }

    @Test(expected = NullPointerException.class)
    public void ifNotIntelliJCloneWithNullThrowsNPE()
    {
        assumeTrue(!TestConstants.IS_INTELLIJ);
        //noinspection ConstantConditions
        new BingUserTranslationSettingDTO("en", false).cloneForLanguage(null);
    }

    @Test public void canCloneWithOtherLanguage()
    {
        UserTranslationSettingDTO settingDTO = new BingUserTranslationSettingDTO("en", false);
        UserTranslationSettingDTO cloned = settingDTO.cloneForLanguage(new LanguageDTO("fr"));

        assertThat(cloned).isExactlyInstanceOf(BingUserTranslationSettingDTO.class);
        assertThat(cloned.languageCode).isEqualTo("fr");
        assertThat(cloned.autoTranslate).isFalse();
    }

    @Test public void canCloneWithAuto()
    {
        UserTranslationSettingDTO settingDTO = new BingUserTranslationSettingDTO("en", false);
        UserTranslationSettingDTO cloned = settingDTO.cloneForAuto(true);

        assertThat(cloned).isExactlyInstanceOf(BingUserTranslationSettingDTO.class);
        assertThat(cloned.languageCode).isEqualTo("en");
        assertThat(cloned.autoTranslate).isTrue();
    }
    //</editor-fold>
}
