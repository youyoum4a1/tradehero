package com.tradehero.th.fragments.trending;

import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import java.util.HashMap;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 9/17/13 Time: 11:45 AM To change this template use File | Settings | File Templates. */
public enum TrendingSearchType
{
    STOCKS(0, R.string.search_stock_spinner_stock, R.drawable.sort_chart, R.drawable.toggle_stocks),

    PEOPLE(1, R.string.search_stock_spinner_people, R.drawable.sort_community, R.drawable.toggle_users);

    public final static String TAG = TrendingSearchType.class.getSimpleName();
    private static final Map<Integer, TrendingSearchType> intToTypeMap = new HashMap<Integer, TrendingSearchType>();

    private final int value;
    private final int searchStringResId;
    private final int searchDropDownDrawableResId;
    private final int searchDrawableResId;
    private TrendingSearchType(int value, int searchStringResId, int searchDropDownDrawableResId, int searchDrawableResId)
    {
        this.value = value;
        this.searchStringResId = searchStringResId;
        this.searchDropDownDrawableResId = searchDropDownDrawableResId;
        this.searchDrawableResId = searchDrawableResId;
    }

    public int getValue()
    {
        return value;
    }

    public int getSearchStringResId()
    {
        return searchStringResId;
    }

    public int getSearchDropDownDrawableResId()
    {
        return searchDropDownDrawableResId;
    }

    public int getSearchDrawableResId()
    {
        return searchDrawableResId;
    }

    static
    {
        for (TrendingSearchType type : TrendingSearchType.values())
        {
            intToTypeMap.put(type.value, type);
        }
    }

    public static TrendingSearchType fromInt(int i)
    {
        TrendingSearchType type = intToTypeMap.get(i);
        if (type == null)
        {
            return TrendingSearchType.STOCKS;
        }
        return type;
    }
}
