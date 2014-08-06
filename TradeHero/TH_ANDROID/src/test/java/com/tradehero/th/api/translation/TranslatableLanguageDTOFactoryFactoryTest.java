package com.tradehero.th.api.translation;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.TestConstants;
import com.tradehero.th.api.i18n.LanguageDTO;
import com.tradehero.th.api.translation.bing.BingLanguageDTOFactory;
import com.tradehero.th.api.translation.bing.BingTranslationToken;
import com.tradehero.th.persistence.translation.TranslationTokenCache;
import com.tradehero.th.persistence.translation.TranslationTokenKey;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

@RunWith(RobolectricMavenTestRunner.class)
public class TranslatableLanguageDTOFactoryFactoryTest
{
    @Inject TranslationTokenCache translationTokenCache;
    @Inject TranslatableLanguageDTOFactoryFactory translatableLanguageDTOFactoryFactory;

    @Before public void setUp()
    {
        translationTokenCache.invalidateAll();
    }

    @After public void tearDown()
    {
        translationTokenCache.invalidateAll();
    }

    public class FakeToken extends TranslationToken
    {
        @Override public boolean isValid()
        {
            return true;
        }
    }

    //<editor-fold desc="Create with type cannot take null">
    @Test(expected = IllegalArgumentException.class)
    public void ifIntelliJCreateWithNullIllegal()
    {
        assumeTrue(TestConstants.IS_INTELLIJ);
        //noinspection ConstantConditions
        translatableLanguageDTOFactoryFactory.create(null);
    }

    @Test(expected = NullPointerException.class)
    public void ifNotIntelliJCreateWithNullNPE()
    {
        assumeTrue(!TestConstants.IS_INTELLIJ);
        //noinspection ConstantConditions
        translatableLanguageDTOFactoryFactory.create(null);
    }
    //</editor-fold>

    //<editor-fold desc="Create with type, unknown or proper type">
    @Test public void nullFactoryIfUnknownTokenType()
    {
        assertThat(translatableLanguageDTOFactoryFactory.create(new FakeToken()))
                .isNull();
    }

    @Test public void bingFactoryIfBingTokenType()
    {
        assertThat(translatableLanguageDTOFactoryFactory.create(new BingTranslationToken()))
                .isExactlyInstanceOf(BingLanguageDTOFactory.class);
    }
    //</editor-fold>

    //<editor-fold desc="Creates new">
    @Test public void bingFactoryIsNewEveryCall()
    {
        TranslatableLanguageDTOFactory first = translatableLanguageDTOFactoryFactory.create(new BingTranslationToken());
        TranslatableLanguageDTOFactory second = translatableLanguageDTOFactoryFactory.create(new BingTranslationToken());

        assertThat(first).isNotNull();
        assertThat(second).isNotNull();
        assertThat(first).isNotSameAs(second);
    }
    //</editor-fold>

    //<editor-fold desc="Blind create, with different cache situations">
    @Test public void nullFactoryIfNoTokenInCache()
    {
        assertThat(translatableLanguageDTOFactoryFactory.create())
                .isNull();
    }

    @Test public void nullFactoryIfUnknownTokenInCache()
    {
        translationTokenCache.put(new TranslationTokenKey(), new FakeToken());
        assertThat(translatableLanguageDTOFactoryFactory.create())
                .isNull();
    }

    @Test public void nullFactoryIfInvalidBingTokenInCache()
    {
        BingTranslationToken translationToken = new BingTranslationToken("", "", "0", "");
        translationTokenCache.put(new TranslationTokenKey(), translationToken);
        assertThat(translatableLanguageDTOFactoryFactory.create())
                .isNull();
    }

    @Test public void bingFactoryIfValidBingTokenInCache()
    {
        BingTranslationToken translationToken = new BingTranslationToken("", "", "2000", "");
        translationTokenCache.put(new TranslationTokenKey(), translationToken);
        assertThat(translatableLanguageDTOFactoryFactory.create())
                .isExactlyInstanceOf(BingLanguageDTOFactory.class);
    }
    //</editor-fold>

    //<editor-fold desc="Get Best Match">
    @Test public void getBestMatchWithWeirdReturnsFallback()
    {
        //noinspection ConstantConditions
        LanguageDTO bestMatch = translatableLanguageDTOFactoryFactory.create(new BingTranslationToken()).getBestMatch("weird", "fr");
        assertThat(bestMatch.code).isEqualTo("fr");
    }

    @Test public void getBestMatchWithExistingReturnsExisting()
    {
        //noinspection ConstantConditions
        LanguageDTO bestMatch = translatableLanguageDTOFactoryFactory.create(new BingTranslationToken()).getBestMatch("ca", "fr");
        assertThat(bestMatch.code).isEqualTo("ca");
    }
    //</editor-fold>
}
