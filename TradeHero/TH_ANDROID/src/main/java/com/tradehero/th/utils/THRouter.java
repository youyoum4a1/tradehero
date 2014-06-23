package com.tradehero.th.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.thoj.route.Routable;
import com.thoj.route.Router;
import com.thoj.route.internal.ContextNotProvided;
import com.thoj.route.internal.RouterOptions;
import com.thoj.route.internal.RouterParams;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class THRouter extends Router
{
    public static final String USER_TIMELINE = "user/:userId";
    public static final String USER_ME = "user/me";
    public static final String STORE = "store";
    public static final String PORTFOLIO_POSITION = "user/:userId/portfolio/:portfolioId";
    public static final String POSITION_TRADE_HISTORY = "user/:userId/portfolio/:portfolioId/position/:positionId";
    public static final String SETTING = "settings";
    public static final String STORE_RESET_PORTFOLIO = "store/reset-portfolio";
    public static final String RESET_PORTFOLIO = "reset-portfolio";
    public static final String SECURITY = "security/:securityId_:exchange_:securitySymbol";
    public static final String PROVIDER_LIST = "providers";
    public static final String PROVIDER = "providers/:providerId";
    public static final String PROVIDER_ENROLL = "providers-enroll/:providerId";
    public static final String PROVIDER_ENROLL_WITH_PAGE = "providers-enroll/:providerId/pages/:encodedUrl";
    public static final String REFER_FRIENDS = "refer-friends";
    public static final String NOTIFICATION = "notifications";
    public static final String TRENDING = "trending-securities";
    public static final String MESSAGE = "messages";

    @Inject public THRouter(Context context)
    {
        super(context);
    }

    @Override public void map(String format, Class<? extends Activity> klass)
    {
        super.map(format, klass);
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

    @Override public void open(String url, Bundle extras, Context context)
    {
        if (context == null)
        {
            throw new ContextNotProvided(
                    "You need to supply a context for Router "
                            + this.toString());
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
        for (Class<?> target: targets) {
            if (Fragment.class.isAssignableFrom(target) && target.isAnnotationPresent(Routable.class)) {
                Routable routable = target.getAnnotation(Routable.class);

                String[] routes = routable.value();
                if (routes != null) {
                    for (String route: routes) {
                        @SuppressWarnings("unchecked")
                        Class<? extends Fragment> fragmentTarget = (Class<? extends Fragment>) target;
                        mapFragment(route, fragmentTarget);
                    }
                }
            }
        }
        return this;
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
                for (Map.Entry<String, String> param: params.openParams.entrySet())
                {
                    args.putString(param.getKey(), param.getValue());
                }
            }
            navigator.pushFragment(options.getOpenFragmentClass(), args);
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
