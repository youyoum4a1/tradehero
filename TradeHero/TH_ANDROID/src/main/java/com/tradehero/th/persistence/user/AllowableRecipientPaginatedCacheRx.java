package com.ayondo.academy.persistence.user;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.users.PaginatedAllowableRecipientDTO;
import com.ayondo.academy.api.users.SearchAllowableRecipientListType;
import com.ayondo.academy.network.service.UserServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class AllowableRecipientPaginatedCacheRx extends BaseFetchDTOCacheRx<SearchAllowableRecipientListType, PaginatedAllowableRecipientDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 20;

    @NonNull private final UserServiceWrapper userServiceWrapper;
    @NonNull private final Lazy<AllowableRecipientCacheRx> allowableRecipientCache;

    //<editor-fold desc="Constructors">
    @Inject public AllowableRecipientPaginatedCacheRx(
            @NonNull UserServiceWrapper userServiceWrapper,
            @NonNull Lazy<AllowableRecipientCacheRx> allowableRecipientCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.userServiceWrapper = userServiceWrapper;
        this.allowableRecipientCache = allowableRecipientCache;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<PaginatedAllowableRecipientDTO> fetch(@NonNull SearchAllowableRecipientListType key)
    {
        return userServiceWrapper.searchAllowableRecipientsRx(key);
    }

    @Override public void onNext(@NonNull SearchAllowableRecipientListType key, @NonNull PaginatedAllowableRecipientDTO value)
    {
        allowableRecipientCache.get().onNext(value.getData());
        super.onNext(key, value);
    }
}
