package com.tradehero.th.models.intent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.base.Application;
import com.tradehero.th.fragments.dashboard.DashboardTabType;

/**
 * Created by xavier on 1/10/14.
 */
abstract public class THIntent extends Intent
{
    public static final String TAG = THIntent.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public THIntent()
    {
        super();
        setDefaultAction();
    }

    public THIntent(Uri uri)
    {
        super(getDefaultAction(), uri);
    }
    //</editor-fold>

    public void setDefaultAction()
    {
        setAction(getDefaultAction());
    }

    public static String getDefaultAction()
    {
        return Intent.ACTION_VIEW;
    }

    public static String getString(int resId)
    {
        return Application.getResourceString(resId);
    }

    public static String getString(int resId, java.lang.Object... formatArgs)
    {
        return Application.getResourceString(resId, formatArgs);
    }

    public static int getInteger(int resId)
    {
        return Application.getResourceInteger(resId);
    }

    public Uri getUri()
    {
        return Uri.parse(getUriPath());
    }

    public String getUriPath()
    {
        return getBaseUriPath();
    }

    public static String getBaseUriPath()
    {
        return getString(
                R.string.intent_uri_base,
                getString(R.string.intent_scheme));
    }

    public static String getHostUriPath(int hostResId)
    {
        return getString(
                R.string.intent_uri_host,
                getString(R.string.intent_scheme),
                getString(hostResId));
    }

    public static String getActionUriPath(int hostResId, int actionResId)
    {
        return getString(
                R.string.intent_uri_action,
                getString(R.string.intent_scheme),
                getString(hostResId),
                getString(actionResId));
    }

    abstract public DashboardTabType getDashboardType();

    public Class<? extends Fragment> getActionFragment()
    {
        return null;
    }

    public Bundle getBundle()
    {
        Bundle newBundle = new Bundle();
        populate(newBundle);
        return newBundle;
    }

    public void populate(Bundle bundle)
    {
    }
}
