package com.tradehero.th.fragments.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.news.NewsHeadline;
import com.tradehero.th.api.news.NewsItemDTO;

/**
 * Created by tradehero on 14-3-7.
 */
public class NewsListAdapter extends ArrayDTOAdapter<NewsItemDTO,NewsHeadlineView>  {
//    private final static String TAG = NewsHeadlineAdapter.class.getSimpleName();
//
    public NewsListAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    @Override protected void fineTune(final int position, NewsItemDTO dto, final NewsHeadlineView dtoView)
    {
    }
}

