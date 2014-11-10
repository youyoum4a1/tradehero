package com.tradehero.th.api.users;

import com.tradehero.common.api.PagedDTOKey;

abstract public class UserListType implements Comparable<UserListType>, PagedDTOKey
{
    @Override abstract public int hashCode();

    @Override public boolean equals(Object other)
    {
        return equalClass(other) && equalFields((UserListType) other);
    }

    public boolean equalClass(Object other)
    {
        return other != null && other.getClass().equals(((Object) this).getClass());
    }

    abstract public boolean equalFields(UserListType other);
}
