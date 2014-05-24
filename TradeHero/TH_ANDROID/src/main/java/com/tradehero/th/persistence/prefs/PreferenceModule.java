package com.tradehero.th.persistence.prefs;

import android.content.SharedPreferences;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.th.activities.SplashActivity;
import com.tradehero.th.fragments.settings.AdminSettingsFragment;
import com.tradehero.th.models.user.auth.CredentialsDTOFactory;
import com.tradehero.th.models.user.auth.CredentialsSetPreference;
import com.tradehero.th.models.user.auth.MainCredentialsPreference;
import dagger.Module;
import dagger.Provides;
import java.util.HashSet;
import javax.inject.Singleton;

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
    private static final String PREF_CURRENT_AUTHENTICATION_TYPE_KEY = "PREF_CURRENT_AUTHENTICATION_TYPE_KEY";
    private static final String PREF_MAIN_CREDENTIALS_KEY = "PREF_MAIN_CREDENTIALS_KEY";
    private static final String PREF_SAVED_CREDENTIALS_KEY = "PREF_SAVED_CREDENTIALS_KEY";
    private static final String PREF_RESET_HELP_SCREENS = "PREF_RESET_HELP_SCREENS";
    private static final String PREF_PUSH_IDENTIFIER_SENT_FLAG = "PREF_PUSH_IDENTIFIER_SENT_FLAG";
    private static final String PREF_SAVED_PUSH_IDENTIFIER = "PREF_SAVED_PUSH_IDENTIFIER";

    @Provides @Singleton @AuthenticationType StringPreference provideCurrentAuthenticationType(SharedPreferences sharedPreferences)
    {
        return new StringPreference(sharedPreferences, PREF_CURRENT_AUTHENTICATION_TYPE_KEY, null);
    }

    @Provides @Singleton MainCredentialsPreference provideMainCredentialsPreference(SharedPreferences sharedPreferences, CredentialsDTOFactory credentialsDTOFactory)
    {
        return new MainCredentialsPreference(credentialsDTOFactory, sharedPreferences, PREF_MAIN_CREDENTIALS_KEY, null);
    }

    @Provides @Singleton @SavedCredentials StringPreference provideMainCredentialsPreference(MainCredentialsPreference mainCredentialsPreference)
    {
        return mainCredentialsPreference;
    }

    @Provides @Singleton CredentialsSetPreference provideSavedPrefCredentials(SharedPreferences sharedPreferences, CredentialsDTOFactory credentialsDTOFactory)
    {
        return new CredentialsSetPreference(credentialsDTOFactory, sharedPreferences, PREF_SAVED_CREDENTIALS_KEY, new HashSet<String>());
    }

    @Provides @Singleton @SavedCredentials StringSetPreference provideSavedPrefCredentials(CredentialsSetPreference credentialsSetPreference)
    {
        return credentialsSetPreference;
    }

    @Provides @Singleton @ResetHelpScreens BooleanPreference provideResetHelpScreen(SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_RESET_HELP_SCREENS, false);
    }

    @Provides @Singleton @SavedPushDeviceIdentifier StringPreference provideSavedPushIdentifier(SharedPreferences sharedPreferences)
    {
        return new StringPreference(sharedPreferences, PREF_SAVED_PUSH_IDENTIFIER, null);
    }

    @Provides @Singleton @BaiduPushDeviceIdentifierSentFlag BooleanPreference providePushIdentifierSentFlag(SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_PUSH_IDENTIFIER_SENT_FLAG, false);
    }
}
