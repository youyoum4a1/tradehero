package com.tradehero.th.api.users;

import com.tradehero.common.persistence.DTOKeyIdList;
import java.util.Collection;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 7:03 PM To change this template use File | Settings | File Templates. */
public class UserTransactionHistoryIdList extends DTOKeyIdList<UserTransactionHistoryId>
{
    public static final String TAG = UserTransactionHistoryIdList.class.getSimpleName();

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
