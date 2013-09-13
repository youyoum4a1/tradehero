package com.tradehero.th.loaders;

/** Created with IntelliJ IDEA. User: tho Date: 9/13/13 Time: 3:19 PM Copyright (c) TradeHero */
public abstract class ItemWithComparableId<T extends Comparable<T>> implements Comparable
{
    public abstract T getId();

    public abstract void setId(T id);

    @SuppressWarnings("unchecked")
    @Override public int compareTo(Object o)
    {
        if (getId() == null)
        {
            throw new IllegalArgumentException("Item id is not set");
        }
        return getId().compareTo(((ItemWithComparableId<T>)o).getId());
    }
}
