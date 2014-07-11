package com.tradehero.th.persistence.translation;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.translation.UserTranslationSettingDTO;
import com.tradehero.th.api.translation.bing.BingUserTranslationSettingDTO;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class UserTranslationSettingPreferenceTest
{
    @Inject UserTranslationSettingPreference userTranslationSettingPreference;

    @After public void tearDown()
    {
        userTranslationSettingPreference.delete();
    }

    @Test public void startsEmpty() throws IOException
    {
        assertThat(userTranslationSettingPreference.getSettingDTOs().size())
                .isEqualTo(0);
    }

    @Test public void savesSetting() throws IOException
    {
        UserTranslationSettingDTO settingDTO = new BingUserTranslationSettingDTO("tg", true);
        Set<UserTranslationSettingDTO> settingDTOSet = new HashSet<>();
        settingDTOSet.add(settingDTO);

        userTranslationSettingPreference.setSettingDTOs(settingDTOSet);

        Set<String> saved = userTranslationSettingPreference.get();
        String expectedFirst = "{\"translatorType\":\"MicrosoftTranslator\",\"languageCode\":\"tg\",\"autoTranslate\":true}";
        assertThat(saved.iterator().next())
            .isEqualTo(expectedFirst);
    }

    @Test public void getSetting() throws IOException
    {
        UserTranslationSettingDTO settingDTO = new BingUserTranslationSettingDTO("tp", true);
        Set<UserTranslationSettingDTO> settingDTOSet = new HashSet<>();
        settingDTOSet.add(settingDTO);

        userTranslationSettingPreference.setSettingDTOs(settingDTOSet);
        Set<UserTranslationSettingDTO> gotSettingDTOSet = userTranslationSettingPreference.getSettingDTOs();

        UserTranslationSettingDTO gotSettingDTO = gotSettingDTOSet.iterator().next();
        assertThat(gotSettingDTO.languageCode).isEqualTo("tp");
        assertThat(gotSettingDTO.autoTranslate).isTrue();
    }
}
