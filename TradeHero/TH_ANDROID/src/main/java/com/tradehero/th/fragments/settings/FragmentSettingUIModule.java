package com.tradehero.th.fragments.settings;

import android.content.IntentFilter;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                AdminSettingsFragment.class,
                SettingsProfileFragment.class,
                ProfileInfoView.class,
                ImagePickerView.class,
                SettingsReferralCodeFragment.class,
                AboutFragment.class,
                SettingsFragment.class,
                AskForReviewDialogFragment.class,
                AskForReviewSuggestedDialogFragment.class,
                AskForInviteDialogFragment.class,
                UserFriendDTOView.class,
                SettingsTransactionHistoryFragment.class,
                SettingsPayPalFragment.class,
                SettingsAlipayFragment.class,
                ReferralCodeUnreadPreference.class
        },
        library = true,
        complete = false
)
public class FragmentSettingUIModule
{
    public static final String SEND_LOVE_INTENT_ACTION_NAME = "com.tradehero.th.setting.sendlove.ALERT";
    public static final String KEY_SEND_LOVE_BROADCAST = FragmentSettingUIModule.class.getName()+".sendLoveBroadcast";

    @Provides @ForSendLove IntentFilter providesIntentFilterSendLove()
    {
        return new IntentFilter(SEND_LOVE_INTENT_ACTION_NAME);
    }
}
