package com.tradehero.common.milestone;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.tradehero.common.persistence.PersistableResource;

/**
 * Created with IntelliJ IDEA. User: tho Date: 12/5/13 Time: 5:18 PM Copyright (c) TradeHero
 */
public abstract class LoaderMilestone<T> extends ContextMilestone
{
    private LoaderManager.LoaderCallbacks<T> loaderCallback;

    protected PersistableResource<T> persistableResource;

    public LoaderMilestone(Context context)
    {
        super(context);

        loaderCallback = new LoaderManager.LoaderCallbacks<T>()
        {
            @Override public Loader<T> onCreateLoader(int id, Bundle args)
            {
                return getLoader();
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

    protected abstract Loader<T> getLoader();

    @Override public void launch()
    {
        launchOwn();
    }

    protected void launchOwn()
    {
        if (isComplete())
        {
            conditionalNotifyCompleteListener();
        }
        else
        {
            getLoaderManager().initLoader(0, null, loaderCallback);
        }
    }

    private LoaderManager getLoaderManager()
    {
        if (getContext() instanceof FragmentActivity)
        {
            return ((FragmentActivity)getContext()).getSupportLoaderManager();
        }
        throw new IllegalArgumentException("Context must be a FragmentActivity");
    }

    @Override public boolean isRunning()
    {
        return getLoader().isStarted();
    }

    @Override public boolean isComplete()
    {
        return !getLoader().isStarted();
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
