package com.tradehero.th.fragments.discovery;

import android.content.Context;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.fragments.news.NewsHeadlineViewLinear;
import android.support.annotation.NonNull;

public class NewsHeadlineAdapter extends ArrayDTOAdapter<NewsItemDTOKey, NewsHeadlineViewLinear>
{
    public NewsHeadlineAdapter(@NonNull Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
    }

    @Override protected void fineTune(int position, NewsItemDTOKey dto, NewsHeadlineViewLinear dtoView)
    {
        // Nothing
    }
}
