package com.tradehero.th.models.share.preference;

import android.support.annotation.NonNull;
import com.tradehero.th.api.social.SocialNetworkEnum;

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
