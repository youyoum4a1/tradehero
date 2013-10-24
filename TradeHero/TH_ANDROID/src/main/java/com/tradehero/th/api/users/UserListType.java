package com.tradehero.th.api.users;

import com.tradehero.common.persistence.DTOKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 5:05 PM To change this template use File | Settings | File Templates. */
abstract public class UserListType implements Comparable<UserListType>, DTOKey
{
    @Override abstract public int hashCode();

    @Override public boolean equals(Object other)
    {
        return (other instanceof UserListType) && equals((UserListType) other);
    }

    abstract public boolean equals(UserListType other);
}
