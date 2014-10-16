package com.tradehero.th.utils.dagger;

import com.tradehero.th.loaders.FriendListLoader;
import com.tradehero.th.persistence.social.VisitedFriendListPrefs;
import dagger.Module;

@Module(
        injects = {
                FriendListLoader.class
        },
        staticInjections = {
                VisitedFriendListPrefs.class,
        },
        complete = false
)
public class UserModule
{
}
