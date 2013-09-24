package com.tradehero.th.fragments.base;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockFragment;
import com.tradehero.th.adapters.DTOAdapter;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.loaders.AbstractItemWithComparableId;
import com.tradehero.th.loaders.ItemWithComparableId;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 9/11/13 Time: 1:08 PM Copyright (c) TradeHero */
public abstract class ItemListFragment<T extends ItemWithComparableId> extends SherlockFragment
        implements LoaderManager.LoaderCallbacks<List<T>>
{
    protected ListView listView;

    @Override public void onDestroyView()
    {
        super.onDestroyView();

        listView = null;
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);
    }

    @SuppressWarnings("unchecked")
    public DTOAdapter<T, ? extends DTOView<T>> getListAdapter()
    {
        if (listView != null)
        {
            return (DTOAdapter<T, ? extends DTOView<T>>) ((HeaderViewListAdapter) listView.getAdapter()).getWrappedAdapter();
        }

        return null;
    }

    public void setListView(ListView listView)
    {
        this.listView = listView;
    }

    //<editor-fold desc="LoaderManager callback methods">
    @Override public void onLoadFinished(Loader<List<T>> listLoader, List<T> items)
    {
        getListAdapter().notifyDataSetChanged();
    }

    @Override public void onLoaderReset(Loader<List<T>> listLoader)
    {
        // TODO more investigation
        if (getListAdapter() != null)
        {
            getListAdapter().setItems(null);
        }
    }
    //</editor-fold>
}
