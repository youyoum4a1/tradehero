package com.tradehero.th.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import com.tradehero.routable.Router;
import com.tradehero.routable.RouterOptions;
import javax.inject.Inject;

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
