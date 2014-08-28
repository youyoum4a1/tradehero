package com.tradehero.th.fragments.discovery;

import android.content.Context;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.fragments.news.NewsHeadlineViewLinear;
import org.jetbrains.annotations.NotNull;

public class FeaturedNewsHeadlineAdapter extends ArrayDTOAdapterNew<NewsItemDTOKey, NewsHeadlineViewLinear>
{
    public FeaturedNewsHeadlineAdapter(@NotNull Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
    }
}
