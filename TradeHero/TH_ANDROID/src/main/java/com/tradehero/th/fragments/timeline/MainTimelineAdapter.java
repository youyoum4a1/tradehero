package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.loaders.ListLoader;
import java.util.List;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by xavier on 3/6/14.
 */
abstract public class MainTimelineAdapter extends ArrayAdapter
    implements StickyListHeadersAdapter
{
    public static final String TAG = MainTimelineAdapter.class.getSimpleName();

    protected final LayoutInflater inflater;
    private TimelineProfileClickListener profileClickListener;

    private int timelineItemViewResId;
    private int portfolioItemViewResId;

    public MainTimelineAdapter(Context context, LayoutInflater inflater)
    {
        super(context, 0);
        this.inflater = inflater;
    }

    public void setProfileClickListener(TimelineProfileClickListener profileClickListener)
    {
        this.profileClickListener = profileClickListener;
    }

    public void setTimelineItemViewResId(int timelineItemViewResId)
    {
        this.timelineItemViewResId = timelineItemViewResId;
    }

    public void setPortfolioItemViewResId(int portfolioItemViewResId)
    {
        this.portfolioItemViewResId = portfolioItemViewResId;
    }

    //////////////////////
    // Timeline elements
    //////////////////////
    private int loaderId;
    private LoaderDTOAdapter.ListLoaderCallback<TimelineItem> callback;

    public int getLoaderId()
    {
        return loaderId;
    }

    public LoaderManager.LoaderCallbacks<List<TimelineItem>> getLoaderTimelineCallback()
    {
        return new LoaderManager.LoaderCallbacks<List<TimelineItem>>()
        {
            @Override public Loader<List<TimelineItem>> onCreateLoader(int id, Bundle args)
            {
                //loaderId = id;
                return callback != null ? callback.onCreateLoader(id, args) : null;
            }

            @Override public void onLoadFinished(Loader<List<TimelineItem>> loader, List<TimelineItem> data)
            {
                notifyDataSetChanged();

                if (loader instanceof ListLoader && callback != null)
                {
                    callback.onLoadFinished((ListLoader<TimelineItem>) loader, data);
                }
            }

            @Override public void onLoaderReset(Loader<List<TimelineItem>> loader)
            {
                if (loader instanceof ListLoader && callback != null)
                {
                    callback.onLoaderReset((ListLoader<TimelineItem>)loader);
                }
            }
        };
    }
}
