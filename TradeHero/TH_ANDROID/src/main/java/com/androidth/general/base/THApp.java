package com.androidth.general.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
<<<<<<< HEAD

=======
import com.androidth.general.activities.IdentityPromptActivity;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.utils.Router;
import com.appsflyer.AppsFlyerLib;
import com.flurry.android.FlurryAgent;
import com.androidth.general.common.utils.THLog;
>>>>>>> def8dcbde2f013d1092e91f73e7ed1767b2fb636
import com.androidth.general.activities.ActivityBuildTypeUtil;
import com.androidth.general.common.utils.THLog;
import com.androidth.general.inject.BaseInjector;
import com.androidth.general.inject.ExInjector;
import com.androidth.general.models.level.UserXPAchievementHandler;
import com.androidth.general.models.push.PushNotificationManager;
import com.androidth.general.utils.dagger.AppModule;
<<<<<<< HEAD
import com.flurry.android.FlurryAgent;

import javax.inject.Inject;

import dagger.ObjectGraph;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import rx.functions.Action1;
=======
import com.tradehero.route.RouteProperty;
import dagger.ObjectGraph;
import javax.inject.Inject;
>>>>>>> def8dcbde2f013d1092e91f73e7ed1767b2fb636
import timber.log.Timber;

public class THApp extends BaseApplication
        implements ExInjector
{
    private static final int MEMORY_CACHE_SIZE = 2 * 1024 * 1024;
    private static final int DISK_CACHE_SIZE = 50 * 1024 * 1024;
    private final String FLURRY_APIKEY = "K8Y3PD7T5M5BNM2X949X";

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
                        initialisationCompleteDTO -> {
                            // Nothing to do
                        },
                        throwable -> {
                            // Likely to happen as long as the server expects credentials on this one
                            Timber.e(throwable, "Failed to initialise PushNotificationManager");
                        });

        THLog.showDeveloperKeyHash(this);

        FlurryAgent.setLogEnabled(false);
        FlurryAgent.init(this, FLURRY_APIKEY);
<<<<<<< HEAD
        Platform.loadPlatformComponent(new AndroidPlatformComponent());
=======

        // TODO: For Kenanga Challenge, can remove after that.
        AppsFlyerLib.getInstance().startTracking(this,"pEuxjZE2GpyRXXwFjHHRRU");
>>>>>>> def8dcbde2f013d1092e91f73e7ed1767b2fb636
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
