package com.tradehero.th.fragments.social;

import com.tradehero.th.api.social.SocialNetworkEnum;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;

public class SocialLinkHelperFactory
{
    @NotNull private final Provider<FacebookSocialLinkHelper> facebookSocialLinkHelperProvider;
    @NotNull private final Provider<TwitterSocialLinkHelper> twitterSocialLinkHelperProvider;
    @NotNull private final Provider<LinkedInSocialLinkHelper> linkedinSocialLinkHelperProvider;
    @NotNull private final Provider<WeiboSocialLinkHelper> weiboSocialLinkHelperProvider;

    @Inject public SocialLinkHelperFactory(
            @NotNull Provider<FacebookSocialLinkHelper> facebookSocialLinkHelperProvider,
            @NotNull Provider<TwitterSocialLinkHelper> twitterSocialLinkHelperProvider,
            @NotNull Provider<LinkedInSocialLinkHelper> linkedinSocialLinkHelperProvider,
            @NotNull Provider<WeiboSocialLinkHelper> weiboSocialLinkHelperProvider)
    {
        this.facebookSocialLinkHelperProvider = facebookSocialLinkHelperProvider;
        this.twitterSocialLinkHelperProvider = twitterSocialLinkHelperProvider;
        this.linkedinSocialLinkHelperProvider = linkedinSocialLinkHelperProvider;
        this.weiboSocialLinkHelperProvider = weiboSocialLinkHelperProvider;
    }

    @NotNull public SocialLinkHelper buildSocialLinkerHelper(@NotNull SocialNetworkEnum socialNetwork)
    {
        SocialLinkHelper socialLinkHelper;
        switch (socialNetwork)
        {
            case FB:
                socialLinkHelper = facebookSocialLinkHelperProvider.get();
                break;
            case TW:
                socialLinkHelper = twitterSocialLinkHelperProvider.get();
                break;
            case LN:
                socialLinkHelper = linkedinSocialLinkHelperProvider.get();
                break;
            case WB:
                socialLinkHelper = weiboSocialLinkHelperProvider.get();
                break;
            default:
                throw new IllegalArgumentException("Do not support SocialNetworkEnum." + socialNetwork);
        }
        return socialLinkHelper;
    }
}
