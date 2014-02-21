package com.tradehero.th.fragments.news;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.news.NewsHeadline;

/**
 * Created by julien on 11/10/13
 *
 * Map a Yahoo News object to a NewsHeadlineView.
 */
public class NewsHeadlineAdapter extends ArrayDTOAdapter<NewsHeadline, NewsHeadlineView>
{
    private final static String TAG = NewsHeadlineAdapter.class.getSimpleName();

    public NewsHeadlineAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    @Override protected void fineTune(final int position, NewsHeadline dto, final NewsHeadlineView dtoView)
    {
    }
}
