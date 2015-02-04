package com.tradehero.th.utils.route;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.tradehero.route.Routable;
import com.tradehero.route.Router;
import com.tradehero.route.RouterOptions;
import com.tradehero.route.RouterParams;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.LinkedHashMap;
import java.util.Map;

@Singleton
public class THRouter extends Router
{
    @NotNull private final CurrentActivityHolder currentActivityHolder;
    private Map<String, String> aliases;

    //<editor-fold desc="Constructors">
    @Inject public THRouter(
            @NotNull Context context,
            @NotNull CurrentActivityHolder currentActivityHolder)
    {
        super(context);
        this.currentActivityHolder = currentActivityHolder;
        aliases = new LinkedHashMap<>();
    }
    //</editor-fold>

    @Override public void map(String format, Class<? extends Activity> klass)
    {
        super.map(format, klass);
    }

    @Override public void open(String url, Bundle extras, Context context)
    {
        if (context == null)
        {
            throw new RuntimeException("You need to supply a context for Router " + this.toString());
        }
        if (aliases.containsKey(url))
        {
            url = aliases.get(url);
        }

        RouterParams params = this.paramsForUrl(url);
        Activity currentActivity = currentActivityHolder.getCurrentActivity();
        if (currentActivity != null && params.routerOptions instanceof THRouterOptions)
        {
            openFragment(params, extras, currentActivity);
        }
        else
        {
            super.open(url, extras, context);
        }
    }

    @Override public Router registerRoutes(Class<?>... targets)
    {
        super.registerRoutes(targets);
        for (Class<?> target : targets)
        {
            if (Fragment.class.isAssignableFrom(target))
            {
                //noinspection unchecked
                Class<? extends Fragment> fragmentTarget = (Class<? extends Fragment>) target;
                Class<? extends Runnable>[] runnableClasses = null;

                if (target.isAnnotationPresent(PreRoutable.class))
                {
                    PreRoutable preRoutable = target.getAnnotation(PreRoutable.class);
                    runnableClasses = preRoutable.preOpenRunnables();
                }

                if (target.isAnnotationPresent(Routable.class))
                {
                    Routable routable = target.getAnnotation(Routable.class);

                    String[] routes = routable.value();
                    if (routes != null)
                    {
                        for (String route : routes)
                        {
                            mapFragment(
                                    route,
                                    new THRouterOptions(runnableClasses, fragmentTarget));
                        }
                    }
                }
            }
        }
        return this;
    }

    public void registerAlias(String alias, String url)
    {
        this.aliases.put(alias, url);
    }

    public void mapFragment(String format, @NotNull THRouterOptions options)
    {
        this.routes.put(format, options);
    }

    private void openFragment(RouterParams params, Bundle extras, Activity activity)
    {
        if (activity instanceof DashboardActivity && params != null)
        {
            DashboardNavigator navigator = ((DashboardActivity) activity).getDashboardNavigator();
            THRouterOptions options = (THRouterOptions) params.routerOptions;

            executePreOpenFragment(options.getPreOpenRunnableClasses());

            Bundle args = new Bundle();
            if (extras != null)
            {
                args.putAll(extras);
            }
            if (params.openParams != null)
            {
                for (Map.Entry<String, String> param : params.openParams.entrySet())
                {
                    args.putString(param.getKey(), param.getValue());
                }
            }

            Fragment currentFragment = navigator.getCurrentFragment();

            /** If the opening fragment is active, and not yet detached, resume it with routing bundle **/
            if (currentFragment != null && !currentFragment.isDetached() &&
                    ((Object) currentFragment).getClass().equals(options.getOpenFragmentClass()))
            {
                currentFragment.getArguments().putAll(args);
                currentFragment.onResume();
            }
            else
            {
                navigator.pushFragment(options.getOpenFragmentClass(), args);
            }
        }
    }

    protected void executePreOpenFragment(@Nullable Class<? extends Runnable>[] preOpenRunnables)
    {
        if (preOpenRunnables != null)
        {
            for (Class<? extends Runnable> runnableClass : preOpenRunnables)
            {
                try
                {
                    runnableClass.newInstance().run();
                }
                catch (InstantiationException|IllegalAccessException e)
                {
                    Timber.e(e, "Failed to instantiate %s", runnableClass.getCanonicalName());
                }
            }
        }
    }

    public void inject(Fragment fragment)
    {
        if (fragment.getArguments() != null)
        {
            inject(fragment, fragment.getArguments());
        }
    }

    public static class THRouterOptions extends RouterOptions
    {
        private Class<? extends Runnable>[] preOpenRunnableClasses;
        private Class<? extends Fragment> fragmentClass;

        //<editor-fold desc="Constructors">

        public THRouterOptions(Class<? extends Runnable>[] preOpenRunnableClasses, Class<? extends Fragment> fragmentClass)
        {
            this.preOpenRunnableClasses = preOpenRunnableClasses;
            this.fragmentClass = fragmentClass;
        }
        //</editor-fold>

        public Class<? extends Fragment> getOpenFragmentClass()
        {
            return fragmentClass;
        }

        public Class<? extends Runnable>[] getPreOpenRunnableClasses()
        {
            return preOpenRunnableClasses;
        }

    }
}
