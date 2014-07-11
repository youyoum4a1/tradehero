package com.tradehero.th.api.translation.bing;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.i18n.LanguageCodePredicate;
import com.tradehero.th.api.i18n.LanguageDTO;
import com.tradehero.th.api.i18n.LanguageDTOList;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class BingLanguageDTOFactoryTest
{
    @Inject BingLanguageDTOFactory bingLanguageDTOFactory;

    @Test public void getLanguagesShouldReturnFairAmount()
    {
        LanguageDTOList languageDTOs = bingLanguageDTOFactory.getTargetLanguages();

        assertThat(languageDTOs.size()).isGreaterThan(10);
    }

    @Test public void getLanguagesShouldPopulateAllFieldsFrench()
    {
        LanguageDTOList languageDTOs = bingLanguageDTOFactory.getTargetLanguages();

        LanguageDTO french = languageDTOs.findFirstWhere(new LanguageCodePredicate("fr"));
        assertThat(french).isNotNull();
        //noinspection ConstantConditions
        assertThat(french.code).isEqualTo("fr");
        assertThat(french.name).isEqualTo("French");
        //noinspection SpellCheckingInspection
        assertThat(french.nameInOwnLang).isEqualTo("français");
    }

    @Test public void getLanguagesShouldPopulateAllFieldsChinese()
    {
        LanguageDTOList languageDTOs = bingLanguageDTOFactory.getTargetLanguages();

        LanguageDTO chineseSimpl = languageDTOs.findFirstWhere(new LanguageCodePredicate("zh-hans"));
        assertThat(chineseSimpl).isNotNull();
        //noinspection ConstantConditions
        assertThat(chineseSimpl.code).isEqualTo("zh-hans");
        assertThat(chineseSimpl.name).isEqualTo("Chinese (Simplified Han)");
        assertThat(chineseSimpl.nameInOwnLang).isEqualTo("中文 (简体中文)");
    }

    @Test public void getLanguagesShouldPopulateAllFieldsKlingon()
    {
        LanguageDTOList languageDTOs = bingLanguageDTOFactory.getTargetLanguages();

        LanguageDTO klingon = languageDTOs.findFirstWhere(new LanguageCodePredicate("tlh"));
        assertThat(klingon).isNotNull();
        //noinspection ConstantConditions
        assertThat(klingon.code).isEqualTo("tlh");
        assertThat(klingon.name).isEqualTo("Klingon");
        assertThat(klingon.nameInOwnLang).isEqualTo("Klingon");
    }
}
