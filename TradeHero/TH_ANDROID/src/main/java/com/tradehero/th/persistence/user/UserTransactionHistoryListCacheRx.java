package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.users.UserTransactionHistoryDTOList;
import com.tradehero.th.api.users.UserTransactionHistoryListType;
import com.tradehero.th.network.service.UserServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import rx.Observable;

@Singleton @UserCache
public class UserTransactionHistoryListCacheRx extends BaseFetchDTOCacheRx<
        UserTransactionHistoryListType,
        UserTransactionHistoryDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 5;

    @NonNull private final UserServiceWrapper userServiceWrapper;
    @NonNull private final UserTransactionHistoryCacheRx userTransactionHistoryCache;

    //<editor-fold desc="Constructors">
    @Inject public UserTransactionHistoryListCacheRx(
            @NonNull UserServiceWrapper userServiceWrapper,
            @NonNull UserTransactionHistoryCacheRx userTransactionHistoryCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
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
