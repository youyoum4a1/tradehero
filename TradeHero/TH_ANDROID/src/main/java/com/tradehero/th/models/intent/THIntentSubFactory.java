package com.tradehero.th.models.intent;

import android.content.Intent;
import java.util.List;

/**
 * Created by xavier on 1/10/14.
 */
abstract public class THIntentSubFactory<THIntentType extends THIntent> extends THIntentFactory<THIntentType>
{
    public static final String TAG = THIntentSubFactory.class.getSimpleName();

    public THIntentSubFactory()
    {
    }

    @Override public boolean isHandlableIntent(Intent intent)
    {
        return super.isHandlableIntent(intent) &&
            isHandlableHost(intent.getData().getHost());
    }

    public boolean isHandlableHost(String host)
    {
        return getHost().equals(host);
    }

    @Override public THIntentType create(Intent intent)
    {
        if (!isHandlableIntent(intent))
        {
            throw new IllegalArgumentException("Unhandled Intent " + intent.getDataString());
        }
        return create(intent, intent.getData().getPathSegments());
    }

    abstract protected THIntentType create(Intent intent, List<String> pathSegments);
}
