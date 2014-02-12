package com.tradehero.th.fragments.trending;

import com.tradehero.th.R;
import java.util.HashMap;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 9/17/13 Time: 11:45 AM To change this template use File | Settings | File Templates. */
public enum TrendingSearchType
{
    STOCKS(0, R.string.search_stock_spinner_stock, R.drawable.graph, R.drawable.toggle_stocks),

    PEOPLE(1, R.string.search_stock_spinner_people, R.drawable.sort_community, R.drawable.toggle_users);

    public final static String TAG = TrendingSearchType.class.getSimpleName();
    private static final Map<Integer, TrendingSearchType> INT_TO_TYPE_MAP = new HashMap<Integer, TrendingSearchType>();

    public final int value;
    public final int searchStringResId;
    public final int searchDropDownDrawableResId;
    public final int searchDrawableResId;

    private TrendingSearchType(int value, int searchStringResId, int searchDropDownDrawableResId, int searchDrawableResId)
    {
        this.value = value;
        this.searchStringResId = searchStringResId;
        this.searchDropDownDrawableResId = searchDropDownDrawableResId;
        this.searchDrawableResId = searchDrawableResId;
    }

    static
    {
        for (TrendingSearchType type : TrendingSearchType.values())
        {
            INT_TO_TYPE_MAP.put(type.value, type);
        }
    }

    public static TrendingSearchType fromInt(int i)
    {
        TrendingSearchType type = INT_TO_TYPE_MAP.get(i);
        if (type == null)
        {
            return TrendingSearchType.STOCKS;
        }
        return type;
    }
}
