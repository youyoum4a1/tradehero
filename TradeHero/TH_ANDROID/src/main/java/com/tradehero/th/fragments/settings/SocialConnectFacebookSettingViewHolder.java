package com.tradehero.th.fragments.settings;

import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.SocialNetworkFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.FacebookUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class SocialConnectFacebookSettingViewHolder extends SocialConnectSettingViewHolder
{
    @NotNull protected final Lazy<FacebookUtils> facebookUtils;

    //<editor-fold desc="Constructors">
    @Inject public SocialConnectFacebookSettingViewHolder(
            @NotNull CurrentUserId currentUserId,
            @NotNull UserProfileCache userProfileCache,
            @NotNull ProgressDialogUtil progressDialogUtil,
            @NotNull SocialServiceWrapper socialServiceWrapper,
            @NotNull Lazy<FacebookUtils> facebookUtils)
    {
        super(currentUserId, userProfileCache, progressDialogUtil, socialServiceWrapper);
        this.facebookUtils = facebookUtils;
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_sharing_facebook;
    }

    @Override protected String getSocialNetworkName()
    {
        return preferenceFragment.getString(R.string.facebook);
    }

    @Override protected int getLinkingDialogTitle()
    {
        return R.string.facebook;
    }

    @Override protected int getLinkingDialogMessage()
    {
        return R.string.authentication_connecting_to_facebook;
    }

    @Override protected int getUnlinkingDialogTitle()
    {
        return R.string.facebook;
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
            facebookUtils.get().logIn(
                    preferenceFragment.getActivity(),
                    middleSocialConnectLogInCallback);
        }
        else
        {
            detachMiddleServerDisconnectCallback();
            middleCallbackDisconnect = socialServiceWrapper.disconnect(
                    currentUserId.toUserBaseKey(),
                    new SocialNetworkFormDTO(SocialNetworkEnum.FB),
                    createSocialDisconnectCallback());
        }
        return returned;
    }

    @Override protected void updateSocialConnectStatus(@NotNull UserProfileDTO updatedUserProfileDTO)
    {
        if (clickablePref != null)
        {
            clickablePref.setChecked(updatedUserProfileDTO.fbLinked);
        }
    }
}
