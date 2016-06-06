package com.androidth.general.models.share.preference;

import android.support.annotation.NonNull;
import com.androidth.general.api.social.SocialNetworkEnum;

public class WeChatSharePreferenceDTO extends BaseSocialSharePreferenceDTO
{
    //<editor-fold desc="Constructors">
    public WeChatSharePreferenceDTO(boolean isShareEnabled)
    {
        super(isShareEnabled);
    }
    //</editor-fold>

    @NonNull @Override public SocialNetworkEnum getSocialNetworkEnum()
    {
        return SocialNetworkEnum.WECHAT;
    }
}
