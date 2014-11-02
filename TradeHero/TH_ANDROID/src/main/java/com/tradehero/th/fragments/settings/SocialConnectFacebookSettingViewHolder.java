package com.tradehero.th.fragments.settings;

import android.support.annotation.IntegerRes;
import android.support.annotation.StringRes;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.auth.FacebookAuthenticationProvider;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.prefs.AuthHeader;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class SocialConnectFacebookSettingViewHolder extends SocialConnectSettingViewHolder
{
    //<editor-fold desc="Constructors">
    @Inject public SocialConnectFacebookSettingViewHolder(
            @NotNull CurrentUserId currentUserId,
            @NotNull UserProfileCacheRx userProfileCache,
            @NotNull ProgressDialogUtil progressDialogUtil,
            @NotNull UserServiceWrapper userServiceWrapper,
            @NotNull AlertDialogUtil alertDialogUtil,
            @NotNull SocialServiceWrapper socialServiceWrapper,
            @NotNull FacebookAuthenticationProvider socialAuthenticationProvider,
            @NotNull UserProfileDTOUtil userProfileDTOUtil,
            @NotNull @AuthHeader String authToken)
    {
        super(currentUserId,
                userProfileCache,
                progressDialogUtil,
                userServiceWrapper,
                alertDialogUtil,
                socialServiceWrapper,
                socialAuthenticationProvider,
                userProfileDTOUtil,
                authToken);
    }
    //</editor-fold>

    @StringRes @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_sharing_facebook;
    }

    @IntegerRes @Override protected int getOrderIntResId()
    {
        return R.integer.key_settings_sharing_facebook_order;
    }

    @NotNull @Override protected SocialNetworkEnum getSocialNetworkEnum()
    {
        return SocialNetworkEnum.FB;
    }

    @StringRes @Override protected int getLinkingDialogMessage()
    {
        return R.string.authentication_connecting_to_facebook;
    }

    @StringRes @Override protected int getUnlinkingProgressDialogMessage()
    {
        return R.string.authentication_connecting_tradehero_only;
    }
}
