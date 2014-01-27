package com.tradehero.th.utils.dagger;

import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.competition.ProviderConstants;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.DisplayablePortfolioUtil;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.loaders.FriendListLoader;
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
                DisplayablePortfolioDTO.class,

                DashboardActivity.class,
                UserManager.class,
        },
        staticInjections = {
                ProviderConstants.class,
                DisplayablePortfolioUtil.class,
        },
        complete = false
)
public class UserModule
{
    @Provides @Singleton CurrentUserBaseKeyHolder provideCurrentUserBaseKeyHolder()
    {
        return new CurrentUserBaseKeyHolder();
    }

    @Provides @Singleton AbstractUserStore provideUserStore(UserStore store)
    {
        return store;
    }
}
