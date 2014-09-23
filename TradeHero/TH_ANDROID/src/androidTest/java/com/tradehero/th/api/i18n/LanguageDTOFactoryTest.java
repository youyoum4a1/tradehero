package com.tradehero.th.api.i18n;

import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.base.TestTHApp;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
public class LanguageDTOFactoryTest
{
    @Inject LanguageDTOFactory languageDTOFactory;

    @Before public void setUp()
    {
        TestTHApp.staticInject(this);
    }

    @SuppressWarnings("ConstantConditions")
    @Test public void canGetLocaleForLanguageTag()
    {
        assertThat(languageDTOFactory.forLanguageTag("en").getLanguage())
                .isEqualTo("en");
        assertThat(languageDTOFactory.forLanguageTag("fr").getLanguage())
                .isEqualTo("fr");
        assertThat(languageDTOFactory.forLanguageTag("zh").getLanguage())
                .isEqualTo("zh");
    }

    @SuppressWarnings("ConstantConditions")
    @Test public void canGetRecognisedLanguage()
    {
        assertThat(languageDTOFactory.createFromCode("mww").name)
                .isEqualTo("Hmong Daw");
    }

    @Test public void populatesKnown()
    {
        LanguageDTOMap known = languageDTOFactory.getHardCodedLanguages();
        assertThat(known.size()).isGreaterThanOrEqualTo(4);
    }
}
