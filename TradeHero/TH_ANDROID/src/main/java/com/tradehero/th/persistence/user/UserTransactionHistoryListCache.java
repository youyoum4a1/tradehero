package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.users.UserTransactionHistoryDTOList;
import com.tradehero.th.api.users.UserTransactionHistoryId;
import com.tradehero.th.api.users.UserTransactionHistoryIdList;
import com.tradehero.th.api.users.UserTransactionHistoryListType;
import com.tradehero.th.network.service.UserServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton @UserCache @Deprecated
public class UserTransactionHistoryListCache extends StraightCutDTOCacheNew<
        UserTransactionHistoryListType,
        UserTransactionHistoryDTOList,
        UserTransactionHistoryIdList>
{
    public static final int DEFAULT_MAX_SIZE = 50;

    @NotNull private final UserServiceWrapper userServiceWrapper;
    @NotNull private final UserTransactionHistoryCacheRx userTransactionHistoryCache;

    //<editor-fold desc="Constructors">
    @Inject public UserTransactionHistoryListCache(
            @NotNull UserServiceWrapper userServiceWrapper,
            @NotNull UserTransactionHistoryCacheRx userTransactionHistoryCache,
            @NotNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
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
        userTransactionHistoryCache.onNext(value);
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
        UserTransactionHistoryDTOList value = new UserTransactionHistoryDTOList();
        for (UserTransactionHistoryId key1 : cutValue)
        {
            value.add(userTransactionHistoryCache.getValue(key1));
        }
        if (value.hasNullItem())
        {
            return null;
        }
        return value;
    }
}
