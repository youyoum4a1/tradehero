package com.tradehero.th.fragments.social;

import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.social.SocialNetworkEnum;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class SocialLinkHelperFactory
{
    @NotNull private final CurrentActivityHolder currentActivityHolder;

    @Inject public SocialLinkHelperFactory(@NotNull CurrentActivityHolder currentActivityHolder)
    {
        this.currentActivityHolder = currentActivityHolder;
    }

    @NotNull public SocialLinkHelper buildSocialLinkerHelper(@NotNull SocialNetworkEnum socialNetwork)
    {
        SocialLinkHelper socialLinkHelper;
        switch (socialNetwork)
        {
            case FB:
                socialLinkHelper = new FacebookSocialLinkHelper(currentActivityHolder.getCurrentActivity());
                break;
            case TW:
                socialLinkHelper = new TwitterSocialLinkHelper(currentActivityHolder.getCurrentActivity());
                break;
            case LN:
                socialLinkHelper = new LinkedInSocialLinkHelper(currentActivityHolder.getCurrentActivity());
                break;
            case WB:
                socialLinkHelper = new WeiboSocialLinkHelper(currentActivityHolder.getCurrentActivity());
                break;
            default:
                throw new IllegalArgumentException("Do not support SocialNetworkEnum." + socialNetwork);
        }
        return socialLinkHelper;
    }
}
