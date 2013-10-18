package com.tradehero.th.fragments.trending;

import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: xavier Date: 10/18/13 Time: 10:34 AM To change this template use File | Settings | File Templates. */
public class TrendingFilterSelectorVolumeFragment extends TrendingFilterSelectorFragment
{
    public static final int POSITION_IN_PAGER  = 1;

    @Override int getTitleResId()
    {
        return R.string.trending_filter_volume_title;
    }

    @Override int getTitleLeftDrawableResId()
    {
        return R.drawable.ic_trending_volume;
    }

    @Override int getDescriptionResId()
    {
        return R.string.trending_filter_volume_description;
    }

}
