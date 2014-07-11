package com.tradehero.th.persistence.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.th.activities.SplashActivity;
import com.tradehero.th.fragments.settings.AdminSettingsFragment;
import com.tradehero.th.models.share.preference.SocialSharePreferenceDTOFactory;
import com.tradehero.th.models.share.preference.SocialShareSetPreference;
import com.tradehero.th.models.user.auth.CredentialsDTO;
import com.tradehero.th.models.user.auth.CredentialsDTOFactory;
import com.tradehero.th.models.user.auth.CredentialsSetPreference;
import com.tradehero.th.models.user.auth.MainCredentialsPreference;
import com.tradehero.common.annotation.ForUser;
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
    @Deprecated
    private static final String PREF_CURRENT_SESSION_TOKEN_KEY = "PREF_CURRENT_SESSION_TOKEN_KEY";
    @Deprecated
    private static final String PREF_CURRENT_AUTHENTICATION_TYPE_KEY = "PREF_CURRENT_AUTHENTICATION_TYPE_KEY";

    private static final String PREF_MAIN_CREDENTIALS_KEY = "PREF_MAIN_CREDENTIALS_KEY";
    private static final String PREF_SAVED_CREDENTIALS_KEY = "PREF_SAVED_CREDENTIALS_KEY";
    private static final String PREF_RESET_HELP_SCREENS = "PREF_RESET_HELP_SCREENS";
    private static final String PREF_PUSH_IDENTIFIER_SENT_FLAG = "PREF_PUSH_IDENTIFIER_SENT_FLAG";
    private static final String PREF_SAVED_PUSH_IDENTIFIER = "PREF_SAVED_PUSH_IDENTIFIER";
    private static final String PREF_FIRST_LAUNCH_FLAG = "PREF_FIRST_LAUNCH_FLAG";
    public static final String PREF_SOCIAL_SHARE_FLAG = "PREF_SAVED_SOCIAL_SHARE_FLAG";
    private static final String PREF_SAVED_SOCIAL_SHARE_KEY = "PREF_SAVED_SOCIAL_SHARE_KEY";

    private static final String USER_PREFERENCE_KEY = "th";
    private static final String APP_PREFERENCE_KEY = "th_app";

    @Provides @Singleton @ForUser SharedPreferences provideUserSharePreferences(Context context)
    {
        return context.getSharedPreferences(USER_PREFERENCE_KEY, Context.MODE_PRIVATE);
    }

    @Provides @Singleton @ForApp SharedPreferences provideAppSharePreferences(Context context)
    {
        return context.getSharedPreferences(APP_PREFERENCE_KEY, Context.MODE_PRIVATE);
    }

    @Provides @Singleton MainCredentialsPreference provideMainCredentialsPreference(@ForUser SharedPreferences sharedPreferences, CredentialsDTOFactory credentialsDTOFactory)
    {
        MainCredentialsPreference newPrefs = new MainCredentialsPreference(credentialsDTOFactory, sharedPreferences, PREF_MAIN_CREDENTIALS_KEY, "");

        { // TODO remove eventually. This is for transitioning the old credentials
            StringPreference oldTypePrefs = new StringPreference(sharedPreferences, PREF_CURRENT_AUTHENTICATION_TYPE_KEY, "");
            StringPreference oldTokenPrefs = new StringPreference(sharedPreferences, PREF_CURRENT_SESSION_TOKEN_KEY, "");
            CredentialsDTO oldCredentials = new CredentialsDTOFactory().createFromOldSessionToken(oldTypePrefs.get(), oldTokenPrefs);
            if (oldCredentials != null)
            {
                newPrefs.setCredentials(oldCredentials);
            }
            oldTypePrefs.delete();
            oldTokenPrefs.delete();
        }
        return newPrefs;
    }

    @Provides @AuthHeader String provideAuthenticationHeader(MainCredentialsPreference mainCredentialsPreference)
    {
        CredentialsDTO currentCredentials = mainCredentialsPreference.getCredentials();
        if (currentCredentials != null)
        {
            return String.format("%1$s %2$s", currentCredentials.getAuthType(), currentCredentials.getAuthHeaderParameter());
        }
        return null;
    }

    @Provides @Singleton @SavedCredentials StringPreference provideMainCredentialsPreference(MainCredentialsPreference mainCredentialsPreference)
    {
        return mainCredentialsPreference;
    }

    @Provides @Singleton CredentialsSetPreference provideSavedPrefCredentials(@ForUser SharedPreferences sharedPreferences, CredentialsDTOFactory credentialsDTOFactory)
    {
        return new CredentialsSetPreference(credentialsDTOFactory, sharedPreferences, PREF_SAVED_CREDENTIALS_KEY, new HashSet<String>());
    }

    @Provides @Singleton @SavedCredentials StringSetPreference provideSavedPrefCredentials(CredentialsSetPreference credentialsSetPreference)
    {
        return credentialsSetPreference;
    }

    @Provides @Singleton SocialShareSetPreference provideSocialSharePref(@ForUser SharedPreferences sharedPreferences,
            SocialSharePreferenceDTOFactory sharePreferenceDTOFactory)
    {
        return new SocialShareSetPreference(sharePreferenceDTOFactory, sharedPreferences, PREF_SAVED_SOCIAL_SHARE_KEY, new HashSet<String>());
    }

    @Provides @Singleton @ResetHelpScreens BooleanPreference provideResetHelpScreen(@ForUser SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_RESET_HELP_SCREENS, false);
    }

    @Provides @Singleton @SavedPushDeviceIdentifier StringPreference provideSavedPushIdentifier(@ForUser SharedPreferences sharedPreferences)
    {
        return new StringPreference(sharedPreferences, PREF_SAVED_PUSH_IDENTIFIER, "");
    }

    @Provides @Singleton @BaiduPushDeviceIdentifierSentFlag BooleanPreference providePushIdentifierSentFlag(@ForUser SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_PUSH_IDENTIFIER_SENT_FLAG, false);
    }

    @Provides @Singleton @FirstLaunch BooleanPreference provideFirstLaunchPreference(@ForApp SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_FIRST_LAUNCH_FLAG, true);
    }
}
