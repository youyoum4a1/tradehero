package com.tradehero.th.fragments.settings;

import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.SocialNetworkFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.WeiboUtils;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class SocialConnectWeiboSettingViewHolder extends SocialConnectSettingViewHolder
{
    @NotNull protected final Lazy<WeiboUtils> weiboUtils;

    //<editor-fold desc="Constructors">
    @Inject public SocialConnectWeiboSettingViewHolder(
            @NotNull CurrentUserId currentUserId,
            @NotNull UserProfileCache userProfileCache,
            @NotNull ProgressDialogUtil progressDialogUtil,
            @NotNull SocialServiceWrapper socialServiceWrapper,
            @NotNull Lazy<WeiboUtils> weiboUtils)
    {
        super(currentUserId, userProfileCache, progressDialogUtil, socialServiceWrapper);
        this.weiboUtils = weiboUtils;
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_sharing_weibo;
    }

    @Override protected String getSocialNetworkName()
    {
        return preferenceFragment.getString(R.string.sina_weibo);
    }

    @Override protected int getLinkingDialogTitle()
    {
        return R.string.sina_weibo;
    }

    @Override protected int getLinkingDialogMessage()
    {
        return R.string.authentication_connecting_to_weibo;
    }

    @Override protected int getUnlinkingDialogTitle()
    {
        return R.string.sina_weibo;
    }

    @Override protected int getUnlinkingDialogMessage()
    {
        return R.string.authentication_connecting_tradehero_only;
    }

    @Override protected boolean changeSharing(boolean enable)
    {
        boolean returned = super.changeSharing(enable);
        if (enable)
        {
            detachMiddleSocialConnectLogInCallback();
            middleSocialConnectLogInCallback = createMiddleSocialConnectLogInCallback();
            weiboUtils.get().logIn(
                    preferenceFragment.getActivity(),
                    middleSocialConnectLogInCallback);
        }
        else
        {
            detachMiddleServerDisconnectCallback();
            middleCallbackDisconnect = socialServiceWrapper.disconnect(
                    currentUserId.toUserBaseKey(),
                    new SocialNetworkFormDTO(SocialNetworkEnum.WB),
                    createSocialDisconnectCallback());
        }
        return returned;
    }

    @Override protected void updateSocialConnectStatus(@NotNull UserProfileDTO updatedUserProfileDTO)
    {
        if (clickablePref != null)
        {
            clickablePref.setChecked(updatedUserProfileDTO.wbLinked);
        }
    }
}
