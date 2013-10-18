package com.tradehero.th.api.security;

import com.tradehero.common.persistence.DTOKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 5:05 PM To change this template use File | Settings | File Templates. */
abstract public class SecurityListType implements Comparable<SecurityListType>, DTOKey<String>
{
    @Override public boolean equals(Object o)
    {
        if (!(o instanceof SecurityListType))
        {
            return false;
        }
        return equals((SecurityListType) o);
    }

    abstract public boolean equals(SecurityListType other);
}
