package com.tradehero.th.api.users;

import com.tradehero.common.persistence.DTOKeyIdList;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public class UserTransactionHistoryIdList extends DTOKeyIdList<UserTransactionHistoryId>
{
    //<editor-fold desc="Constructors">
    public UserTransactionHistoryIdList()
    {
        super();
    }

    public UserTransactionHistoryIdList(@NotNull Collection<? extends UserTransactionHistoryDTO> transactionHistoryDTOs)
    {
        for (@NotNull UserTransactionHistoryDTO transactionHistoryDTO : transactionHistoryDTOs)
        {
            add(transactionHistoryDTO.getUserTransactionHistoryId());
        }
    }
    //</editor-fold>
}
