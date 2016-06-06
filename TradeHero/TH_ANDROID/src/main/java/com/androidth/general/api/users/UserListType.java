package com.androidth.general.api.users;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.api.PagedDTOKey;

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
