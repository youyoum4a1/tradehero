package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.users.UserTransactionHistoryDTO;
import com.tradehero.th.api.users.UserTransactionHistoryId;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class UserTransactionHistoryCache extends StraightDTOCache<UserTransactionHistoryId, UserTransactionHistoryDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    //<editor-fold desc="Constructors">
    @Inject public UserTransactionHistoryCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected UserTransactionHistoryDTO fetch(UserTransactionHistoryId key)
    {
        throw new IllegalArgumentException("There is no fetch on this cache");
    }

    public List<UserTransactionHistoryDTO> get(List<UserTransactionHistoryId> baseKeys)
    {
        if (baseKeys == null)
        {
            return null;
        }

        List<UserTransactionHistoryDTO> UserTransactionHistoryDTOs = new ArrayList<>();
        for (UserTransactionHistoryId baseKey: baseKeys)
        {
            UserTransactionHistoryDTOs.add(get(baseKey));
        }
        return UserTransactionHistoryDTOs;
    }
}
