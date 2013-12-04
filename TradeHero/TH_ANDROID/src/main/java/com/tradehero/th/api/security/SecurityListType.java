package com.tradehero.th.api.security;

import com.tradehero.common.persistence.DTOKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 5:05 PM To change this template use File | Settings | File Templates. */
abstract public class SecurityListType implements Comparable<SecurityListType>, DTOKey
{
    @Override abstract public int hashCode();

    @Override public boolean equals(Object other)
    {
        return getClass().isInstance(other) && equals(getClass().cast(other));
    }

    abstract public boolean equals(SecurityListType other);
}
