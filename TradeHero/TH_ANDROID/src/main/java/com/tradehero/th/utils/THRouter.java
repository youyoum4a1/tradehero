package com.tradehero.th.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import com.tradehero.routable.Router;
import com.tradehero.routable.RouterOptions;
import javax.inject.Inject;

public class THRouter extends Router
{
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
