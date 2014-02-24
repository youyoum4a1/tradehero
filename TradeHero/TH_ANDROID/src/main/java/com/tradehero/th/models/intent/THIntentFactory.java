package com.tradehero.th.models.intent;

import android.content.Intent;
import android.net.Uri;
import com.tradehero.th.R;
import com.tradehero.th.base.Application;
import java.util.List;

/**
 * Created by xavier on 1/10/14.
 */
abstract public class THIntentFactory<THIntentType extends THIntent>
{
    public THIntentFactory()
    {
    }

    abstract public String getHost();

    public static String getString(int resId)
    {
        return Application.getResourceString(resId);
    }

    public static int getInteger(int resId)
    {
        return Application.getResourceInteger(resId);
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
