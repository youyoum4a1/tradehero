package com.tradehero.th.fragments.discovery;

import android.content.SharedPreferences;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.th.api.news.CountryLanguagePairDTO;
import dagger.Component;
import dagger.Provides;
import java.util.Locale;
import javax.inject.Singleton;

@Component(modules = { FragmentDiscoveryComponent.Module.class })
public interface FragmentDiscoveryComponent
{
    void injectDiscoveryMainFragment(DiscoveryMainFragment target);
    void injectNewsHeadlineFragment(NewsHeadlineFragment target);
    void injectRegionalNewsHeadlineFragment(RegionalNewsHeadlineFragment target);
    void injectDiscoveryDiscussionFragment(DiscoveryDiscussionFragment target);
    void injectLearningFragment(LearningFragment target);
    void injectRegionalNewsSelectorView(RegionalNewsSelectorView target);
    void injectRegionalNewsSearchableSelectorView(RegionalNewsSearchableSelectorView target);
    void injectDiscoveryGameFragment(DiscoveryGameFragment target);
    void injectMiniGameDefItemView(MiniGameDefItemView target);
    void injectDiscoveryArticleFragment(DiscoveryArticleFragment target);
    void injectArticleItemView(ArticleItemView target);
    void injectDiscoveryFaqWebFragment(DiscoveryFaqWebFragment target);

    @dagger.Module
    static class Module
    {
        private static final String PREF_KEY_REGIONAL_NEWS_COUNTRY_LANGUAGE = "PREF_KEY_REGIONAL_NEWS_COUNTRY_LANGUAGE";

        @Provides @Singleton @RegionalNews
        public CountryLanguagePreference provideRegionalNewsCountryLanguagePreference(@ForUser SharedPreferences sharedPreferences, Locale locale)
        {
            CountryLanguagePairDTO countryLanguagePairDTO = new CountryLanguagePairDTO(null, null, locale.getLanguage());
            return new CountryLanguagePreference(sharedPreferences, PREF_KEY_REGIONAL_NEWS_COUNTRY_LANGUAGE, countryLanguagePairDTO);
        }
    }
}
