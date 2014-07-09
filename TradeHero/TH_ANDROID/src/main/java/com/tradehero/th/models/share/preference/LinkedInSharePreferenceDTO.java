package com.tradehero.th.models.share.preference;

import com.tradehero.th.api.social.SocialNetworkEnum;
import org.jetbrains.annotations.NotNull;

public class LinkedInSharePreferenceDTO extends BaseSocialSharePreferenceDTO
{
    public LinkedInSharePreferenceDTO(boolean isShareEnabled)
    {
        super(isShareEnabled);
    }

    @NotNull @Override public SocialNetworkEnum getSocialNetworkEnum()
    {
        return SocialNetworkEnum.LN;
    }
}
