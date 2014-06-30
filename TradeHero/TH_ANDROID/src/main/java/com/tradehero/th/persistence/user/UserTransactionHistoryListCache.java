package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.users.UserTransactionHistoryDTO;
import com.tradehero.th.api.users.UserTransactionHistoryId;
import com.tradehero.th.api.users.UserTransactionHistoryIdList;
import com.tradehero.th.api.users.UserTransactionHistoryListType;
import com.tradehero.th.network.service.UserServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class UserTransactionHistoryListCache extends StraightDTOCacheNew<UserTransactionHistoryListType, UserTransactionHistoryIdList>
{
    public static final int DEFAULT_MAX_SIZE = 50;

    @NotNull private final UserServiceWrapper userServiceWrapper;
    @NotNull private final UserTransactionHistoryCache userTransactionHistoryCache;

    //<editor-fold desc="Constructors">
    @Inject public UserTransactionHistoryListCache(
            @NotNull UserServiceWrapper userServiceWrapper,
            @NotNull UserTransactionHistoryCache userTransactionHistoryCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.userServiceWrapper = userServiceWrapper;
        this.userTransactionHistoryCache = userTransactionHistoryCache;
    }
    //</editor-fold>

    @Override @NotNull public UserTransactionHistoryIdList fetch(@NotNull UserTransactionHistoryListType key)
    {
        return putInternal(key, userServiceWrapper.getUserTransactions(key));
    }

    @NotNull protected UserTransactionHistoryIdList putInternal(
            @NotNull UserTransactionHistoryListType key,
            @NotNull  List<UserTransactionHistoryDTO> fleshedValues)
    {
        UserTransactionHistoryIdList userTransactionHistoryIds = new UserTransactionHistoryIdList();
        UserTransactionHistoryId userTransactionHistoryId;
        for (@NotNull UserTransactionHistoryDTO userTransactionHistoryDTO: fleshedValues)
        {
            userTransactionHistoryId = userTransactionHistoryDTO.getUserTransactionHistoryId();
            userTransactionHistoryIds.add(userTransactionHistoryId);
            userTransactionHistoryCache.put(userTransactionHistoryId, userTransactionHistoryDTO);
        }
        put(key, userTransactionHistoryIds);
        return userTransactionHistoryIds;
    }
}
