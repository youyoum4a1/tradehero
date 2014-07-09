package com.tradehero.th.utils.dagger;

import android.content.SharedPreferences;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.loaders.FriendListLoader;
import com.tradehero.th.persistence.social.VisitedFriendListPrefs;
import com.tradehero.th.wxapi.WXEntryActivity;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        injects = {
                DashboardActivity.class,

                FriendListLoader.class,

                DashboardActivity.class,
                WXEntryActivity.class
        },
        staticInjections = {
                VisitedFriendListPrefs.class,
        },
        complete = false
)
public class UserModule
{
    private static final String PREF_CURRENT_USER_ID_KEY = "PREF_CURRENT_USER_ID_KEY";

    @Provides @Singleton CurrentUserId provideCurrentUser(@ForUser SharedPreferences sharedPreferences)
    {
        return new CurrentUserId(sharedPreferences, PREF_CURRENT_USER_ID_KEY, 0);
    }
}
