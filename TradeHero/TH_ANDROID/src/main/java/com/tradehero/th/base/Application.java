package com.tradehero.th.base;

import android.app.Activity;
import android.content.Intent;
import com.tradehero.common.application.PApplication;
import com.tradehero.common.log.CrashReportingTree;
import com.tradehero.common.log.EasyDebugTree;
import com.tradehero.common.thread.KnownExecutorServices;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.DebugModule;
import com.tradehero.th.filter.FilterModule;
import com.tradehero.th.fragments.competition.CompetitionModule;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
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

        thRouter.mapFragment(THRouter.USER_TIMELINE, PushableTimelineFragment.class);
        thRouter.mapFragment(THRouter.USER_ME, MeTimelineFragment.class);

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
            listModules.add(new DebugModule());
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
