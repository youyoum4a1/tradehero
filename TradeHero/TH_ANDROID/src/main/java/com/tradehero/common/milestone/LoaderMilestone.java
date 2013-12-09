package com.tradehero.common.milestone;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.tradehero.common.persistence.PersistableResource;

/**
 * Created with IntelliJ IDEA. User: tho Date: 12/5/13 Time: 5:18 PM Copyright (c) TradeHero
 */
abstract public class LoaderMilestone<T> extends BaseMilestone
{
    private LoaderManager.LoaderCallbacks<T> loaderCallback;

    protected PersistableResource<T> persistableResource;

    public LoaderMilestone()
    {
        loaderCallback = new LoaderManager.LoaderCallbacks<T>()
        {
            @Override public Loader<T> onCreateLoader(int id, Bundle args)
            {
                return null;
            }

            @Override public void onLoadFinished(Loader<T> loader, T data)
            {
                conditionalNotifyCompleteListener();
            }

            @Override public void onLoaderReset(Loader<T> loader)
            {
                conditionalNotifyCompleteListener();
            }
        };
    }

    @Override public void launch()
    {
        launchOwn();
    }

    protected void launchOwn()
    {
        if (isComplete())
        {

        }
    }

    @Override public boolean isRunning()
    {
        return false;
    }

    @Override public boolean isComplete()
    {
        return false;
    }

    @Override public boolean isFailed()
    {
        return false;
    }

    @Override public void onDestroy()
    {
        loaderCallback = null;
    }
}
