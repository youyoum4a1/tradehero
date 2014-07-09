package com.tradehero.th.models.share.preference;

import com.tradehero.th.api.social.SocialNetworkEnum;
import org.jetbrains.annotations.NotNull;

public class FacebookSharePreferenceDTO extends BaseSocialSharePreferenceDTO
{
    //<editor-fold desc="Constructors">
    public FacebookSharePreferenceDTO(boolean isShareEnabled)
    {
        super(isShareEnabled);
    }
    //</editor-fold>

    @NotNull @Override public SocialNetworkEnum getSocialNetworkEnum()
    {
        return SocialNetworkEnum.FB;
    }
}
