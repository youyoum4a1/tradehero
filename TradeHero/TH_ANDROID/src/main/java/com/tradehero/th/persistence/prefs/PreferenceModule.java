package com.tradehero.th.persistence.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.th.activities.SplashActivity;
import com.tradehero.th.api.translation.UserTranslationSettingDTOFactory;
import com.tradehero.th.fragments.settings.AdminSettingsFragment;
import com.tradehero.th.models.share.preference.SocialSharePreferenceDTOFactory;
import com.tradehero.th.models.share.preference.SocialShareSetPreference;
import com.tradehero.th.models.user.auth.CredentialsDTO;
import com.tradehero.th.models.user.auth.CredentialsDTOFactory;
import com.tradehero.th.models.user.auth.CredentialsSetPreference;
import com.tradehero.th.models.user.auth.MainCredentialsPreference;
import com.tradehero.th.persistence.translation.UserTranslationSettingPreference;
import dagger.Module;
import dagger.Provides;
import java.util.HashSet;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

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
    private static final String PREF_FIRST_SHOW_REFERRAL_CODE_FLAG = "PREF_FIRST_SHOW_REFERRAL_CODE_FLAG";
    private static final String PREF_BIND_GUEST_USER_FLAG = "PREF_BIND_GUEST_USER_FLAG";
    private static final String PREF_SHARE_DIALOG_KEY_FLAG = "PREF_SHARE_DIALOG_KEY_FLAG";
    private static final String PREF_SHARE_DIALOG_TOTAL_VALUE_KEY_FLAG = "PREF_SHARE_DIALOG_TOTAL_VALUE_KEY_FLAG";
    private static final String PREF_SHARE_DIALOG_ROI_VALUE_KEY_FLAG = "PREF_SHARE_DIALOG_ROI_VALUE_KEY_FLAG";
    private static final String PREF_SHARE_DIALOG_AFTER_SCORE_KEY_FLAG = "PREF_SHARE_DIALOG_AFTER_SCORE_KEY_FLAG";
    private static final String PREF_SHARE_DIALOG_FOLLOWER_COUNT_KEY_FLAG = "PREF_SHARE_DIALOG_FOLLOWER_COUNT_KEY_FLAG";
    private static final String PREF_DEVICE_ID_FLAG = "PREF_DEVICE_ID_FLAG";
    private static final String PREF_SHARE_SHEET_TITLE_CACHE_FLAG = "PREF_SHARE_SHEET_TITLE_CACHE_FLAG";
    public static final String PREF_SOCIAL_SHARE_FLAG = "PREF_SAVED_SOCIAL_SHARE_FLAG";
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

    @Provides @Singleton @BaiduPushDeviceIdentifierSentFlag BooleanPreference providePushIdentifierSentFlag(@ForUser SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_PUSH_IDENTIFIER_SENT_FLAG, false);
    }

    @Provides @Singleton @FirstLaunch BooleanPreference provideFirstLaunchPreference(@ForApp SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_FIRST_LAUNCH_FLAG, true);
    }

    @Provides @Singleton @FirstShowReferralCodeDialog BooleanPreference provideFirstShowReferralCodeDialogPreference(@ForUser SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_FIRST_SHOW_REFERRAL_CODE_FLAG, true);
    }

    @Provides @Singleton @DiviceID StringPreference provideDeviceIDPreference(@ForApp SharedPreferences sharedPreferences)
    {
        return new StringPreference(sharedPreferences, PREF_DEVICE_ID_FLAG, "");
    }

    @Provides @Singleton @BindGuestUser BooleanPreference provideBindGuestPreference(@ForApp SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_BIND_GUEST_USER_FLAG, false);
    }

    @Provides @Singleton @ShareSheetTitleCache StringPreference provideShareSheetTitleCachePreference(@ForUser SharedPreferences sharedPreferences)
    {
        return new StringPreference(sharedPreferences, PREF_SHARE_SHEET_TITLE_CACHE_FLAG, "");
    }

    @Provides @Singleton @ShareDialogKey BooleanPreference provideShareDialogKeyPreference(@ForUser SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_SHARE_DIALOG_KEY_FLAG, true);
    }
    //总资产数达到15w
    @Provides @Singleton @ShareDialogTotalValueKey BooleanPreference provideShareDialogTotalValueKeyPreference(@ForApp SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_SHARE_DIALOG_TOTAL_VALUE_KEY_FLAG, true);
    }
    //持有股票收益率涨副超过 10% 弹窗提示分享
    @Provides @Singleton @ShareDialogROIValueKey BooleanPreference provideShareDialogROIValueKeyPreference(@ForApp SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_SHARE_DIALOG_ROI_VALUE_KEY_FLAG, true);
    }
    //评分后
    @Provides @Singleton @ShareDialogAfterScoreKey BooleanPreference provideShareDialogAfterScoreKeyPreference(@ForApp SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_SHARE_DIALOG_AFTER_SCORE_KEY_FLAG, true);
    }
    //粉丝数达到8人
    @Provides @Singleton @ShareDialogFollowerCountKey BooleanPreference provideShareDialogFollowerCountKeyPreference(@ForApp SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_SHARE_DIALOG_FOLLOWER_COUNT_KEY_FLAG, true);
    }
}
