package com.ayondo.academy.fragments.discovery;

import android.content.SharedPreferences;
import com.tradehero.common.annotation.ForUser;
import com.ayondo.academy.api.news.CountryLanguagePairDTO;
import com.ayondo.academy.fragments.discovery.newsfeed.DiscoveryNewsfeedFragment;
import com.ayondo.academy.fragments.discovery.newsfeed.NewsfeedPaginatedAdapter;
import dagger.Module;
import dagger.Provides;
import java.util.Locale;
import javax.inject.Singleton;

@Module(
        injects = {
                DiscoveryMainFragment.class,
                DiscoveryNewsfeedFragment.class,
                NewsHeadlineFragment.class,
                NewsfeedPaginatedAdapter.class,
                DiscoveryDiscussionFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentDiscoveryModule
{
    private static final String PREF_KEY_REGIONAL_NEWS_COUNTRY_LANGUAGE = "PREF_KEY_REGIONAL_NEWS_COUNTRY_LANGUAGE";

    @Provides @Singleton @RegionalNews
    public CountryLanguagePreference provideRegionalNewsCountryLanguagePreference(@ForUser SharedPreferences sharedPreferences, Locale locale)
    {
        CountryLanguagePairDTO countryLanguagePairDTO = new CountryLanguagePairDTO(null, null, locale.getLanguage());
        return new CountryLanguagePreference(sharedPreferences, PREF_KEY_REGIONAL_NEWS_COUNTRY_LANGUAGE, countryLanguagePairDTO);
    }
}
