package com.tradehero.th.utils.dagger;

import android.content.SharedPreferences;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.portfolio.DisplayablePortfolioUtil;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.loaders.FriendListLoader;
import com.tradehero.th.persistence.social.VisitedFriendListPrefs;
import com.tradehero.th.persistence.user.AbstractUserStore;
import com.tradehero.th.persistence.user.UserManager;
import com.tradehero.th.persistence.user.UserStore;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/27/14 Time: 11:44 AM Copyright (c) TradeHero
 */
@Module(
        injects = {
                DashboardActivity.class,

                FriendListLoader.class,

                DashboardActivity.class,
                UserManager.class,
        },
        staticInjections = {
                DisplayablePortfolioUtil.class,
                VisitedFriendListPrefs.class,
        },
        complete = false
)
public class UserModule
{
    private static final String PREF_CURRENT_USER_ID_KEY = "PREF_CURRENT_USER_ID_KEY";

    @Provides @Singleton CurrentUserId provideCurrentUser(SharedPreferences sharedPreferences)
    {
        return new CurrentUserId(sharedPreferences, PREF_CURRENT_USER_ID_KEY, 0);
    }

    @Provides @Singleton AbstractUserStore provideUserStore(UserStore store)
    {
        return store;
    }
}
