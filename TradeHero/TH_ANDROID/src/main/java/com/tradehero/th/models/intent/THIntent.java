package com.ayondo.academy.models.intent;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import com.ayondo.academy.R;
import com.ayondo.academy.fragments.dashboard.RootFragmentType;

abstract public class THIntent extends Intent
{
    @NonNull protected Resources resources;

    //<editor-fold desc="Constructors">
    public THIntent(@NonNull Resources resources)
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

    @NonNull public static String getDefaultAction()
    {
        return Intent.ACTION_VIEW;
    }

    @NonNull public Uri getUri()
    {
        return Uri.parse(getUriPath());
    }

    @NonNull public String getUriPath()
    {
        return getBaseUriPath(resources);
    }

    @NonNull public static String getBaseUriPath(@NonNull Resources resources)
    {
        return resources.getString(
                R.string.intent_uri_base,
                resources.getString(R.string.intent_scheme));
    }

    @NonNull public static String getHostUriPath(
            @NonNull Resources resources,
            @StringRes int hostResId)
    {
        return resources.getString(
                R.string.intent_uri_host,
                resources.getString(R.string.intent_scheme),
                resources.getString(hostResId));
    }

    @NonNull public static String getActionUriPath(
            @NonNull Resources resources,
            @StringRes int hostResId,
            @StringRes int actionResId)
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

    @NonNull public Bundle getBundle()
    {
        Bundle newBundle = new Bundle();
        populate(newBundle);
        return newBundle;
    }

    public void populate(@NonNull Bundle bundle)
    {
    }
}
