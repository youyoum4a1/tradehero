package com.tradehero.th.fragments.trending;

import com.tradehero.th.widget.trending.TrendingBarStatusDTO;

/** Created with IntelliJ IDEA. User: xavier Date: 9/17/13 Time: 2:34 PM To change this template use File | Settings | File Templates. */
public class TrendingFragmentFactory
{
    public static Class<?> getTrendingFragmentClass(TrendingBarStatusDTO actionBarStatus)
    {
        if (actionBarStatus == null || actionBarStatus.searchType == null || actionBarStatus.searchText == null || actionBarStatus.searchText.length() == 0)
        {
           return TrendingFragment.class;
        }
        else if (actionBarStatus.searchType == TrendingSearchType.STOCKS)
        {
            return SearchStockFragment.class;
        }
        else if (actionBarStatus.searchType == TrendingSearchType.PEOPLE)
        {
            return SearchPeopleFragment.class;
        }

        // We should never reach here.
        throw new IllegalArgumentException();
    }
}
