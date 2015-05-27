package com.tradehero.th.fragments.settings;

import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.auth.AuthenticationProvider;
import com.tradehero.th.auth.SocialAuth;
import dagger.Module;
import dagger.Provides;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
