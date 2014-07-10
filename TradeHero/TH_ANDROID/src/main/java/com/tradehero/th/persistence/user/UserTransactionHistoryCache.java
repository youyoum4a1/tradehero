package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.users.UserTransactionHistoryDTO;
import com.tradehero.th.api.users.UserTransactionHistoryDTOList;
import com.tradehero.th.api.users.UserTransactionHistoryId;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class UserTransactionHistoryCache extends StraightDTOCacheNew<UserTransactionHistoryId, UserTransactionHistoryDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    //<editor-fold desc="Constructors">
    @Inject public UserTransactionHistoryCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override @NotNull public UserTransactionHistoryDTO fetch(@NotNull UserTransactionHistoryId key)
    {
        throw new IllegalArgumentException("There is no fetch on this cache");
    }

    @NotNull public UserTransactionHistoryDTOList put(
            @NotNull List<UserTransactionHistoryDTO> transactionHistoryDTOs)
    {
        UserTransactionHistoryDTOList previous = new UserTransactionHistoryDTOList();
        for (@NotNull UserTransactionHistoryDTO transactionHistoryDTO : transactionHistoryDTOs)
        {
            previous.add(put(transactionHistoryDTO.getUserTransactionHistoryId(), transactionHistoryDTO));
        }
        return previous;
    }

    @NotNull public UserTransactionHistoryDTOList get(
            @NotNull List<UserTransactionHistoryId> baseKeys)
    {
        UserTransactionHistoryDTOList UserTransactionHistoryDTOs = new UserTransactionHistoryDTOList();
        for (@NotNull UserTransactionHistoryId baseKey: baseKeys)
        {
            UserTransactionHistoryDTOs.add(get(baseKey));
        }
        return UserTransactionHistoryDTOs;
    }
}
