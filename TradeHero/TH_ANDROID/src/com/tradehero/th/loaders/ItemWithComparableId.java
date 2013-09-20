package com.tradehero.th.loaders;

/** Created with IntelliJ IDEA. User: xavier Date: 9/20/13 Time: 1:01 PM To change this template use File | Settings | File Templates. */
public interface ItemWithComparableId<T extends Comparable<T>> extends Comparable<ItemWithComparableId<T>>
{
    T getId();
    void setId(T id);
    int compareTo(ItemWithComparableId<T> o);
}
