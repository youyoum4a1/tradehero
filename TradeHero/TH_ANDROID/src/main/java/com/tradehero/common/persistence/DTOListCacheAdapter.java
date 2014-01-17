package com.tradehero.common.persistence;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.DTOAdapter;
import com.tradehero.th.api.DTOView;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/17/14 Time: 3:10 PM Copyright (c) TradeHero
 */
public abstract class DTOListCacheAdapter<T extends DTOKey, T2 extends DTOKey, DTOCacheType
        extends StraightDTOCache<T2, ? extends DTOKeyIdList<T>>, V extends DTOView<T>>
        extends DTOAdapter<T, V>
{
    private final T2 keyForList;
    private final DTOCacheType dtoCache;

    public DTOListCacheAdapter(Context context, LayoutInflater inflater, int layoutResourceId, DTOCacheType dtoCache, T2 keyForList)
    {
        super(context, inflater, layoutResourceId);
        this.keyForList = keyForList;
        this.dtoCache = dtoCache;
    }

    @Override public int getCount()
    {
        return dtoCache.get(keyForList).size();
    }

    @Override public Object getItem(int i)
    {
        DTOKeyIdList<T> keyList = dtoCache.get(keyForList);
        if (keyList != null && keyList.size() > i)
        {
            return keyList.get(i);
        }
        return null;
    }
}