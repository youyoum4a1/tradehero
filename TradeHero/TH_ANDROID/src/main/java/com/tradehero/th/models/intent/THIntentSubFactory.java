package com.tradehero.th.models.intent;

import android.content.Intent;
import android.content.res.Resources;
import java.util.List;
import org.jetbrains.annotations.NotNull;

abstract public class THIntentSubFactory<THIntentType extends THIntent> extends THIntentFactory<THIntentType>
{
    @NotNull protected final Resources resources;

    //<editor-fold desc="Constructors">
    public THIntentSubFactory(@NotNull Resources resources)
    {
        this.resources = resources;
    }
    //</editor-fold>

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
