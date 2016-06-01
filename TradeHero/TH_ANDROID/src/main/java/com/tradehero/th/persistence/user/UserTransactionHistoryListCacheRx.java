package com.ayondo.academy.persistence.user;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.users.UserTransactionHistoryDTOList;
import com.ayondo.academy.api.users.UserTransactionHistoryListType;
import com.ayondo.academy.network.service.UserServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class UserTransactionHistoryListCacheRx extends BaseFetchDTOCacheRx<
        UserTransactionHistoryListType,
        UserTransactionHistoryDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;

    @NonNull private final UserServiceWrapper userServiceWrapper;
    @NonNull private final UserTransactionHistoryCacheRx userTransactionHistoryCache;

    //<editor-fold desc="Constructors">
    @Inject public UserTransactionHistoryListCacheRx(
            @NonNull UserServiceWrapper userServiceWrapper,
            @NonNull UserTransactionHistoryCacheRx userTransactionHistoryCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.userServiceWrapper = userServiceWrapper;
        this.userTransactionHistoryCache = userTransactionHistoryCache;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<UserTransactionHistoryDTOList> fetch(@NonNull UserTransactionHistoryListType key)
    {
        return userServiceWrapper.getUserTransactionsRx(key);
    }

    @Override public void onNext(@NonNull UserTransactionHistoryListType key, @NonNull UserTransactionHistoryDTOList value)
    {
        userTransactionHistoryCache.onNext(value);
        super.onNext(key, value);
    }
}
