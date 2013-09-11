package com.tradehero.th.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.actionbarsherlock.app.SherlockFragment;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/11/13 Time: 1:08 PM Copyright (c) TradeHero */
public class ItemListFragment<T> extends SherlockFragment implements LoaderManager.LoaderCallbacks<List<T>>
{
    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override public Loader<List<T>> onCreateLoader(int i, Bundle bundle)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public void onLoadFinished(Loader<List<T>> listLoader, List<T> ts)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public void onLoaderReset(Loader<List<T>> listLoader)
    {
        // intentionally left blank.
    }
}
