package com.ayondo.academy.api.i18n;

import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.base.TestTHApp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class LanguageDTOFactoryTest
{
    @Before public void setUp()
    {
        TestTHApp.staticInject(this);
    }

    @SuppressWarnings("ConstantConditions")
    @Test public void canGetLocaleForLanguageTag()
    {
        assertThat(LanguageDTOFactory.forLanguageTag("en").getLanguage())
                .isEqualTo("en");
        assertThat(LanguageDTOFactory.forLanguageTag("fr").getLanguage())
                .isEqualTo("fr");
        assertThat(LanguageDTOFactory.forLanguageTag("zh").getLanguage())
                .isEqualTo("zh");
    }

    @SuppressWarnings("ConstantConditions")
    @Test public void canGetRecognisedLanguage()
    {
        assertThat(LanguageDTOFactory.createFromCode(TestTHApp.context().getResources(), "mww").name)
                .isEqualTo("Hmong Daw");
    }

    @Test public void populatesKnown()
    {
        LanguageDTOMap known = LanguageDTOFactory.getHardCodedLanguages(TestTHApp.context().getResources());
        assertThat(known.size()).isGreaterThanOrEqualTo(4);
    }
}
