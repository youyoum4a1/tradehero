package com.tradehero.th.persistence.prefs;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.api.translation.UserTranslationSettingDTOFactory;
import com.tradehero.th.models.share.preference.SocialShareSetPreference;
import com.tradehero.th.persistence.market.ExchangeMarketPreference;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import com.tradehero.th.persistence.translation.UserTranslationSettingPreference;
import dagger.Module;
import dagger.Provides;
import java.util.HashSet;
import javax.inject.Singleton;
import timber.log.Timber;

import static com.tradehero.th.utils.Constants.Auth.PARAM_ACCOUNT_TYPE;
import static com.tradehero.th.utils.Constants.Auth.PARAM_AUTHTOKEN_TYPE;

@Module(
        includes = {
                PreferenceGameLiveModule.class,
        },
        complete = false,
        library = true
)
public class PreferenceModule
{
    private static final String PREF_MAIN_CREDENTIALS_KEY = "PREF_MAIN_CREDENTIALS_KEY";
    private static final String PREF_SAVED_CREDENTIALS_KEY = "PREF_SAVED_CREDENTIALS_KEY";
    private static final String PREF_RESET_HELP_SCREENS = "PREF_RESET_HELP_SCREENS";
    private static final String PREF_SAVED_PUSH_IDENTIFIER = "PREF_SAVED_PUSH_IDENTIFIER";
    private static final String PREF_FIRST_LAUNCH_FLAG = "PREF_FIRST_LAUNCH_FLAG";
    private static final String PREF_FIRST_SHOW_ON_BOARD_FLAG = "PREF_FIRST_SHOW_ON_BOARD_FLAG";
    private static final String PREF_SHOW_ASK_FOR_REVIEW_FLAG = "PREF_SHOW_ASK_FOR_REVIEW_FLAG";
    private static final String PREF_SHOW_ASK_FOR_INVITE_FLAG = "PREF_SHOW_ASK_FOR_INVITE_FLAG";
    private static final String PREF_SHOW_ASK_FOR_INVITE_TIMES_FLAG = "PREF_SHOW_ASK_FOR_INVITE_TIMES_FLAG";
    private static final String PREF_SHOW_MARKET_CLOSED = "PREF_SHOW_MARKET_CLOSED";
    private static final String PREF_PREFERRED_EXCHANGE_MARKET = "PREF_PREFERRED_EXCHANGE_MARKET";
    private static final String PREF_IS_VISITED_REFERRAL_CODE_SETTINGS_FLAG = "PREF_IS_VISITED_REFERRAL_CODE_SETTINGS_FLAG";
    private static final String PREF_SOCIAL_SHARE_FLAG = "PREF_SAVED_SOCIAL_SHARE_FLAG";
    private static final String PREF_SAVED_SOCIAL_SHARE_KEY = "PREF_SAVED_SOCIAL_SHARE_KEY";

    private static final String PREF_SAVED_TRANSLATION_SETTING_KEY = "PREF_SAVED_TRANSLATION_SETTING_KEY";
    private static final String USER_PREFERENCE_KEY = "th";
    private static final String APP_PREFERENCE_KEY = "th_app";
    private static final String PREF_IS_ONBOARD_SHOWN_FLAG = "PREF_IS_ONBOARD_SHOWN";
    private static final String PREF_IS_FX_SHOWN_FLAG = "PREF_IS_FX_SHOWN_FLAG";

    public static final String PREF_ON_BOARDING_EXCHANGE = "PREF_ON_BOARDING_EXCHANGE";
    public static final String PREF_IS_LIVE_TRADING = "PREF_IS_LIVE_TRADING";
    public static final String PREF_IS_LIVE_LOGIN = "PREF_IS_LIVE_LOGIN";

    @Provides @Singleton @ForUser SharedPreferences provideUserSharePreferences(Context context)
    {
        return context.getSharedPreferences(USER_PREFERENCE_KEY, Context.MODE_PRIVATE);
    }

    @Provides @Singleton @ForApp SharedPreferences provideAppSharePreferences(Context context)
    {
        return context.getSharedPreferences(APP_PREFERENCE_KEY, Context.MODE_PRIVATE);
    }

    @Provides @Singleton SocialShareSetPreference provideSocialSharePref(
            @ForUser @NonNull SharedPreferences sharedPreferences)
    {
        return new SocialShareSetPreference(
                sharedPreferences,
                PREF_SAVED_SOCIAL_SHARE_KEY,
                new HashSet<String>());
    }

