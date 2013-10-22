package com.tradehero.common.persistence;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 8:08 PM To change this template use File | Settings | File Templates. */
public interface DTOKey
{
    // DTOKeys should carefully write these methods to work with Map and LruCache as intended
    int hashCode();
    boolean equals(Object other);
}
