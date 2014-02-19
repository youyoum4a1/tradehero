package com.tradehero.th.fragments.security;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.yahoo.News;

/**
 * Created by julien on 11/10/13
 *
 * Map a Yahoo News object to a YahooNewsView.
 */
public class YahooNewsAdapter extends ArrayDTOAdapter<News, YahooNewsView>
{
    private final static String TAG = YahooNewsAdapter.class.getSimpleName();

    public YahooNewsAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    @Override protected void fineTune(final int position, News dto, final YahooNewsView dtoView)
    {
    }
}
