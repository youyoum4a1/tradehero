package com.ayondo.academy.api.translation;

import android.support.annotation.NonNull;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.api.i18n.LanguageDTOFactory;
import com.ayondo.academy.api.translation.bing.BingUserTranslationSettingDTO;
import com.ayondo.academy.base.TestTHApp;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class UserTranslationSettingDTOTest
{
    private class FakeSettingDTO extends UserTranslationSettingDTO
    {
        public FakeSettingDTO(@NonNull String languageCode)
        {
            super(languageCode);
        }
    }

    @Test public void canOnlyHaveOnePerTypeInHashSet()
    {
        Set<UserTranslationSettingDTO> set = new HashSet<>();

        set.add(new BingUserTranslationSettingDTO("en"));
        assertThat(set.size()).isEqualTo(1);
        assertThat(set.iterator().next().languageCode).isEqualTo("en");

        set.add(new FakeSettingDTO("en"));
        assertThat(set.size()).isEqualTo(2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void basicClassCannotCloneForLanguage()
    {
        new UserTranslationSettingDTO("em").cloneForLanguage(LanguageDTOFactory.createFromCode(TestTHApp.context().getResources(), "a"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void basicClassCannotCloneForAuto()
    {
        new UserTranslationSettingDTO("em").cloneForAuto(false);
    }
}
