package com.ayondo.academy.api.i18n;

import com.ayondo.academyRobolectricTestRunner;
import com.tradehero.TestConstants;
import com.ayondo.academy.BuildConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class LanguageDTOMapTest
{
    private LanguageDTOMap languageDTOMap;

    @Before public void setUp()
    {
        languageDTOMap = new LanguageDTOMap();
    }

    //<editor-fold desc="Add language">
    @Test(expected = IllegalArgumentException.class)
    public void ifIntelliJAddNullThrowsIllegal()
    {
        assumeTrue(TestConstants.IS_INTELLIJ);
        //noinspection ConstantConditions
        languageDTOMap.add(null);
    }

    @Test(expected = NullPointerException.class)
    public void ifNotIntelliJAddNullThrowsNPE()
    {
        assumeTrue(!TestConstants.IS_INTELLIJ);
        //noinspection ConstantConditions
        languageDTOMap.add(null);
    }

    @Test public void canAddLanguage()
    {
        LanguageDTO french = new LanguageDTO("fr", "French", "français");
        languageDTOMap.add(french);

        assertThat(languageDTOMap.size()).isEqualTo(1);
        assertThat(languageDTOMap.get("fr")).isSameAs(french);
    }
    //</editor-fold>
}
