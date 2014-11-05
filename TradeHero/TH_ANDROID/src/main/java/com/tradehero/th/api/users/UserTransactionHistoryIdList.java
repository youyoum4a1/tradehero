package com.tradehero.th.api.users;

import com.tradehero.common.persistence.DTOKeyIdList;
import java.util.Collection;
import android.support.annotation.NonNull;

@Deprecated
public class UserTransactionHistoryIdList extends DTOKeyIdList<UserTransactionHistoryId>
{
    //<editor-fold desc="Constructors">
    public UserTransactionHistoryIdList()
    {
        super();
    }

    public UserTransactionHistoryIdList(@NonNull Collection<? extends UserTransactionHistoryDTO> transactionHistoryDTOs)
    {
        for (UserTransactionHistoryDTO transactionHistoryDTO : transactionHistoryDTOs)
        {
            add(transactionHistoryDTO.getUserTransactionHistoryId());
        }
    }
    //</editor-fold>
}
