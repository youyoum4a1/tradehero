package com.tradehero.th.activities;

import android.app.Activity;
import android.os.Handler;
import java.lang.ref.WeakReference;

/**
 * Created by xavier on 2/11/14.
 */
public class CurrentActivityHolder
{
    public static final String TAG = CurrentActivityHolder.class.getSimpleName();

    protected WeakReference<Activity> currentActivityWeak = new WeakReference<>(null);
    protected final Handler currentHandler;

    public CurrentActivityHolder(Handler handler)
    {
        this.currentHandler = handler;
    }

    public Activity getCurrentActivity()
    {
        return currentActivityWeak.get();
    }

    public void setCurrentActivity(Activity currentActivity)
    {
        this.currentActivityWeak = new WeakReference<>(currentActivity);
    }

    public void unsetActivity(Activity toUnset)
    {
        if (currentActivityWeak.get() == toUnset)
        {
            setCurrentActivity(null);
        }
    }

    public Handler getCurrentHandler()
    {
        return currentHandler;
    }
}
