package com.tradehero.th.api.users;

import android.os.Bundle;

public class UserTransactionHistoryListType extends UserBaseKey
{
    //<editor-fold desc="Constructors">
    public UserTransactionHistoryListType(Integer key)
    {
        super(key);
    }

    public UserTransactionHistoryListType(Bundle args)
    {
        super(args);
    }

    public UserTransactionHistoryListType(UserBaseKey userBaseKey)
    {
        super(userBaseKey.key);
    }
    //</editor-fold>

    @Override public boolean equals(Object other)
    {
        return other != null &&
                other instanceof UserTransactionHistoryListType &&
                super.equals(other);
    }
}
