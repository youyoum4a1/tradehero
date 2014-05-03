package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.users.UserTransactionHistoryDTO;
import com.tradehero.th.api.users.UserTransactionHistoryId;
import com.tradehero.th.api.users.UserTransactionHistoryIdList;
import com.tradehero.th.api.users.UserTransactionHistoryListType;
import com.tradehero.th.network.service.UserServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton public class UserTransactionHistoryListCache extends StraightDTOCache<UserTransactionHistoryListType, UserTransactionHistoryIdList>
{
    public static final String TAG = UserTransactionHistoryListCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 50;

    @Inject protected UserServiceWrapper userServiceWrapper;
    @Inject protected UserTransactionHistoryCache userTransactionHistoryCache;

    //<editor-fold desc="Constructors">
    @Inject public UserTransactionHistoryListCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected UserTransactionHistoryIdList fetch(UserTransactionHistoryListType key)
    {
        return putInternal(key, userServiceWrapper.getUserTransactions(key));
    }

    protected UserTransactionHistoryIdList putInternal(UserTransactionHistoryListType key, List<UserTransactionHistoryDTO> fleshedValues)
    {
        UserTransactionHistoryIdList userTransactionHistoryIds = null;
        if (fleshedValues != null)
        {
            userTransactionHistoryIds = new UserTransactionHistoryIdList();
            UserTransactionHistoryId userTransactionHistoryId;
            for (UserTransactionHistoryDTO userTransactionHistoryDTO: fleshedValues)
            {
                userTransactionHistoryId = userTransactionHistoryDTO.getUserTransactionHistoryId();
                userTransactionHistoryIds.add(userTransactionHistoryId);
                userTransactionHistoryCache.put(userTransactionHistoryId, userTransactionHistoryDTO);
            }
            put(key, userTransactionHistoryIds);
        }
        return userTransactionHistoryIds;
    }
}
