package com.tradehero.th.api.i18n;

import com.tradehero.RobolectricMavenTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class LanguageDTOTest
{
    @Test public void canGetLocaleForLanguageTag()
    {
        assertThat(LanguageDTO.forLanguageTag("en").getLanguage())
                .isEqualTo("en");
        assertThat(LanguageDTO.forLanguageTag("fr").getLanguage())
                .isEqualTo("fr");
        assertThat(LanguageDTO.forLanguageTag("zh").getLanguage())
                .isEqualTo("zh");
    }
}
