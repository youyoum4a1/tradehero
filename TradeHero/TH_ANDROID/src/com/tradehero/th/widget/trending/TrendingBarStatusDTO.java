package com.tradehero.th.widget.trending;

import com.tradehero.th.fragments.trending.TrendingSearchType;

/** Created with IntelliJ IDEA. User: xavier Date: 9/17/13 Time: 5:47 PM To change this template use File | Settings | File Templates. */
public class TrendingBarStatusDTO
{
    public TrendingSearchType searchType;
    public String searchText;

    public TrendingBarStatusDTO()
    {
        super();
    }

    public TrendingBarStatusDTO(TrendingSearchType trendingSearchType, String searchText)
    {
        super();
        this.searchType = trendingSearchType;
        this.searchText = searchText;
    }
}
