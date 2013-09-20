package com.tradehero.th.loaders;

/** Created with IntelliJ IDEA. User: tho Date: 9/13/13 Time: 3:19 PM Copyright (c) TradeHero */
public abstract class ItemWithComparableId<T extends Comparable<T>> implements Comparable<ItemWithComparableId<T>>
{
    public abstract T getId();

    public abstract void setId(T id);

    @Override public int compareTo(ItemWithComparableId<T> o)
    {
        if (getId() == null)
        {
            throw new IllegalArgumentException("Item id is not set");
        }
        return getId().compareTo(o.getId());
    }
}
