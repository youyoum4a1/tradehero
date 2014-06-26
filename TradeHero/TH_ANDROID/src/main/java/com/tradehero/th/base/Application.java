package com.tradehero.th.base;

import android.app.Activity;
import android.content.Intent;
import com.tradehero.common.application.PApplication;
import com.tradehero.common.log.CrashReportingTree;
import com.tradehero.common.log.EasyDebugTree;
import com.tradehero.common.thread.KnownExecutorServices;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.filter.FilterModule;
import com.tradehero.th.fragments.billing.StoreItemAdapter;
import com.tradehero.th.fragments.billing.StoreScreenFragment;
import com.tradehero.th.fragments.competition.CompetitionModule;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
import com.tradehero.th.fragments.competition.MainCompetitionFragment;
import com.tradehero.th.fragments.leaderboard.main.LeaderboardCommunityFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.fragments.updatecenter.messages.MessagesCenterFragment;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationsCenterFragment;
import com.tradehero.th.models.intent.IntentDaggerModule;
import com.tradehero.th.models.push.PushNotificationManager;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.EmailSignUtils;
import com.tradehero.th.utils.THRouter;
import com.tradehero.th.utils.dagger.TradeHeroModule;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class Application extends PApplication
{
    @Inject protected PushNotificationManager pushNotificationManager;
    @Inject protected THRouter thRouter;

    @Override protected void init()
    {
        super.init();

        if (Constants.RELEASE)
        {
            Timber.plant(new CrashReportingTree());
        }
        else
        {
            Timber.plant(new EasyDebugTree()
            {
                @Override public String createTag()
                {
                    return String.format("TradeHero-%s", super.createTag());
                }
            });
        }

        // Supposedly get the count of cores
        KnownExecutorServices.setCpuThreadCount(Runtime.getRuntime().availableProcessors());
        Timber.d("Available Processors Count: %d", KnownExecutorServices.getCpuThreadCount());

        DaggerUtils.initialize(getModules());
        DaggerUtils.inject(this);

        THUser.initialize();

        EmailSignUtils.initialize();

        pushNotificationManager.initialise();

        thRouter.registerRoutes(
                PushableTimelineFragment.class,
                MeTimelineFragment.class,

                NotificationsCenterFragment.class,
                MessagesCenterFragment.class,
                UpdateCenterFragment.class,

                TrendingFragment.class,
                FriendsInvitationFragment.class,
                SettingsFragment.class,
                MainCompetitionFragment.class,
                BuySellFragment.class,
                StoreScreenFragment.class,

                LeaderboardCommunityFragment.class,

                CompetitionWebViewFragment.class,

                PositionListFragment.class
        );
        thRouter.registerAlias("messages", "updatecenter/0");
        thRouter.registerAlias("notifications", "updatecenter/1");
        thRouter.registerAlias("reset-portfolio", "store/" + StoreItemAdapter.POSITION_BUY_RESET_PORTFOLIO);
        thRouter.registerAlias("store/reset-portfolio", "store/" + StoreItemAdapter.POSITION_BUY_RESET_PORTFOLIO);

        THLog.showDeveloperKeyHash();
    }

    protected Object[] getModules()
    {
        Object[] modules = new Object[]
                {
                        new TradeHeroModule(this),
                        new IntentDaggerModule(),
                        new CompetitionModule(),
                        new FilterModule()
                };

        if (!Constants.RELEASE)
        {
            List<Object> listModules = new ArrayList<>(Arrays.asList(modules));
            //listModules.add(new com.tradehero.th.DebugModule());
            return listModules.toArray();
        }
        return modules;
    }

    public void restartActivity(Class<? extends Activity> activityClass)
    {
        Intent newApp = new Intent(this, activityClass);
        newApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(newApp);

        DaggerUtils.initialize(getModules());
        DaggerUtils.inject(this);
    }
}
