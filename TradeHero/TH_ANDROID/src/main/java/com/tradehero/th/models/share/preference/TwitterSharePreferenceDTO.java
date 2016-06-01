package com.ayondo.academy.models.share.preference;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.social.SocialNetworkEnum;

public class TwitterSharePreferenceDTO extends BaseSocialSharePreferenceDTO
{
    //<editor-fold desc="Constructors">
    public TwitterSharePreferenceDTO(boolean isShareEnabled)
    {
        super(isShareEnabled);
    }
    //</editor-fold>

    @NonNull @Override public SocialNetworkEnum getSocialNetworkEnum()
    {
        return SocialNetworkEnum.TW;
    }
}
