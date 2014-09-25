package com.tradehero.th.persistence.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.tradehero.common.annotation.ForApp;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.th.api.translation.UserTranslationSettingDTOFactory;
import com.tradehero.th.models.share.preference.SocialSharePreferenceDTOFactory;
import com.tradehero.th.models.share.preference.SocialShareSetPreference;
import com.tradehero.th.models.user.auth.CredentialsDTO;
import com.tradehero.th.models.user.auth.CredentialsDTOFactory;
import com.tradehero.th.models.user.auth.CredentialsSetPreference;
import com.tradehero.th.models.user.auth.MainCredentialsPreference;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import com.tradehero.th.persistence.translation.UserTranslationSettingPreference;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
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
    private static final String PREF_FIRST_SHOW_INVITE_CODE_FLAG = "PREF_FIRST_SHOW_REFERRAL_CODE_FLAG";
    private static final String PREF_FIRST_SHOW_ON_BOARD_FLAG = "PREF_FIRST_SHOW_ON_BOARD_FLAG";
    private static final String PREF_SHOW_ASK_FOR_REVIEW_FLAG = "PREF_SHOW_ASK_FOR_REVIEW_FLAG";
    private static final String PREF_SHOW_ASK_FOR_INVITE_FLAG = "PREF_SHOW_ASK_FOR_INVITE_FLAG";
    private static final String PREF_SHOW_ASK_FOR_INVITE_TIMES_FLAG = "PREF_SHOW_ASK_FOR_INVITE_TIMES_FLAG";
    private static final String PREF_SHOW_MARKET_CLOSED = "PREF_SHOW_MARKET_CLOSED";
    private static final String PREF_IS_VISITED_REFERRAL_CODE_SETTINGS_FLAG = "PREF_IS_VISITED_REFERRAL_CODE_SETTINGS_FLAG";
    private static final String PREF_SOCIAL_SHARE_FLAG = "PREF_SAVED_SOCIAL_SHARE_FLAG";
    private static final String PREF_SAVED_SOCIAL_SHARE_KEY = "PREF_SAVED_SOCIAL_SHARE_KEY";
    private static final String PREF_SAVED_TRANSLATION_SETTING_KEY = "PREF_SAVED_TRANSLATION_SETTING_KEY";

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

    @Provides @Singleton SocialShareSetPreference provideSocialSharePref(
            @ForUser @NotNull SharedPreferences sharedPreferences,
            @NotNull SocialSharePreferenceDTOFactory sharePreferenceDTOFactory)
    {
        return new SocialShareSetPreference(
                sharePreferenceDTOFactory,
                sharedPreferences,
                PREF_SAVED_SOCIAL_SHARE_KEY,
                new HashSet<String>());
    }

    @Provides @Singleton UserTranslationSettingPreference provideUserTranslationSettingPref(
            @ForUser @NotNull SharedPreferences sharedPreferences,
            @NotNull UserTranslationSettingDTOFactory userTranslationSettingDTOFactory)
    {
        return new UserTranslationSettingPreference(
                userTranslationSettingDTOFactory,
                sharedPreferences,
                PREF_SAVED_TRANSLATION_SETTING_KEY,
                new HashSet<String>());
    }

    @Provides @Singleton @ResetHelpScreens BooleanPreference provideResetHelpScreen(@ForUser SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_RESET_HELP_SCREENS, false);
    }

    @Provides @Singleton @SavedPushDeviceIdentifier StringPreference provideSavedPushIdentifier(@ForUser SharedPreferences sharedPreferences)
    {
        return new StringPreference(sharedPreferences, PREF_SAVED_PUSH_IDENTIFIER, "");
    }

    @Provides @Singleton @FirstLaunch BooleanPreference provideFirstLaunchPreference(@ForApp SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_FIRST_LAUNCH_FLAG, true);
    }

    @Provides @Singleton @ShowAskForReviewDialog TimingIntervalPreference provideAskForReviewDialogPreference(
            @ForApp SharedPreferences sharedPreferences)
    {
        return new TimingIntervalPreference(sharedPreferences, PREF_SHOW_ASK_FOR_REVIEW_FLAG, TimingIntervalPreference.YEAR);
    }

    @Provides @Singleton @FirstShowInviteCodeDialog BooleanPreference provideFirstShowInviteCodeDialogPreference(
            @ForUser SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_FIRST_SHOW_INVITE_CODE_FLAG, true);
    }

    @Provides @Singleton @FirstShowOnBoardDialog TimingIntervalPreference provideFirstShowOnBoardDialogTimingPreference(
            @ForApp SharedPreferences sharedPreferences) {
        return new TimingIntervalPreference(sharedPreferences, PREF_FIRST_SHOW_ON_BOARD_FLAG, TimingIntervalPreference.MONTH);
    }

    @Provides @Singleton @ShowAskForInviteDialog TimingIntervalPreference provideAskForInviteDialogPreference(
            @ForApp SharedPreferences sharedPreferences)
    {
        return new TimingIntervalPreference(sharedPreferences, PREF_SHOW_ASK_FOR_INVITE_FLAG, TimingIntervalPreference.WEEK);
    }

    @Provides @Singleton @ShowAskForInviteDialogCloseTimes IntPreference provideAskForInviteDialogCloseTimesPreference(
            @ForApp SharedPreferences sharedPreferences)
    {
        return new IntPreference(sharedPreferences, PREF_SHOW_ASK_FOR_INVITE_TIMES_FLAG, 1);
    }

    @Provides @Singleton @ShowMarketClosed TimingIntervalPreference provideShowMarketClosedIntervalPreference(
            @ForUser SharedPreferences sharedPreferences)
    {
        return new TimingIntervalPreference(sharedPreferences, PREF_SHOW_MARKET_CLOSED, 30 * TimingIntervalPreference.MINUTE);
    }

    @Provides @Singleton @IsVisitedReferralCodeSettings BooleanPreference provideIsVisitedReferralCodeSettingsPreference(
            @ForApp SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_IS_VISITED_REFERRAL_CODE_SETTINGS_FLAG, false);
    }
}
