package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.users.UserTransactionHistoryDTOList;
import com.tradehero.th.api.users.UserTransactionHistoryIdList;
import com.tradehero.th.api.users.UserTransactionHistoryListType;
import com.tradehero.th.network.service.UserServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class UserTransactionHistoryListCache extends StraightCutDTOCacheNew<
        UserTransactionHistoryListType,
        UserTransactionHistoryDTOList,
        UserTransactionHistoryIdList>
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

    @Override @NotNull public UserTransactionHistoryDTOList fetch(@NotNull UserTransactionHistoryListType key)
    {
        return userServiceWrapper.getUserTransactions(key);
    }

    @NotNull @Override protected UserTransactionHistoryIdList cutValue(
            @NotNull UserTransactionHistoryListType key,
            @NotNull UserTransactionHistoryDTOList value)
    {
        userTransactionHistoryCache.put(value);
        return new UserTransactionHistoryIdList(value);
    }

    @Nullable @Override protected UserTransactionHistoryDTOList inflateValue(
            @NotNull UserTransactionHistoryListType key,
            @Nullable UserTransactionHistoryIdList cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        UserTransactionHistoryDTOList value = userTransactionHistoryCache.get(cutValue);
        if (value.hasNullItem())
        {
            return null;
        }
        return value;
    }
}
