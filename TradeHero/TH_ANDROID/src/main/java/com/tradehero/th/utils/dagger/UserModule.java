package com.tradehero.th.utils.dagger;

import com.tradehero.th.persistence.social.VisitedFriendListPrefs;
import dagger.Module;

@Module(
        injects = {
        },
        staticInjections = {
                VisitedFriendListPrefs.class,
        },
        complete = false
)
public class UserModule
{
}
