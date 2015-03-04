package com.tradehero.th.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.crashlytics.android.Crashlytics;
import com.tradehero.common.application.PApplication;
import com.tradehero.common.log.CrashReportingTree;
import com.tradehero.common.log.EasyDebugTree;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.BuildConfig;
import com.tradehero.th.inject.BaseInjector;
import com.tradehero.th.inject.ExInjector;
import com.tradehero.th.models.push.PushNotificationManager;
import com.tradehero.th.persistence.prefs.SavedPushDeviceIdentifier;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.dagger.AppModule;
import dagger.ObjectGraph;
import javax.inject.Inject;
import rx.functions.Action1;
import timber.log.Timber;

public class THApp extends PApplication
        implements ExInjector
{
    public static boolean timberPlanted = false;

    @Inject protected PushNotificationManager pushNotificationManager;
    @Inject @SavedPushDeviceIdentifier StringPreference savedPushDeviceIdentifier;
    private ObjectGraph objectGraph;

    @Override protected void init()
    {
        super.init();

        Timber.plant(createTimberTree());
        Timber.plant(createCrashlyticsTree());

        buildObjectGraphAndInject();

        DaggerUtils.setObjectGraph(objectGraph);

        pushNotificationManager.initialise().subscribe(
                new Action1<PushNotificationManager.InitialisationCompleteDTO>()
                {
                    @Override public void call(PushNotificationManager.InitialisationCompleteDTO initialisationCompleteDTO)
                    {
                        savedPushDeviceIdentifier.set(initialisationCompleteDTO.pushId);
                    }
                },
                new Action1<Throwable>()
                {
                    @Override public void call(Throwable throwable)
                    {
                        Timber.e(throwable, "Failed to initialise PushNotificationManager");
                    }
                });

        THLog.showDeveloperKeyHash(this);
    }

    private Timber.Tree createCrashlyticsTree()
    {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (!BuildConfig.IS_INTELLIJ)
        {
            Crashlytics.start(this);
        }
        return new CrashReportingTree();
    }

    private void buildObjectGraphAndInject()
    {
        objectGraph = ObjectGraph.create(getModules());
        objectGraph.injectStatics();
        objectGraph.inject(this);
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

    protected Object[] getModules()
    {
        return new Object[] {new AppModule(this)};
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
