package com.ayondo.academy.api.i18n;

import com.ayondo.academyRobolectricTestRunner;
import com.tradehero.TestConstants;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.base.TestTHApp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class LanguageCodePredicateTest
{
    @Before public void setUp()
    {
        TestTHApp.staticInject(this);
    }

    //<editor-fold desc="Constructor does not accept null language code">
    @Test(expected = IllegalArgumentException.class)
    public void ifIntelliJIllegalOnConstructNullLanguageCode()
    {
        assumeTrue(TestConstants.IS_INTELLIJ);
        //noinspection ConstantConditions
        new LanguageCodePredicate(null);
    }

    @Test(expected = NullPointerException.class)
    public void ifNotIntelliJNPEOnConstructAndUserNullLanguageCode()
    {
        assumeTrue(!TestConstants.IS_INTELLIJ);
        //noinspection ConstantConditions
        new LanguageCodePredicate(null).apply(LanguageDTOFactory.createFromCode(TestTHApp.context().getResources(), "en"));
    }
    //</editor-fold>

    //<editor-fold desc="Apply does not accept null language">
    @Test(expected = IllegalArgumentException.class)
    public void ifIntelliJIllegalOnApplyNullLanguage()
    {
        assumeTrue(TestConstants.IS_INTELLIJ);
        //noinspection ConstantConditions
        new LanguageCodePredicate("ap").apply(null);
    }

    @Test(expected = NullPointerException.class)
    public void ifNotIntelliJNPEOnApplyNullLanguage()
    {
        assumeTrue(!TestConstants.IS_INTELLIJ);
        //noinspection ConstantConditions
        new LanguageCodePredicate("ap").apply(null);
    }
    //</editor-fold>

    //<editor-fold desc="Proper match testing">
    @Test public void matches()
    {
        LanguageCodePredicate languageCodePredicate = new LanguageCodePredicate("ap");
        assertThat(languageCodePredicate.apply(new LanguageDTO("ap", ""))).isTrue();
    }

    @Test public void doesNotMatch()
    {
        LanguageCodePredicate languageCodePredicate = new LanguageCodePredicate("ap");
        assertThat(languageCodePredicate.apply(new LanguageDTO("ad", ""))).isFalse();
    }
    //</editor-fold>
}
