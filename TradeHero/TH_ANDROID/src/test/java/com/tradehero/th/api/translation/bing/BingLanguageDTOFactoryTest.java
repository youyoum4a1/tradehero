package com.ayondo.academy.api.translation.bing;

import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.api.i18n.LanguageCodePredicate;
import com.ayondo.academy.api.i18n.LanguageDTO;
import com.ayondo.academy.api.i18n.LanguageDTOList;
import com.ayondo.academy.base.TestTHApp;
import javax.inject.Inject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class BingLanguageDTOFactoryTest
{
    @Inject BingLanguageDTOFactory bingLanguageDTOFactory;

    @Test public void getLanguagesShouldReturnFairAmount()
    {
        LanguageDTOList languageDTOs = bingLanguageDTOFactory.getTargetLanguages(TestTHApp.context().getResources());

        assertThat(languageDTOs.size()).isGreaterThan(10);
    }

    @Test public void getLanguagesShouldPopulateAllFieldsFrench()
    {
        LanguageDTOList languageDTOs = bingLanguageDTOFactory.getTargetLanguages(TestTHApp.context().getResources());

        LanguageDTO french = languageDTOs.findFirstWhere(new LanguageCodePredicate("fr"));
        assertThat(french).isNotNull();
        //noinspection ConstantConditions
        assertThat(french.code).isEqualTo("fr");
        assertThat(french.name).isEqualTo("French");
        //noinspection SpellCheckingInspection
        assertThat(french.nameInOwnLang).isEqualTo("français");
    }

    @Ignore("Should hard-code some conversion between languages")
    @Test public void getLanguagesShouldPopulateAllFieldsChinese()
    {
        LanguageDTOList languageDTOs = bingLanguageDTOFactory.getTargetLanguages(TestTHApp.context().getResources());

        LanguageDTO chineseSimpl = languageDTOs.findFirstWhere(new LanguageCodePredicate("zh-hans"));
        assertThat(chineseSimpl).isNotNull();
        //noinspection ConstantConditions
        assertThat(chineseSimpl.code).isEqualTo("zh-hans");
        assertThat(chineseSimpl.name).isEqualTo("Chinese (Simplified Han)");
        assertThat(chineseSimpl.nameInOwnLang).isEqualTo("中文 (简体中文)");
    }

    @Ignore("Should hard-code some conversion between languages")
    @Test public void getLanguagesShouldPopulateAllFieldsKlingon()
    {
        LanguageDTOList languageDTOs = bingLanguageDTOFactory.getTargetLanguages(TestTHApp.context().getResources());

        LanguageDTO klingon = languageDTOs.findFirstWhere(new LanguageCodePredicate("tlh"));
        assertThat(klingon).isNotNull();
        //noinspection ConstantConditions
        assertThat(klingon.code).isEqualTo("tlh");
        assertThat(klingon.name).isEqualTo("Klingon");
        assertThat(klingon.nameInOwnLang).isEqualTo("Klingon");
    }
}
