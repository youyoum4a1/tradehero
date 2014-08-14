package com.tradehero.th.fragments.settings;

import android.support.v4.preference.PreferenceFragment;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.SocialNetworkFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.user.auth.FacebookCredentialsDTO;
import com.tradehero.th.models.user.auth.MainCredentialsPreference;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.FacebookUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SocialConnectFacebookSettingViewHolder extends SocialConnectSettingViewHolder
{
    @NotNull protected final Lazy<FacebookUtils> facebookUtils;

    //<editor-fold desc="Constructors">
    @Inject public SocialConnectFacebookSettingViewHolder(
            @NotNull CurrentUserId currentUserId,
            @NotNull UserProfileCache userProfileCache,
            @NotNull ProgressDialogUtil progressDialogUtil,
            @NotNull UserServiceWrapper userServiceWrapper,
            @NotNull AlertDialogUtil alertDialogUtil,
            @NotNull SocialServiceWrapper socialServiceWrapper,
            @NotNull Lazy<FacebookUtils> facebookUtils,
            @NotNull MainCredentialsPreference mainCredentialsPreference)
    {
        super(currentUserId, userProfileCache, progressDialogUtil, userServiceWrapper, alertDialogUtil, socialServiceWrapper,mainCredentialsPreference);
        this.facebookUtils = facebookUtils;
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_sharing_facebook;
    }

    @Override protected int getOrderIntResId()
    {
        return R.integer.key_settings_sharing_facebook_order;
    }

    @Override @Nullable protected String getSocialNetworkName()
    {
        PreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (preferenceFragmentCopy != null)
        {
            return preferenceFragmentCopy.getString(R.string.facebook);
        }
        return null;
    }

    @Override protected int getLinkingDialogTitle()
    {
        return R.string.facebook;
    }

    @Override protected int getLinkingDialogMessage()
    {
        return R.string.authentication_connecting_to_facebook;
    }

    @Override protected int getUnlinkingProgressDialogTitle()
    {
        return R.string.facebook;
    }

    @Override protected int getUnlinkingProgressDialogMessage()
    {
        return R.string.authentication_connecting_tradehero_only;
    }

    @Override protected boolean changeStatus(boolean enable)
    {
        boolean returned = super.changeStatus(enable);
        if (enable)
        {
            detachMiddleSocialConnectLogInCallback();
            middleSocialConnectLogInCallback = createMiddleSocialConnectLogInCallback();
            PreferenceFragment preferenceFragmentCopy = preferenceFragment;
            if (preferenceFragmentCopy != null)
            {
                facebookUtils.get().logIn(
                        preferenceFragmentCopy.getActivity(),
                        middleSocialConnectLogInCallback);
            }
        }
        return returned;
    }

    @Override protected void effectUnlink()
    {
        super.effectUnlink();
        if (!checkIsLoginType(FacebookCredentialsDTO.FACEBOOK_AUTH_TYPE))
        {
            detachMiddleServerDisconnectCallback();
            middleCallbackDisconnect = socialServiceWrapper.disconnect(
                    currentUserId.toUserBaseKey(),
                    new SocialNetworkFormDTO(SocialNetworkEnum.FB),
                    createSocialDisconnectCallback());
        }
    }

    @Override protected void updateStatus(@NotNull UserProfileDTO updatedUserProfileDTO)
    {
        if (clickablePref != null)
        {
            clickablePref.setChecked(updatedUserProfileDTO.fbLinked);
        }
    }
}
