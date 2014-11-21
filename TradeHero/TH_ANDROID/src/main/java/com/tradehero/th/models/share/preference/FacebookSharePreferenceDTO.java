package com.tradehero.th.models.share.preference;

import android.support.annotation.NonNull;
import com.tradehero.th.api.social.SocialNetworkEnum;

public class FacebookSharePreferenceDTO extends BaseSocialSharePreferenceDTO
{
    //<editor-fold desc="Constructors">
    public FacebookSharePreferenceDTO(boolean isShareEnabled)
    {
        super(isShareEnabled);
    }
    //</editor-fold>

    @NonNull @Override public SocialNetworkEnum getSocialNetworkEnum()
    {
        return SocialNetworkEnum.FB;
    }
}
