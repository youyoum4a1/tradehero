package com.tradehero.th.persistence.prefs;

import android.content.SharedPreferences;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.th.activities.SplashActivity;
import com.tradehero.th.fragments.settings.AdminSettingsFragment;
import dagger.Module;
import dagger.Provides;
import java.util.HashSet;
import javax.inject.Singleton;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/5/14 Time: 5:43 PM Copyright (c) TradeHero
 */
@Module(
        injects = {
                SplashActivity.class,
                AdminSettingsFragment.class,
        },
        complete = false,
        library = true
)
public class PreferenceModule
{
    private static final String PREF_CURRENT_SESSION_TOKEN_KEY = "PREF_CURRENT_SESSION_TOKEN_KEY";
    private static final String PREF_CURRENT_AUTHENTICATION_TYPE_KEY = "PREF_CURRENT_AUTHENTICATION_TYPE_KEY";
    private static final String PREF_SAVED_CREDENTIALS_KEY = "PREF_SAVED_CREDENTIALS_KEY";
    private static final String PREF_RESET_HELP_SCREENS = "PREF_RESET_HELP_SCREENS";

    @Provides @Singleton @SessionToken StringPreference provideCurrentSessionToken(SharedPreferences sharedPreferences)
    {
        return new StringPreference(sharedPreferences, PREF_CURRENT_SESSION_TOKEN_KEY, null);
    }

    @Provides @Singleton @AuthenticationType StringPreference provideCurrentAuthenticationType(SharedPreferences sharedPreferences)
    {
        return new StringPreference(sharedPreferences, PREF_CURRENT_AUTHENTICATION_TYPE_KEY, null);
    }

    @Provides @Singleton @SavedCredentials StringSetPreference provideSavedCredentials(SharedPreferences sharedPreferences)
    {
        return new StringSetPreference(sharedPreferences, PREF_SAVED_CREDENTIALS_KEY, new HashSet<String>());
    }

    @Provides @Singleton @ResetHelpScreens BooleanPreference provideResetHelpScreen(SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_RESET_HELP_SCREENS, false);
    }
}
