package com.ayondo.academy.api.users;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class UserTransactionHistoryListType extends UserBaseKey
{
    //<editor-fold desc="Constructors">
    public UserTransactionHistoryListType(@NonNull UserBaseKey userBaseKey)
    {
        super(userBaseKey.key);
    }
    //</editor-fold>

    @Override public boolean equals(@Nullable Object other)
    {
        return other instanceof UserTransactionHistoryListType &&
                super.equals(other);
    }
}
