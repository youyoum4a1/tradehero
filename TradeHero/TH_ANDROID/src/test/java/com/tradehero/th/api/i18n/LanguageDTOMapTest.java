package com.tradehero.th.api.i18n;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.TestConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

@RunWith(RobolectricMavenTestRunner.class)
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
