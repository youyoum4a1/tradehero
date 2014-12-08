package com.tradehero.th.fragments.settings;

import android.content.IntentFilter;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.auth.AuthenticationProvider;
import com.tradehero.th.auth.SocialAuth;
import dagger.Component;
import dagger.Provides;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component(modules = { FragmentSettingUIComponent.Module.class })
public interface FragmentSettingUIComponent
{
    void injectAdminSettingsFragment(AdminSettingsFragment target);
    void injectSettingsProfileFragment(SettingsProfileFragment target);
    void injectProfileInfoView(ProfileInfoView target);
    void injectImagePickerView(ImagePickerView target);
    void injectSettingsReferralCodeFragment(SettingsReferralCodeFragment target);
    void injectAboutFragment(AboutFragment target);
    void injectSettingsFragment(SettingsFragment target);
    void injectAskForReviewDialogFragment(AskForReviewDialogFragment target);
    void injectAskForReviewSuggestedDialogFragment(AskForReviewSuggestedDialogFragment target);
    void injectAskForInviteDialogFragment(AskForInviteDialogFragment target);
    void injectUserFriendDTOView(UserFriendDTOView target);
    void injectSettingsTransactionHistoryFragment(SettingsTransactionHistoryFragment target);
    void injectSettingsPayPalFragment(SettingsPayPalFragment target);
    void injectSettingsAlipayFragment(SettingsAlipayFragment target);
    void injectReferralCodeUnreadPreference(ReferralCodeUnreadPreference target);

    @dagger.Module
    static class Module
    {
        public static final String SEND_LOVE_INTENT_ACTION_NAME = "com.tradehero.th.setting.sendlove.ALERT";

        @Provides @ForSendLove IntentFilter providesIntentFilterSendLove()
        {
            return new IntentFilter(SEND_LOVE_INTENT_ACTION_NAME);
        }

        @Provides(type = Provides.Type.SET_VALUES) Set<SocialConnectSettingViewHolder> provideSocialConnectSettingViewHolderSet(
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
}
