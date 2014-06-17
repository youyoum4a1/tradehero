package com.tradehero.th.fragments.social.friend;

import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.fragments.social.*;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

@Singleton public class SocialNetworkFactory
{
    @NotNull private Map<SocialNetworkEnum, SocialLinkHelper> socialLinkHelperCache;
    @NotNull private final CurrentActivityHolder currentActivityHolder;

    //<editor-fold desc="Constructors">
    @Inject public SocialNetworkFactory(@NotNull CurrentActivityHolder currentActivityHolder)
    {
        this.socialLinkHelperCache = new HashMap<>();
        this.currentActivityHolder = currentActivityHolder;
    }
    //</editor-fold>

    public Class<? extends SocialFriendsFragment> findProperTargetFragment(SocialNetworkEnum socialNetworkEnum)
    {
        switch (socialNetworkEnum)
        {
            case FB:
                return FacebookSocialFriendsFragment.class;

            case TW:
                return TwitterSocialFriendsFragment.class;

            case LN:
                return LinkedInSocialFriendsFragment.class;

            case WB:
                return WeiboSocialFriendsFragment.class;
        }
        throw new IllegalArgumentException("Do not support " + socialNetworkEnum);
    }

    @NotNull public SocialLinkHelper buildSocialLinkerHelper(SocialNetworkEnum socialNetwork)
    {
        if (socialLinkHelperCache.get(socialNetwork) != null)
        {
            return socialLinkHelperCache.get(socialNetwork);
        }
        SocialLinkHelper socialLinkHelper = null;
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
                throw new IllegalArgumentException("Do not support " + socialNetwork);
        }
        socialLinkHelperCache.put(socialNetwork, socialLinkHelper);
        return socialLinkHelper;
    }
}
