package com.tradehero.th.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.activities.ActivityBuildTypeUtil;
import com.tradehero.th.inject.BaseInjector;
import com.tradehero.th.inject.ExInjector;
import com.tradehero.th.models.level.UserXPAchievementHandler;
import com.tradehero.th.models.push.PushNotificationManager;
import com.tradehero.th.utils.ImageUtils;
import com.tradehero.th.utils.dagger.AppModule;
import dagger.ObjectGraph;
import java.io.File;
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

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(defaultOptions)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(MEMORY_CACHE_SIZE))
                .memoryCacheSize(MEMORY_CACHE_SIZE)
                .diskCacheSize(DISK_CACHE_SIZE)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCacheFileCount(300)
                .diskCache(new UnlimitedDiscCache(new File(ImageUtils.getImageStoragePath(this))))
                .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000))
                .build();

        ImageLoader.getInstance().init(config);

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
