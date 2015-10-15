package com.tradehero.th.utils.dagger;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.activities.TradeHeroMainActivity;
import com.tradehero.th.activities.RecommendStocksActivity;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.loaders.FriendListLoader;
import com.tradehero.th.persistence.social.VisitedFriendListPrefs;
import com.tradehero.th.wxapi.WXEntryActivity;

import org.ocpsoft.prettytime.PrettyTime;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(
        injects = {
                DashboardActivity.class,
                TradeHeroMainActivity.class,
                FriendListLoader.class,
                RecommendStocksActivity.class,
                WXEntryActivity.class
        },
        staticInjections = {
                VisitedFriendListPrefs.class,
        },
        complete = false,
        library = true
)
public class UserModule
{
    private static final String PREF_CURRENT_USER_ID_KEY = "PREF_CURRENT_USER_ID_KEY";

    @Provides @Singleton CurrentUserId provideCurrentUser(@ForUser SharedPreferences sharedPreferences)
    {
        return new CurrentUserId(sharedPreferences, PREF_CURRENT_USER_ID_KEY, 0);
    }

    @Provides @ForUIThread Handler provideUIHandler()
    {
        return new Handler(Looper.getMainLooper());
    }

    @Provides @Singleton CurrentActivityHolder provideCurrentActivityHandler(@ForUIThread Handler handler)
    {
        return new CurrentActivityHolder(handler);
    }

    @Provides
    PrettyTime providePrettyTime()
    {
        return new PrettyTime();
    }
}
