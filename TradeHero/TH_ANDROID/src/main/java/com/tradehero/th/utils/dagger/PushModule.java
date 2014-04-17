package com.tradehero.th.utils.dagger;

import android.content.SharedPreferences;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.portfolio.DisplayablePortfolioUtil;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.loaders.FriendListLoader;
import com.tradehero.th.models.push.DeviceTokenHelper;
import com.tradehero.th.models.push.baidu.BaiduPushManager;
import com.tradehero.th.models.push.baidu.PushSender;
import com.tradehero.th.persistence.social.VisitedFriendListPrefs;
import com.tradehero.th.persistence.user.AbstractUserStore;
import com.tradehero.th.persistence.user.UserManager;
import com.tradehero.th.persistence.user.UserStore;
import com.tradehero.th.utils.ForBaiduPush;
import com.tradehero.th.wxapi.WXEntryActivity;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/27/14 Time: 11:44 AM Copyright (c) TradeHero
 */
@Module(
        injects = {
                BaiduPushManager.class,
                PushSender.class

        },
        staticInjections = {
                DeviceTokenHelper.class
        },
        complete = false
)
public class PushModule
{
    private static final String BAIDU_API_KEY = "iI9WWqP3SfGApTW37UuSyIdc";
    private static final String BAIDU_SECRET_KEY = "i5xkWnVUQLYE703cYG85QoSkrPwjl3ip";

    @Provides @Singleton @ForBaiduPush String provideBaiduAppKey()
    {
        return BAIDU_API_KEY;
    }

}
