package com.tradehero.th.api.translation.bing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.translation.UserTranslationSettingDTO;
import java.io.IOException;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class BingUserTranslationSettingDTOTest
{
    @Inject ObjectMapper objectMapper;

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
}
