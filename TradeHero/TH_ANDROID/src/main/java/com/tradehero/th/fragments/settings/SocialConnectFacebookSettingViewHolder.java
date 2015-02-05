package com.tradehero.th.fragments.settings;

import android.content.Context;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.facebook.FacebookOperationCanceledException;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.models.share.SocialShareHelper;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.prefs.AuthHeader;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;

public class SocialConnectFacebookSettingViewHolder extends SocialConnectSettingViewHolder
{
    //<editor-fold desc="Constructors">
    @Inject public SocialConnectFacebookSettingViewHolder(
            @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull UserServiceWrapper userServiceWrapper,
            @NonNull SocialServiceWrapper socialServiceWrapper,
            @NonNull @AuthHeader String authToken,
            @NonNull SocialShareHelper socialShareHelper)
    {
        super(currentUserId,
                userProfileCache,
                userServiceWrapper,
                socialServiceWrapper,
                authToken,
                socialShareHelper);
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

    @Override protected void onChangeStatusError(@NonNull Context activityContext, @NonNull Throwable e)
    {
        if (!(e instanceof FacebookOperationCanceledException))
        {
            super.onChangeStatusError(activityContext, e);
        }
    }

    @NonNull @Override protected SocialNetworkEnum getSocialNetworkEnum()
    {
        return SocialNetworkEnum.FB;
    }

    @StringRes @Override protected int getUnlinkingProgressDialogMessage()
    {
        return R.string.authentication_connecting_tradehero_only;
    }
}
