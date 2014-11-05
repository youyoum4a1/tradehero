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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Singleton @UserCache @Deprecated
public class UserTransactionHistoryListCache extends StraightCutDTOCacheNew<
        UserTransactionHistoryListType,
        UserTransactionHistoryDTOList,
        UserTransactionHistoryIdList>
{
    public static final int DEFAULT_MAX_SIZE = 50;

    @NonNull private final UserServiceWrapper userServiceWrapper;
    @NonNull private final UserTransactionHistoryCacheRx userTransactionHistoryCache;

    //<editor-fold desc="Constructors">
    @Inject public UserTransactionHistoryListCache(
            @NonNull UserServiceWrapper userServiceWrapper,
            @NonNull UserTransactionHistoryCacheRx userTransactionHistoryCache,
            @NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.userServiceWrapper = userServiceWrapper;
        this.userTransactionHistoryCache = userTransactionHistoryCache;
    }
    //</editor-fold>

    @Override @NonNull public UserTransactionHistoryDTOList fetch(@NonNull UserTransactionHistoryListType key)
    {
        return userServiceWrapper.getUserTransactions(key);
    }

    @NonNull @Override protected UserTransactionHistoryIdList cutValue(
            @NonNull UserTransactionHistoryListType key,
            @NonNull UserTransactionHistoryDTOList value)
    {
        userTransactionHistoryCache.onNext(value);
        return new UserTransactionHistoryIdList(value);
    }

    @Nullable @Override protected UserTransactionHistoryDTOList inflateValue(
            @NonNull UserTransactionHistoryListType key,
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
