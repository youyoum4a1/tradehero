package com.tradehero.th.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.loaders.ListLoader;
import java.util.List;


public abstract class LoaderDTOAdapter<
            DTOType,
            DTOViewType extends DTOView<DTOType>,
            LoaderType extends ListLoader<DTOType>>
        extends DTOAdapter<DTOType, DTOViewType>
{
    private int loaderId;
    private ListLoaderCallback<DTOType> callback;

    public LoaderDTOAdapter(Context context, LayoutInflater inflater, int loaderId, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
        this.loaderId = loaderId;
    }

    @Override public int getCount()
    {
        return getLoader() != null ? getLoader().getCount() : 0;
    }

    @Override public Object getItem(int position)
    {
        return getCount() > position && getLoader() != null ? getLoader().getItems().get(position) : null;
    }

    public LoaderType getLoader()
    {
        if (context instanceof FragmentActivity)
        {
            Loader loader = (Loader) ((FragmentActivity) context).getSupportLoaderManager().getLoader(getLoaderId());
            return (LoaderType) loader;
        }
        throw new IllegalArgumentException("Context has to be FragmentActivity");
    }

    public LoaderManager.LoaderCallbacks<List<DTOType>> getLoaderCallback()
    {
        return new LoaderManager.LoaderCallbacks<List<DTOType>>()
        {
            @Override public Loader<List<DTOType>> onCreateLoader(int id, Bundle args)
            {
                //loaderId = id;
                return callback != null ? callback.onCreateLoader(id, args) : null;
            }

            @Override public void onLoadFinished(Loader<List<DTOType>> loader, List<DTOType> data)
            {
                notifyDataSetChanged();

                if (loader instanceof ListLoader && callback != null)
                {
                    callback.onLoadFinished((ListLoader<DTOType>) loader, data);
                }
            }

            @Override public void onLoaderReset(Loader<List<DTOType>> loader)
            {
                if (loader instanceof ListLoader && callback != null)
                {
                    callback.onLoaderReset((ListLoader<DTOType>)loader);
                }
            }
        };
    }

    public void setDTOLoaderCallback(ListLoaderCallback<DTOType> callback)
    {
        this.callback = callback;
    }

    public int getLoaderId()
    {
        return loaderId;
    }

    public static abstract class ListLoaderCallback<DTOType>
            implements LoaderManager.LoaderCallbacks<List<DTOType>>
    {
        @Override public Loader<List<DTOType>> onCreateLoader(int id, Bundle args)
        {
            return onCreateLoader(args);
        }

        @Override public final void onLoaderReset(Loader<List<DTOType>> loader)
        {
            throw new IllegalAccessError("This method should not be called!");
        }

        @Override public final void onLoadFinished(Loader<List<DTOType>> loader, List<DTOType> data)
        {
            throw new IllegalAccessError("This method should not be called");
        }

        protected abstract void onLoadFinished(ListLoader<DTOType> loader, List<DTOType> data);

        protected abstract ListLoader<DTOType> onCreateLoader(Bundle args);

        protected void onLoaderReset(ListLoader<DTOType> loader)
        {
            //loader.getItems().clear();
        }
    }
}
