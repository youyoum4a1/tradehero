package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.users.UserTransactionHistoryDTO;
import com.tradehero.th.api.users.UserTransactionHistoryId;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class UserTransactionHistoryCache extends StraightDTOCacheNew<UserTransactionHistoryId, UserTransactionHistoryDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    //<editor-fold desc="Constructors">
    @Inject public UserTransactionHistoryCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override public UserTransactionHistoryDTO fetch(@NotNull UserTransactionHistoryId key)
    {
        throw new IllegalArgumentException("There is no fetch on this cache");
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public List<UserTransactionHistoryDTO> get(@Nullable List<UserTransactionHistoryId> baseKeys)
    {
        if (baseKeys == null)
        {
            return null;
        }

        List<UserTransactionHistoryDTO> UserTransactionHistoryDTOs = new ArrayList<>();
        for (@NotNull UserTransactionHistoryId baseKey: baseKeys)
        {
            UserTransactionHistoryDTOs.add(get(baseKey));
        }
        return UserTransactionHistoryDTOs;
    }
}
