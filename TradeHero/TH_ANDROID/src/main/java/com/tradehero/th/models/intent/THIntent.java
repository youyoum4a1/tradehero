package com.tradehero.th.models.intent;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import org.jetbrains.annotations.NotNull;

abstract public class THIntent extends Intent
{
    @NotNull protected Resources resources;

    //<editor-fold desc="Constructors">
    public THIntent(@NotNull Resources resources)
    {
        super();
        this.resources = resources;
        setDefaultAction();
        setData(getUri());
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

    public Uri getUri()
    {
        return Uri.parse(getUriPath());
    }

    public String getUriPath()
    {
        return getBaseUriPath(resources);
    }

    public static String getBaseUriPath(@NotNull Resources resources)
    {
        return resources.getString(
                R.string.intent_uri_base,
                resources.getString(R.string.intent_scheme));
    }

    public static String getHostUriPath(
            @NotNull Resources resources,
            int hostResId)
    {
        return resources.getString(
                R.string.intent_uri_host,
                resources.getString(R.string.intent_scheme),
                resources.getString(hostResId));
    }

    public static String getActionUriPath(
            @NotNull Resources resources,
            int hostResId,
            int actionResId)
    {
        return resources.getString(
                R.string.intent_uri_action,
                resources.getString(R.string.intent_scheme),
                resources.getString(hostResId),
                resources.getString(actionResId));
    }

    abstract public RootFragmentType getDashboardType();

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
