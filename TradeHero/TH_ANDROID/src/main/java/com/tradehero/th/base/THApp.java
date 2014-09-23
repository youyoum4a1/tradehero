package com.tradehero.th.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.tradehero.common.application.PApplication;
import com.tradehero.common.log.CrashReportingTree;
import com.tradehero.common.log.EasyDebugTree;
import com.tradehero.common.thread.KnownExecutorServices;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.inject.BaseInjector;
import com.tradehero.th.inject.ExInjector;
import com.tradehero.th.inject.Injector;
import com.tradehero.th.models.push.PushNotificationManager;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.EmailSignUtils;
import com.tradehero.th.utils.dagger.AppModule;
import dagger.ObjectGraph;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class THApp extends PApplication
    implements ExInjector
{
    public static boolean timberPlanted = false;

    @Inject protected PushNotificationManager pushNotificationManager;
    private ObjectGraph objectGraph;

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

        buildObjectGraphAndInject();

        DaggerUtils.setObjectGraph(objectGraph);

        THUser.initialize();

        EmailSignUtils.initialize();

        pushNotificationManager.initialise();

        THLog.showDeveloperKeyHash();
    }

    private void buildObjectGraphAndInject()
    {
        objectGraph = ObjectGraph.create(getModules());
        objectGraph.injectStatics();
        objectGraph.inject(this);
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
        return new Object[] { new AppModule(this) };
    }

    public void restartActivity(Class<? extends Activity> activityClass)
    {
        Intent newApp = new Intent(this, activityClass);
        newApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(newApp);

        buildObjectGraphAndInject();
    }

    @Override public ExInjector plus(Object... modules)
    {
        return new BaseInjector(objectGraph.plus(modules));
    }

    @Override public void inject(Object o)
    {
        objectGraph.inject(o);
    }

    public static THApp get(Context context)
    {
        return (THApp) context.getApplicationContext();
    }
}
