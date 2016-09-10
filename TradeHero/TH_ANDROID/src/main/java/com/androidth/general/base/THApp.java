package com.androidth.general.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.androidth.general.BuildConfig;
import com.androidth.general.activities.ActivityBuildTypeUtil;
import com.androidth.general.common.utils.THLog;
import com.androidth.general.inject.BaseInjector;
import com.androidth.general.inject.ExInjector;
import com.androidth.general.models.level.UserXPAchievementHandler;
import com.androidth.general.models.push.PushNotificationManager;
import com.androidth.general.utils.Constants;
import com.androidth.general.utils.dagger.AppModule;
import com.appsflyer.AppsFlyerLib;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import com.tune.Tune;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;
import javax.inject.Inject;

import dagger.ObjectGraph;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import timber.log.Timber;

public class THApp extends BaseApplication
        implements ExInjector
{
    private static final int MEMORY_CACHE_SIZE = 2 * 1024 * 1024;
    private static final int DISK_CACHE_SIZE = 50 * 1024 * 1024;
    private final String FLURRY_APIKEY = "K8Y3PD7T5M5BNM2X949X";

    private static final String MAT_APP_ID = "19686";
    private static final String MAT_APP_KEY = "c65b99d5b751944e3637593edd04ce01";

    private static final String TWITTER_KEY = "j79q8diGnadXdcOFZJ6K13UTL";
    private static final String TWITTER_SECRET = "TrhCrSePLTF8yCmfsTvU7B3RoOQLgFf2zz0QXJd7KIeJ6WESZ9";

    public static Context context;

    @Inject protected PushNotificationManager pushNotificationManager;
    @Inject UserXPAchievementHandler userXPAchievementHandler;

    private ObjectGraph objectGraph;

    @Override public void onCreate()
    {
        super.onCreate();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);//new

        setupRealm();

        context = getApplicationContext();

        setupFabricWithTwitter();
        setupTune();

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

        Platform.loadPlatformComponent(new AndroidPlatformComponent());


        // TODO: For Kenanga Challenge, can remove after that.
        AppsFlyerLib.getInstance().startTracking(this,"pEuxjZE2GpyRXXwFjHHRRU");

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

    private void setupRealm(){
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(this)
                .name(Constants.REALM_DB_NAME).deleteRealmIfMigrationNeeded().build());
    }

    private void setupFabricWithTwitter(){
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        CrashlyticsCore crashlytics = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();

        Fabric.with(this, new TwitterCore(authConfig), new Crashlytics(), new Crashlytics.Builder().core(crashlytics).build());

        ActivityBuildTypeUtil.startCrashReports(context);
    }

    private void setupTune(){
        Tune.init(this, MAT_APP_ID, MAT_APP_KEY);
        Tune.getInstance().setPackageName(context.getPackageName() + "." + Constants.TAP_STREAM_TYPE.name());
        Tune.getInstance().setDebugMode(!Constants.RELEASE);
    }
}
