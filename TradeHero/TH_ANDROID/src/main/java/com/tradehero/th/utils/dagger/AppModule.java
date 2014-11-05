package com.tradehero.th.utils.dagger;

import android.accounts.AccountManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import com.tencent.tauth.Tencent;
import com.tradehero.FlavorModule;
import com.tradehero.th.BuildTypeModule;
import com.tradehero.th.activities.ActivityAppModule;
import com.tradehero.th.activities.GuideActivity;
import com.tradehero.th.api.ObjectMapperWrapper;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.base.THApp;
import com.tradehero.th.billing.BillingModule;
import com.tradehero.th.filter.FilterModule;
import com.tradehero.th.loaders.FriendListLoader;
import com.tradehero.th.loaders.TimelineListLoader;
import com.tradehero.th.loaders.security.SecurityListPagedLoader;
import com.tradehero.th.models.ModelsModule;
import com.tradehero.th.models.chart.ChartModule;
import com.tradehero.th.models.portfolio.DisplayablePortfolioFetchAssistant;
import com.tradehero.th.models.push.PushModule;
import com.tradehero.th.models.user.follow.ChoiceFollowUserAssistantWithDialog;
import com.tradehero.th.network.NetworkModule;
import com.tradehero.th.network.share.SocialNetworkAppModule;
import com.tradehero.th.persistence.PersistenceModule;
import com.tradehero.th.persistence.prefs.LanguageCode;
import com.tradehero.th.persistence.user.UserProfileRetrievedMilestone;
import com.tradehero.th.utils.achievement.AchievementModule;
import com.tradehero.th.utils.level.XpModule;
import com.tradehero.th.utils.metrics.MetricsModule;
import dagger.Module;
import dagger.Provides;
import java.util.Locale;
import javax.inject.Singleton;

@Module(
        includes = {
                ActivityAppModule.class,
                FlavorModule.class,
                AchievementModule.class,
                XpModule.class,
                CacheModule.class,
                GraphicModule.class,
                NetworkModule.class,
                MetricsModule.class,
                ModelsModule.class,
                UserModule.class,
                PersistenceModule.class,
                ChartModule.class,
                FilterModule.class,
                BillingModule.class,
                SocialNetworkAppModule.class,
                PushModule.class,
                BuildTypeModule.class
        },
        injects =
                {
                        THApp.class,
                        ChoiceFollowUserAssistantWithDialog.class,
                        SecurityListPagedLoader.class,

                        DisplayablePortfolioFetchAssistant.class,

                        TimelineListLoader.class,

                        UserProfileRetrievedMilestone.class,

                        FriendListLoader.class,
                        MessageHeaderDTO.class,
                        GuideActivity.class,

                        ObjectMapperWrapper.class,
                },
        complete = false,
        library = true // TODO remove this line
)
public class AppModule
{
    private static final String TENCENT_APP_ID = "1101331512";

    private final THApp THApp;

    public AppModule(THApp THApp)
    {
        this.THApp = THApp;
    }

    @Provides Context provideContext()
    {
        return THApp;
    }

    @Provides Locale provideLocale(Context context)
    {
        return context.getResources().getConfiguration().locale;
    }

    @Provides @LanguageCode String provideCurrentLanguageCode(Locale locale)
    {
        return String.format("%s-%s", locale.getLanguage(), locale.getCountry());
    }

    @Provides @Singleton THApp provideApplication()
    {
        return THApp;
    }

    @Provides @Singleton AccountManager provideAccountManager(Context context)
    {
        return AccountManager.get(context);
    }

    @Provides @Singleton
    LocalBroadcastManager providesLocalBroadcastReceiver(Context context)
    {
        return LocalBroadcastManager.getInstance(context);
    }

    @Provides @Singleton ConnectivityManager provideConnectivityManager(Context context) {

        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Provides
    NetworkInfo provideNetworkInfo(ConnectivityManager connectivityManager)
    {
        return connectivityManager.getActiveNetworkInfo();
    }
    
    @Provides @Singleton Tencent provideTencentAuth(Context context)
    {
        return Tencent.createInstance(TENCENT_APP_ID, context);
    }
}
