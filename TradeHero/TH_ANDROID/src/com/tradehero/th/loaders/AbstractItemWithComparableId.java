package com.tradehero.th.loaders;

/** Created with IntelliJ IDEA. User: tho Date: 9/13/13 Time: 3:19 PM Copyright (c) TradeHero */
public abstract class AbstractItemWithComparableId<T extends Comparable<T>> implements ItemWithComparableId<T>
{
    public abstract T getId();

    public abstract void setId(T id);

    @Override public int compareTo(ItemWithComparableId<T> other)
    {
        if (getId() == null)
        {
            throw new IllegalArgumentException("Item id is not set");
        }
        return getId().compareTo(other.getId());
    }
}
