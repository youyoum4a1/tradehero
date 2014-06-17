package com.tradehero.th.fragments.social.friend;

import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.fragments.social.*;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

// TODO remove singleton
@Singleton public class SocialNetworkFactory
{
    // TODO don't use a map.
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

    @NotNull public SocialLinkHelper buildSocialLinkerHelper(@NotNull SocialNetworkEnum socialNetwork)
    {
        if (socialLinkHelperCache.get(socialNetwork) != null)
        {
            return socialLinkHelperCache.get(socialNetwork);
        }
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
        socialLinkHelperCache.put(socialNetwork, socialLinkHelper);
        return socialLinkHelper;
    }

    public void forgetSocialLinkerHelper(@NotNull SocialNetworkEnum socialNetworkEnum)
    {
        // TODO see if the value itself needs to be cleared explicitly
        socialLinkHelperCache.remove(socialNetworkEnum);
    }
}
