package com.tradehero.th.network.share;

import dagger.Component;
import dagger.Provides;

@Component(modules = { SocialNetworkUIComponent.Module.class })
public interface SocialNetworkUIComponent
{
    @dagger.Module
    static class Module
    {
        @Provides SocialSharer provideSocialSharer(SocialSharerImpl socialSharerImpl)
        {
            return socialSharerImpl;
        }
    }
}
