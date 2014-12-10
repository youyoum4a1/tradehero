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
    @Provides(type = Provides.Type.SET_VALUES)
    Set<SocialConnectSettingViewHolder> provideSocialConnectSettingViewHolderSet(
            SocialConnectFacebookSettingViewHolder socialConnectFacebookSettingViewHolder,
            SocialConnectLinkedInSettingViewHolder socialConnectLinkedInSettingViewHolder,
            SocialConnectQQSettingViewHolder socialConnectQQSettingViewHolder,
            SocialConnectTwitterSettingViewHolder socialConnectTwitterSettingViewHolder,
            SocialConnectWeiboSettingViewHolder socialConnectWeiboSettingViewHolder,
            @SocialAuth Map<SocialNetworkEnum, AuthenticationProvider> authenticationProviderMap
    )
    {
        Set<SocialNetworkEnum> applicableSocialNetwork = authenticationProviderMap.keySet();
        Set<SocialConnectSettingViewHolder> allSocialConnectSettingViewHolders = new HashSet<>();
        if (applicableSocialNetwork.contains(SocialNetworkEnum.FB))
        {
            allSocialConnectSettingViewHolders.add(socialConnectFacebookSettingViewHolder);
        }
        if (applicableSocialNetwork.contains(SocialNetworkEnum.LN))
        {
            allSocialConnectSettingViewHolders.add(socialConnectLinkedInSettingViewHolder);
        }
        if (applicableSocialNetwork.contains(SocialNetworkEnum.QQ))
        {
            allSocialConnectSettingViewHolders.add(socialConnectQQSettingViewHolder);
        }
        if (applicableSocialNetwork.contains(SocialNetworkEnum.TW))
        {
            allSocialConnectSettingViewHolders.add(socialConnectTwitterSettingViewHolder);
        }
        if (applicableSocialNetwork.contains(SocialNetworkEnum.WB))
        {
            allSocialConnectSettingViewHolders.add(socialConnectWeiboSettingViewHolder);
        }
        return allSocialConnectSettingViewHolders;
    }
}
