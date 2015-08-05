package com.tradehero.th.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.activities.ActivityBuildTypeUtil;
import com.tradehero.th.inject.BaseInjector;
import com.tradehero.th.inject.ExInjector;
import com.tradehero.th.models.level.UserXPAchievementHandler;
import com.tradehero.th.models.push.PushNotificationManager;
import com.tradehero.th.utils.dagger.AppModule;
import dagger.ObjectGraph;
import javax.inject.Inject;
import rx.functions.Action1;
import timber.log.Timber;

public class THApp extends BaseApplication
        implements ExInjector
{
    private static final int MEMORY_CACHE_SIZE = 2 * 1024 * 1024;
    private static final int DISK_CACHE_SIZE = 50 * 1024 * 1024;

    public static Context context;

    @Inject protected PushNotificationManager pushNotificationManager;
    @Inject UserXPAchievementHandler userXPAchievementHandler;

    private ObjectGraph objectGraph;

    @Override public void onCreate()
    {
        super.onCreate();
        context = getApplicationContext();

        ActivityBuildTypeUtil.startCrashReports(this);
        Timber.plant(TimberUtil.createTree());

        buildObjectGraphAndInject();

        userXPAchievementHandler.register(this);

        pushNotificationManager.initialise()
                .subscribe(
                        new Action1<PushNotificationManager.InitialisationCompleteDTO>()
                        {
                            @Override public void call(PushNotificationManager.InitialisationCompleteDTO initialisationCompleteDTO)
                            {
                                // Nothing to do
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable throwable)
                            {
                                // Likely to happen as long as the server expects credentials on this one
                                Timber.e(throwable, "Failed to initialise PushNotificationManager");
                            }
                        });

        THLog.showDeveloperKeyHash(this);
    }

    private void buildObjectGraphAndInject()
    {
        objectGraph = ObjectGraph.create(getModules());
        objectGraph.injectStatics();
        objectGraph.inject(this);
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

    @Override public void onTerminate()
    {
        userXPAchievementHandler.unregister();
        super.onTerminate();
    }

    public static THApp get(Context context)
    {
        return (THApp) context.getApplicationContext();
    }

    public static THApp context()
    {
        return (THApp) context;
    }
}
