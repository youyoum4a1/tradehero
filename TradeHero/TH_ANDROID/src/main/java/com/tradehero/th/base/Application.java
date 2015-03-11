package com.tradehero.th.base;

import com.tradehero.common.application.PApplication;
import com.tradehero.common.thread.KnownExecutorServices;
import com.tradehero.common.timber.CrashReportingTree;
import com.tradehero.common.timber.EasyDebugTree;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.filter.FilterModule;
import com.tradehero.th.fragments.competition.CompetitionModule;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
import com.tradehero.th.fragments.competition.MainCompetitionFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.fragments.updatecenter.messages.MessagesCenterFragment;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationsCenterFragment;
import com.tradehero.th.models.intent.IntentDaggerModule;
import com.tradehero.th.models.push.PushNotificationManager;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.DeviceSignUtils;
import com.tradehero.th.utils.EmailSignUtils;
import com.tradehero.th.utils.dagger.AppModule;
import com.tradehero.th.utils.route.THRouter;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Application extends PApplication
{
    public static boolean timberPlanted = false;

    @Inject protected PushNotificationManager pushNotificationManager;
    @Inject protected THRouter thRouter;

    @Override protected void init()
    {
        super.init();

        if (!timberPlanted)
        {
            Timber.plant(createTimberTree());
            timberPlanted = true;
        }

        // Supposedly get the count of cores
        KnownExecutorServices.setCpuThreadCount(Runtime.getRuntime().availableProcessors());
        Timber.d("Available Processors Count: %d", KnownExecutorServices.getCpuThreadCount());

        DaggerUtils.initialize(getModules());
        DaggerUtils.inject(this);

        THUser.initialize();

        EmailSignUtils.initialize();
        DeviceSignUtils.initialize();

        pushNotificationManager.initialise();

        thRouter.registerRoutes(
                NotificationsCenterFragment.class,
                MessagesCenterFragment.class,
                UpdateCenterFragment.class,
                TrendingFragment.class,
                FriendsInvitationFragment.class,
                MainCompetitionFragment.class,
                BuySellFragment.class,
                CompetitionWebViewFragment.class,
                PositionListFragment.class,
                TradeListFragment.class
        );
        thRouter.registerAlias("messages", "updatecenter/0");
        thRouter.registerAlias("notifications", "updatecenter/1");
        thRouter.registerAlias("reset-portfolio", "store/" + ProductIdentifierDomain.DOMAIN_RESET_PORTFOLIO.ordinal());
        thRouter.registerAlias("store/reset-portfolio", "store/" + ProductIdentifierDomain.DOMAIN_RESET_PORTFOLIO.ordinal());

        THLog.showDeveloperKeyHash();
    }

    @NotNull protected Timber.Tree createTimberTree()
    {
        if (Constants.RELEASE)
        {
            return new CrashReportingTree();
        }
        return new EasyDebugTree()
        {
            @Override public String createTag()
            {
                return String.format("TradeHero-%s", super.createTag());
            }
        };
    }

    protected Object[] getModules()
    {
        Object[] modules = new Object[]
                {
                        new AppModule(this),
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
}
