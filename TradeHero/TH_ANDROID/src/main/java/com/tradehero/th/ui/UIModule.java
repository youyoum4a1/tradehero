package com.tradehero.th.ui;

import com.tradehero.th.fragments.social.friend.SocialFriendUserView;

import org.ocpsoft.prettytime.PrettyTime;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
        },
        injects = {
                SocialFriendUserView.class
        },
        complete = false,
        library = true
)
public class UIModule
{
    @Provides PrettyTime providePrettyTime()
    {
        return new PrettyTime();
    }
}
