package com.tradehero.th.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.tradehero.route.Routable;
import com.tradehero.route.Router;
import com.tradehero.route.RouterOptions;
import com.tradehero.route.RouterParams;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class THRouter extends Router
{
    private Map<String, String> aliases;

    @Inject public THRouter(Context context)
    {
        super(context);

        aliases = new LinkedHashMap<>();
    }

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
        if (params.routerOptions instanceof THRouterOptions)
        {
            openFragment(params, extras, context);
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
            if (Fragment.class.isAssignableFrom(target) && target.isAnnotationPresent(Routable.class))
            {
                Routable routable = target.getAnnotation(Routable.class);

                String[] routes = routable.value();
                if (routes != null)
                {
                    for (String route : routes)
                    {
                        @SuppressWarnings("unchecked")
                        Class<? extends Fragment> fragmentTarget = (Class<? extends Fragment>) target;
                        mapFragment(route, fragmentTarget);
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

    public void mapFragment(String format, Class<? extends Fragment> klass)
    {
        mapFragment(format, klass, null);
    }

    public void mapFragment(String format, Class<? extends Fragment> klass, THRouterOptions options)
    {
        if (options == null)
        {
            options = new THRouterOptions();
        }
        options.setOpenFragmentClass(klass);
        this.routes.put(format, options);
    }

    private void openFragment(RouterParams params, Bundle extras, Context context)
    {
        if (context instanceof DashboardActivity && params != null)
        {
            DashboardNavigator navigator = ((DashboardActivity) context).getDashboardNavigator();
            THRouterOptions options = (THRouterOptions) params.routerOptions;
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

    public void inject(Fragment fragment)
    {
        if (fragment.getArguments() != null)
        {
            inject(fragment, fragment.getArguments());
        }
    }

    public static class THRouterOptions extends RouterOptions
    {
        private Class<? extends Fragment> fKlass;

        public void setOpenFragmentClass(Class<? extends Fragment> klass)
        {
            this.fKlass = klass;
        }

        public Class<? extends Fragment> getOpenFragmentClass()
        {
            return fKlass;
        }
    }
}
