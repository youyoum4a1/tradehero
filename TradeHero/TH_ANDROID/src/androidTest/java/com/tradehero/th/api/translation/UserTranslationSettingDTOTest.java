package com.tradehero.th.api.translation;

import android.support.annotation.NonNull;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.api.i18n.LanguageDTOFactory;
import com.tradehero.th.api.translation.bing.BingUserTranslationSettingDTO;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(THRobolectricTestRunner.class)
public class UserTranslationSettingDTOTest
{
    @Inject LanguageDTOFactory languageDTOFactory;

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
        new UserTranslationSettingDTO("em").cloneForLanguage(languageDTOFactory.createFromCode("a"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void basicClassCannotCloneForAuto()
    {
        new UserTranslationSettingDTO("em").cloneForAuto(false);
    }
}
