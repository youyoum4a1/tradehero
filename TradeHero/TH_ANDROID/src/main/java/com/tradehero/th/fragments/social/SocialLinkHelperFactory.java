package com.tradehero.th.fragments.social;

import com.tradehero.th.api.social.SocialNetworkEnum;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;

public class SocialLinkHelperFactory
{
    @NotNull private final Provider<WeiboSocialLinkHelper> weiboSocialLinkHelperProvider;

    @Inject public SocialLinkHelperFactory(
            @NotNull Provider<WeiboSocialLinkHelper> weiboSocialLinkHelperProvider)
    {
        this.weiboSocialLinkHelperProvider = weiboSocialLinkHelperProvider;
    }

    @NotNull public SocialLinkHelper buildSocialLinkerHelper(@NotNull SocialNetworkEnum socialNetwork)
    {
        SocialLinkHelper socialLinkHelper;
        switch (socialNetwork)
        {
            case WB:
                socialLinkHelper = weiboSocialLinkHelperProvider.get();
                break;
            default:
                throw new IllegalArgumentException("Do not support SocialNetworkEnum." + socialNetwork);
        }
        return socialLinkHelper;
    }
}
