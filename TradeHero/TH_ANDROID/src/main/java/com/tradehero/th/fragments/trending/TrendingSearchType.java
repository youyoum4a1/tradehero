package com.tradehero.th.fragments.trending;

import com.tradehero.th.R;

public enum TrendingSearchType
{
    STOCKS(0, R.string.search_stock_spinner_stock, R.drawable.toggle_stocks),
    PEOPLE(1, R.string.search_stock_spinner_people, R.drawable.toggle_users);

    public final int value;
    public final int searchStringResId;
    public final int searchDrawableResId;

    private TrendingSearchType(int value, int searchStringResId, int searchDrawableResId)
    {
        this.value = value;
        this.searchStringResId = searchStringResId;
        this.searchDrawableResId = searchDrawableResId;
    }

    public static TrendingSearchType fromInt(int index)
    {
        if (index >= values().length)
        {
            return TrendingSearchType.STOCKS;
        }

        for (TrendingSearchType trendingSearchType: values())
        {
            if (trendingSearchType.ordinal() == index)
            {
                return trendingSearchType;
            }
        }
        return null;
    }
}
