package com.tradehero.th.api.users;

import com.tradehero.common.persistence.DTOKeyIdList;
import java.util.Collection;

public class UserTransactionHistoryIdList extends DTOKeyIdList<UserTransactionHistoryId>
{
    //<editor-fold desc="Constructors">
    public UserTransactionHistoryIdList()
    {
        super();
    }

    public UserTransactionHistoryIdList(int capacity)
    {
        super(capacity);
    }

    public UserTransactionHistoryIdList(Collection<? extends UserTransactionHistoryId> collection)
    {
        super(collection);
    }
    //</editor-fold>
}
