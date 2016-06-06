package com.androidth.general.fragments.settings;

import dagger.Module;

@Module(
        injects = {
                AdminSettingsFragment.class,
                SettingsProfileFragment.class,
                ProfileInfoView.class,
                SettingsReferralCodeFragment.class,
                AboutFragment.class,
                SettingsFragment.class,
                AskForReviewDialogFragment.class,
                AskForReviewSuggestedDialogFragment.class,
                AskForInviteDialogFragment.class,
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
}
