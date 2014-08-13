package com.tradehero.th.fragments.settings;

import android.support.v4.preference.PreferenceFragment;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.SocialNetworkFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.QQUtils;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SocialConnectQQSettingViewHolder extends SocialConnectSettingViewHolder
{
    @NotNull protected final Lazy<QQUtils> qqUtils;

    //<editor-fold desc="Constructors">
    @Inject public SocialConnectQQSettingViewHolder(
            @NotNull CurrentUserId currentUserId,
            @NotNull UserProfileCache userProfileCache,
            @NotNull ProgressDialogUtil progressDialogUtil,
            @NotNull AlertDialogUtil alertDialogUtil,
            @NotNull SocialServiceWrapper socialServiceWrapper,
            @NotNull Lazy<QQUtils> qqUtils)
    {
        super(currentUserId, userProfileCache, progressDialogUtil, alertDialogUtil, socialServiceWrapper);
        this.qqUtils = qqUtils;
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_sharing_qq;
    }

    @Override @Nullable protected String getSocialNetworkName()
    {
        PreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (preferenceFragmentCopy != null)
        {
            return preferenceFragmentCopy.getString(R.string.tencent_qq);
        }
        return null;
    }

    @Override protected int getLinkingDialogTitle()
    {
        return R.string.tencent_qq;
    }

    @Override protected int getLinkingDialogMessage()
    {
        return R.string.authentication_connecting_to_qq;
    }

    @Override protected int getUnlinkingProgressDialogTitle()
    {
        return R.string.tencent_qq;
    }

    @Override protected int getUnlinkingProgressDialogMessage()
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
            PreferenceFragment preferenceFragmentCopy = preferenceFragment;
            if (preferenceFragmentCopy != null)
            {
                qqUtils.get().logIn(
                        preferenceFragmentCopy.getActivity(),
                        middleSocialConnectLogInCallback);
            }
        }
        return returned;
    }

    @Override protected void effectUnlink()
    {
        super.effectUnlink();
        detachMiddleServerDisconnectCallback();
        middleCallbackDisconnect = socialServiceWrapper.disconnect(
                currentUserId.toUserBaseKey(),
                new SocialNetworkFormDTO(SocialNetworkEnum.QQ),
                createSocialDisconnectCallback());
    }

    @Override protected void updateSocialConnectStatus(@NotNull UserProfileDTO updatedUserProfileDTO)
    {
        if (clickablePref != null)
        {
            clickablePref.setChecked(updatedUserProfileDTO.qqLinked);
        }
    }
}
