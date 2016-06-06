package com.androidth.general.models.intent;

import android.content.Intent;
import android.net.Uri;
import com.androidth.general.R;
import com.androidth.general.base.THApp;
import java.util.List;

abstract public class THIntentFactory<THIntentType extends THIntent>
{
    abstract public String getHost();

    public static String getString(int resId)
    {
        return THApp.context.getString(resId);
    }

    public static int getInteger(int resId)
    {
        return THApp.context.getResources().getInteger(resId);
    }

    public boolean isHandlableIntent(Intent intent)
    {
        Uri data = intent.getData();
        return data != null && isHandlableScheme(data.getScheme());
    }

    public boolean isHandlableScheme(String scheme)
    {
        return getString(R.string.intent_scheme).equals(scheme);
    }

    public String getAction(List<String> pathSegments)
    {
        return pathSegments.get(getInteger(R.integer.intent_uri_path_index_action));
    }

    abstract public THIntentType create(Intent intent);
}
