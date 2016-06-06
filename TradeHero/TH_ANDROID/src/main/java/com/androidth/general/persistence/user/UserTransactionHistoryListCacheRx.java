package com.androidth.general.persistence.user;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.users.UserTransactionHistoryDTOList;
import com.androidth.general.api.users.UserTransactionHistoryListType;
import com.androidth.general.network.service.UserServiceWrapper;
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
