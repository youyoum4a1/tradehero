package com.tradehero.th.fragments.trending;

import com.tradehero.th.R;
import com.tradehero.th.api.security.TrendingAllSecurityListType;
import com.tradehero.th.api.security.TrendingSecurityListType;

/** Created with IntelliJ IDEA. User: xavier Date: 10/18/13 Time: 10:34 AM To change this template use File | Settings | File Templates. */
public class TrendingFilterSelectorAllFragment extends TrendingFilterSelectorFragment
{
    public static final int POSITION_IN_PAGER  = 3;

    @Override int getTitleResId()
    {
        return R.string.trending_filter_all_title;
    }

    @Override int getTitleLeftDrawableResId()
    {
        return 0;
    }

    @Override int getDescriptionResId()
    {
        return R.string.trending_filter_all_description;
    }

    @Override public void displayNextButton()
    {
        super.displayNextButton();
        if (mNext != null)
        {
            mNext.setEnabled(false);
        }
    }

    @Override public TrendingSecurityListType getTrendingSecurityListType(String exchangeName, Integer page, Integer perPage)
    {
        return sGetTrendingSecurityListType(exchangeName, page, perPage);
    }

    public static TrendingSecurityListType sGetTrendingSecurityListType(String exchangeName, Integer page, Integer perPage)
    {
        return new TrendingAllSecurityListType(exchangeName, page, perPage);
    }
}
