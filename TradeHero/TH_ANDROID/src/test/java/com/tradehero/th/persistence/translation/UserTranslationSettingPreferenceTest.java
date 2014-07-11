package com.tradehero.th.persistence.translation;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.TestConstants;
import com.tradehero.th.api.translation.TranslationToken;
import com.tradehero.th.api.translation.UserTranslationSettingDTO;
import com.tradehero.th.api.translation.bing.BingTranslationToken;
import com.tradehero.th.api.translation.bing.BingUserTranslationSettingDTO;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

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

    //<editor-fold desc="Get and Set">
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
    //</editor-fold>

    //<editor-fold desc="Get of same type setting">
    @Test public void getOfSameTypeIfNoneReturnsSame() throws IOException
    {
        UserTranslationSettingDTO settingDTO = new BingUserTranslationSettingDTO("tp", true);
        UserTranslationSettingDTO found = userTranslationSettingPreference.getOfSameTypeOrDefault(settingDTO);

        assertThat(found).isSameAs(settingDTO);
    }

    @Test public void getOfSameTypeWhenHasReturnsTheOther() throws IOException
    {
        UserTranslationSettingDTO savedSetting = new BingUserTranslationSettingDTO("tp", true);
        Set<UserTranslationSettingDTO> settingDTOSet = new HashSet<>();
        settingDTOSet.add(savedSetting);
        userTranslationSettingPreference.setSettingDTOs(settingDTOSet);

        UserTranslationSettingDTO defaultSettingDTO = new BingUserTranslationSettingDTO("ur", false);
        UserTranslationSettingDTO found = userTranslationSettingPreference.getOfSameTypeOrDefault(defaultSettingDTO);

        assertThat(found).isExactlyInstanceOf(BingUserTranslationSettingDTO.class);
        assertThat(found.languageCode).isEqualTo("tp");
        assertThat(found.autoTranslate).isTrue();
    }
    //</editor-fold>

    //<editor-fold desc="Get of same type as token">
    @Test(expected = IllegalArgumentException.class)
    public void ifIntelliJGetOfSameTypeAsTokenNullThrowsIllegal() throws IOException
    {
        assumeTrue(TestConstants.IS_INTELLIJ);
        //noinspection ConstantConditions
        userTranslationSettingPreference.getOfSameTypeOrDefault((TranslationToken) null);
    }

    @Test(expected = NullPointerException.class)
    public void ifNotIntelliJGetOfSameTypeAsTokenNullThrowsNPE() throws IOException
    {
        assumeTrue(!TestConstants.IS_INTELLIJ);
        //noinspection ConstantConditions
        userTranslationSettingPreference.getOfSameTypeOrDefault((TranslationToken) null);
    }

    @Test public void getOfSameTypeAsTokenIfUnknownReturnsNull() throws IOException
    {
        assertThat(userTranslationSettingPreference.getOfSameTypeOrDefault(new TranslationToken()))
                .isNull();
    }

    @Test public void getOfSameTypeAsTokenBing() throws IOException
    {
        assertThat(userTranslationSettingPreference.getOfSameTypeOrDefault(new BingTranslationToken()))
                .isExactlyInstanceOf(BingUserTranslationSettingDTO.class);
    }
    //</editor-fold>
}
