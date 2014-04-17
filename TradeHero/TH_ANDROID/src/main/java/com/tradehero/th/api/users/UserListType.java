package com.tradehero.th.api.users;

import com.tradehero.common.persistence.DTOKey;

abstract public class UserListType implements Comparable<UserListType>, DTOKey
{
    @Override abstract public int hashCode();

    @Override public boolean equals(Object other)
    {
        return equalClass(other) && equalFields((UserListType) other);
    }

    public boolean equalClass(Object other)
    {
        return other != null && other.getClass().equals(getClass());
    }

    abstract public boolean equalFields(UserListType other);
}
