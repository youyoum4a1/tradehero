package com.tradehero.common.persistence;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.DTOAdapter;
import com.tradehero.th.api.DTOView;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/17/14 Time: 3:10 PM Copyright (c) TradeHero
 */
public abstract class DTOListCacheAdapter<
        DTOKeyType extends DTOKey,
        DTOViewType extends DTOView<DTOKeyType>
        >
        extends DTOAdapter<DTOKeyType, DTOViewType>
{

    public DTOListCacheAdapter(Context context, int layoutResourceId)
    {
        super(context, LayoutInflater.from(context), layoutResourceId);
    }

    @Override public int getCount()
    {
        DTOKeyIdList<DTOKeyType> items = getItems();
        return (items != null) ? items.size() : 0;
    }

    @Override public Object getItem(int i)
    {
        DTOKeyIdList<DTOKeyType> keyList = getItems();
        if (keyList != null && keyList.size() > i)
        {
            return keyList.get(i);
        }
        return null;
    }

    public abstract DTOKeyIdList<DTOKeyType> getItems();

    //<editor-fold desc="Prevent subclass from changing underlying data">
    @Override public final void add(Object object)
    {
        throw new IllegalAccessError("You should add item to the underlying cache instead!");
    }

    @Override public final void addAll(Collection collection)
    {
        throw new IllegalAccessError("You should add item to the underlying cache instead!");
    }

    @Override public final void addAll(Object[] items)
    {
        throw new IllegalAccessError("You should add item to the underlying cache instead!");
    }

    @Override public final void insert(Object object, int index)
    {
        throw new IllegalAccessError("You should add item to the underlying cache instead!");
    }

    @Override public final void clear()
    {
        throw new IllegalAccessError("You should clear item to the underlying cache instead!");
    }
    //</editor-fold>
}