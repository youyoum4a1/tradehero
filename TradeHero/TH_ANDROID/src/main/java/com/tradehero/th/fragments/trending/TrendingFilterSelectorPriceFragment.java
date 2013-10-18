package com.tradehero.th.fragments.trending;

import android.view.View;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: xavier Date: 10/18/13 Time: 10:34 AM To change this template use File | Settings | File Templates. */
public class TrendingFilterSelectorPriceFragment extends TrendingFilterSelectorFragment
{
    public static final int POSITION_IN_PAGER  = 2;

    @Override int getTitleResId()
    {
        return R.string.trending_filter_price_title;
    }

    @Override int getTitleLeftDrawableResId()
    {
        return R.drawable.ic_trending_price;
    }

    @Override int getDescriptionResId()
    {
        return R.string.trending_filter_price_description;
    }

    @Override public void displayNextButton()
    {
        super.displayNextButton();
        if (mNext != null)
        {
            mNext.setVisibility(View.INVISIBLE);
        }
    }
}
