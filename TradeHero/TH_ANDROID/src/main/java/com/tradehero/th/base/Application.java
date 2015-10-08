package com.tradehero.th.base;

import com.flurry.android.FlurryAgent;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.common.application.PApplication;
import com.tradehero.common.thread.KnownExecutorServices;
import com.tradehero.common.timber.CrashReportingTree;
import com.tradehero.common.timber.EasyDebugTree;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.DeviceSignUtils;
import com.tradehero.th.utils.EmailSignUtils;
import com.tradehero.th.utils.dagger.AppModule;
import com.tradehero.th.utils.route.THRouter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class Application extends PApplication
{
    public static boolean timberPlanted = false;

    @Inject protected THRouter thRouter;

    @Override protected void init()
    {
        super.init();
        if (!timberPlanted)
        {
            Timber.plant(createTimberTree());
            timberPlanted = true;
        }

        FlurryAgent.init(this, "4D4P6Y3W2JGRP3DGWFYY");

        //init universal image loader
        UniversalImageLoader.initImageLoader(this);

        // Supposedly get the count of cores
        KnownExecutorServices.setCpuThreadCount(Runtime.getRuntime().availableProcessors());
        Timber.d("Available Processors Count: %d", KnownExecutorServices.getCpuThreadCount());

        DaggerUtils.initialize(getModules());
        DaggerUtils.inject(this);

        THUser.initialize();

        EmailSignUtils.initialize();
        DeviceSignUtils.initialize();

        thRouter.registerRoutes();
        thRouter.registerAlias("messages", "updatecenter/0");
        thRouter.registerAlias("notifications", "updatecenter/1");

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
