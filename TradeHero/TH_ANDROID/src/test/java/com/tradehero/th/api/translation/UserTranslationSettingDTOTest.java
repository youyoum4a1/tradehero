package com.tradehero.th.api.translation;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.i18n.LanguageDTO;
import com.tradehero.th.api.translation.bing.BingUserTranslationSettingDTO;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class UserTranslationSettingDTOTest
{
    private class FakeSettingDTO extends UserTranslationSettingDTO
    {
        public FakeSettingDTO(@NotNull String languageCode)
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
    public void basicClassCannotClone()
    {
        new UserTranslationSettingDTO("em").cloneForLanguage(new LanguageDTO("a"));
    }
}
