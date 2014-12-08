package com.tradehero.th;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.crashlytics.android.Crashlytics;
import com.tradehero.common.application.PApplication;
import com.tradehero.common.log.CrashReportingTree;
import com.tradehero.common.log.EasyDebugTree;
import com.tradehero.common.thread.KnownExecutorServices;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.models.push.PushNotificationManager;
import com.tradehero.th.utils.dagger.AppModule;
import javax.inject.Inject;
import timber.log.Timber;

public class THApp extends PApplication
{
    @Inject PushNotificationManager pushNotificationManager;

    private AppGraph component;

    @Override protected void init()
    {
        super.init();

        Timber.plant(createTimberTree());
        Timber.plant(createCrashlyticsTree());

        // Supposedly get the count of cores
        KnownExecutorServices.setCpuThreadCount(Runtime.getRuntime().availableProcessors());
        Timber.d("Available Processors Count: %d", KnownExecutorServices.getCpuThreadCount());

        buildObjectGraphAndInject();

        pushNotificationManager.initialise();

        THLog.showDeveloperKeyHash(this);
    }

    private void buildObjectGraphAndInject()
    {
        component = AppComponent.Initializer.init(this);
        component.injectApp(this);
    }

    protected Object[] getModules()
    {
        return new Object[] { new AppModule(this) };
    }

    public static THApp get(Context context)
    {
        return (THApp) context.getApplicationContext();
    }

    public void restartActivity(Class<? extends Activity> activityClass)
    {
        Intent newApp = new Intent(this, activityClass);
        newApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(newApp);

        buildObjectGraphAndInject();
    }

    private Timber.Tree createCrashlyticsTree()
    {
        Crashlytics.start(this);
        return new CrashReportingTree();
    }

    @NonNull protected Timber.Tree createTimberTree()
    {
        return new EasyDebugTree()
        {
            @Override public String createTag()
            {
                return String.format("TradeHero-%s", super.createTag());
            }
        };
    }
}
