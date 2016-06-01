package com.ayondo.academy.api.users;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.api.PagedDTOKey;

abstract public class UserListType implements Comparable<UserListType>, PagedDTOKey
{
    @Override public boolean equals(@Nullable Object other)
    {
        if (other == this)
        {
            return true;
        }
        return other instanceof UserListType
                && equalFields((UserListType) other);
    }

    abstract protected boolean equalFields(@NonNull UserListType other);
}