    @Provides @Singleton UserTranslationSettingPreference provideUserTranslationSettingPref(
            @NonNull Context context,
            @ForUser @NonNull SharedPreferences sharedPreferences,
            @NonNull UserTranslationSettingDTOFactory userTranslationSettingDTOFactory)
    {
        return new UserTranslationSettingPreference(
                context.getResources(),
                userTranslationSettingDTOFactory,
                sharedPreferences,
                PREF_SAVED_TRANSLATION_SETTING_KEY,
                new HashSet<String>());
    }

    @Provides @Singleton @ResetHelpScreens BooleanPreference provideResetHelpScreen(@ForUser SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_RESET_HELP_SCREENS, false);
    }

    @Provides @Singleton @SavedPushDeviceIdentifier StringPreference provideSavedPushIdentifier(@ForApp SharedPreferences sharedPreferences)
    {
        return new StringPreference(sharedPreferences, PREF_SAVED_PUSH_IDENTIFIER, "No Id");
    }

    @Provides @Singleton @FirstLaunch BooleanPreference provideFirstLaunchPreference(@ForApp SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_FIRST_LAUNCH_FLAG, true);
    }

    @Provides @Singleton @ShowAskForReviewDialog TimingIntervalPreference provideAskForReviewDialogPreference(
            @ForUser SharedPreferences sharedPreferences)
    {
        TimingIntervalPreference askForReview = new TimingIntervalPreference(sharedPreferences, PREF_SHOW_ASK_FOR_REVIEW_FLAG, TimingIntervalPreference.YEAR);
        askForReview.pushInFuture(TimingIntervalPreference.DAY); // Not to show on the first day
        return askForReview;
    }

    @Provides @Singleton @FirstShowOnBoardDialog TimingIntervalPreference provideFirstShowOnBoardDialogTimingPreference(
            @ForUser SharedPreferences sharedPreferences) {
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

    @Provides @Singleton @PreferredExchangeMarket ExchangeMarketPreference providePreferredExchangeMarketPreference(@ForUser SharedPreferences sharedPreferences)
    {
        return new ExchangeMarketPreference(sharedPreferences, PREF_PREFERRED_EXCHANGE_MARKET);
    }

    @Provides @Singleton @IsVisitedReferralCodeSettings BooleanPreference provideIsVisitedReferralCodeSettingsPreference(
            @ForApp SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_IS_VISITED_REFERRAL_CODE_SETTINGS_FLAG, false);
    }

    @Provides @Singleton @IsOnBoardShown BooleanPreference provideIsOnBoardShown(@ForApp SharedPreferences sharedPreferences)
    {
        // We show the on-boarding only when the server says firstTimeLogin
        return new BooleanPreference(sharedPreferences, PREF_IS_ONBOARD_SHOWN_FLAG, true);
    }

    @Provides @Singleton @IsFxShown BooleanPreference provideIsFxShown(@ForApp SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_IS_FX_SHOWN_FLAG, false);
    }

    @Provides @Singleton @IsLiveTrading BooleanPreference provideIsLiveTrading(@ForUser SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_IS_LIVE_TRADING, false);
    }

    @Provides @Singleton @IsLiveLogIn BooleanPreference provideIsLiveLogIn(@ForUser SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_IS_LIVE_LOGIN, false);
    }

    @Provides @AuthHeader String provideAuthenticationHeader(final Context context)
    {
        Account[] accounts = null;
        AccountManager accountManager = AccountManager.get(context);
        try
        {
            accounts = accountManager.getAccountsByType(PARAM_ACCOUNT_TYPE);
        } catch (SecurityException e)
        {
            Timber.e(e, "Failed to getAccountsByType");
        }

        if (accounts != null && accounts.length != 0)
        {
            for (Account account: accounts)
            {
                String token = accountManager.peekAuthToken(account, PARAM_AUTHTOKEN_TYPE);
                if (token != null)
                {
                    return token;
                }
            }
        }

        return null;
    }

    @Provides @Singleton @THPreference(PREF_ON_BOARDING_EXCHANGE) StringPreference provideOnBoardExchange(@ForUser SharedPreferences sharedPreferences)
    {
        return new StringPreference(sharedPreferences, PREF_ON_BOARDING_EXCHANGE, "");
    }
}
