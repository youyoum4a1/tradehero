package com.tradehero.th.fragments.trending;

import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import java.util.HashMap;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 9/17/13 Time: 11:45 AM To change this template use File | Settings | File Templates. */
public enum TrendingSearchType
{
    @TrendingSearchTypeInfo(
            stringResourceId = R.string.search_stock_spinner_stock,
            dropDownDrawableResourceId = R.drawable.sort_chart,
            drawableResourceId = R.drawable.toggle_stocks
    )
    STOCKS(0),

    @TrendingSearchTypeInfo(
            stringResourceId = R.string.search_stock_spinner_people,
            dropDownDrawableResourceId = R.drawable.sort_community,
            drawableResourceId = R.drawable.toggle_users
    )
    PEOPLE(1);

    public final static String TAG = TrendingSearchType.class.getSimpleName();
    private static final Map<Integer, TrendingSearchType> intToTypeMap = new HashMap<Integer, TrendingSearchType>();

    private final int value;
    private TrendingSearchType(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
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

    public static int getStringResourceId(TrendingSearchType searchType)
    {
        return getStringResourceId(searchType.name());
    }

    public static int getStringResourceId(String searchType)
    {
        try
        {
            return TrendingSearchType.class.getField(searchType).getAnnotation(TrendingSearchTypeInfo.class).stringResourceId();
        }
        catch (NoSuchFieldException e)
        {
            THLog.e(TAG, "Unavailable searchType name " + searchType, e);
            return 0;
        }
    }

    public static int getDrawableResourceId(TrendingSearchType searchType)
    {
        return getDrawableResourceId(searchType.name());
    }

    public static int getDrawableResourceId(String searchType)
    {
        try
        {
            return TrendingSearchType.class.getField(searchType).getAnnotation(TrendingSearchTypeInfo.class).drawableResourceId();
        }
        catch (NoSuchFieldException e)
        {
            THLog.e(TAG, "Unavailable searchType name " + searchType, e);
            return 0;
        }
    }

    public static int getDropDownDrawableResourceId(TrendingSearchType searchType)
    {
        return getDropDownDrawableResourceId(searchType.name());
    }

    public static int getDropDownDrawableResourceId(String searchType)
    {
        try
        {
            return TrendingSearchType.class.getField(searchType).getAnnotation(TrendingSearchTypeInfo.class).dropDownDrawableResourceId();
        }
        catch (NoSuchFieldException e)
        {
            THLog.e(TAG, "Unavailable searchType name " + searchType, e);
            return 0;
        }
    }
}
