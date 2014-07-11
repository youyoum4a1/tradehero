package com.tradehero.th.api.translation.bing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.translation.UserTranslationSettingDTO;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

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
}
