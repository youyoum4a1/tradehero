package com.tradehero.th.fragments.settings;

import com.tradehero.th.api.social.SocialNetworkEnum;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class SocialConnectSettingViewHolderContainer implements SettingViewHolder
{
    @NotNull protected SocialConnectFacebookSettingViewHolder socialConnectFacebookSettingViewHolder;
    @NotNull protected SocialConnectLinkedInSettingViewHolder socialConnectLinkedInSettingViewHolder;
    @NotNull protected SocialConnectQQSettingViewHolder socialConnectQQSettingViewHolder;
    @NotNull protected SocialConnectTwitterSettingViewHolder socialConnectTwitterSettingViewHolder;
    @NotNull protected SocialConnectWeiboSettingViewHolder socialConnectWeiboSettingViewHolder;

    //<editor-fold desc="Constructors">
    @Inject public SocialConnectSettingViewHolderContainer(
            @NotNull SocialConnectFacebookSettingViewHolder socialConnectFacebookSettingViewHolder,
            @NotNull SocialConnectLinkedInSettingViewHolder socialConnectLinkedInSettingViewHolder,
            @NotNull SocialConnectQQSettingViewHolder socialConnectQQSettingViewHolder,
            @NotNull SocialConnectTwitterSettingViewHolder socialConnectTwitterSettingViewHolder,
            @NotNull SocialConnectWeiboSettingViewHolder socialConnectWeiboSettingViewHolder)
    {
        this.socialConnectFacebookSettingViewHolder = socialConnectFacebookSettingViewHolder;
        this.socialConnectLinkedInSettingViewHolder = socialConnectLinkedInSettingViewHolder;
        this.socialConnectQQSettingViewHolder = socialConnectQQSettingViewHolder;
        this.socialConnectTwitterSettingViewHolder = socialConnectTwitterSettingViewHolder;
        this.socialConnectWeiboSettingViewHolder = socialConnectWeiboSettingViewHolder;
    }
    //</editor-fold>

    @Override public void initViews(@NotNull DashboardPreferenceFragment preferenceFragment)
    {
        socialConnectFacebookSettingViewHolder.initViews(preferenceFragment);
        socialConnectLinkedInSettingViewHolder.initViews(preferenceFragment);
        socialConnectQQSettingViewHolder.initViews(preferenceFragment);
        socialConnectTwitterSettingViewHolder.initViews(preferenceFragment);
        socialConnectWeiboSettingViewHolder.initViews(preferenceFragment);
    }

    @Override public void destroyViews()
    {
        socialConnectWeiboSettingViewHolder.destroyViews();
        socialConnectTwitterSettingViewHolder.destroyViews();
        socialConnectQQSettingViewHolder.destroyViews();
        socialConnectLinkedInSettingViewHolder.destroyViews();
        socialConnectFacebookSettingViewHolder.destroyViews();
    }

    public void changeSharing(SocialNetworkEnum socialNetworkEnum, boolean enable)
    {
        switch (socialNetworkEnum)
        {
            case FB:
                socialConnectFacebookSettingViewHolder.changeSharing(enable);
                break;
            case LN:
                socialConnectLinkedInSettingViewHolder.changeSharing(enable);
                break;
            case QQ:
                socialConnectQQSettingViewHolder.changeSharing(enable);
                break;
            case TW:
                socialConnectTwitterSettingViewHolder.changeSharing(enable);
                break;
            case WB:
                socialConnectWeiboSettingViewHolder.changeSharing(enable);
                break;
        }
    }
}
