package com.tradehero.th.network.share;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true
)
public class SocialNetworkUIModule
{
    @Provides SocialSharer provideSocialSharer(SocialSharerImpl socialSharerImpl)
    {
        return socialSharerImpl;
    }
}
