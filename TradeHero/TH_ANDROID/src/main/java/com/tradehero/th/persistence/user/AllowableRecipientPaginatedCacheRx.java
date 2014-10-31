package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.users.PaginatedAllowableRecipientDTO;
import com.tradehero.th.api.users.SearchAllowableRecipientListType;
import com.tradehero.th.network.service.UserServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton @UserCache
public class AllowableRecipientPaginatedCacheRx extends BaseFetchDTOCacheRx<SearchAllowableRecipientListType, PaginatedAllowableRecipientDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 20;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 2;

    @NotNull private final UserServiceWrapper userServiceWrapper;
    @NotNull private final Lazy<AllowableRecipientCacheRx> allowableRecipientCache;

    //<editor-fold desc="Constructors">
    @Inject public AllowableRecipientPaginatedCacheRx(
            @NotNull UserServiceWrapper userServiceWrapper,
            @NotNull Lazy<AllowableRecipientCacheRx> allowableRecipientCache,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.userServiceWrapper = userServiceWrapper;
        this.allowableRecipientCache = allowableRecipientCache;
    }
    //</editor-fold>

    @Override @NotNull protected Observable<PaginatedAllowableRecipientDTO> fetch(@NotNull SearchAllowableRecipientListType key)
    {
        return userServiceWrapper.searchAllowableRecipientsRx(key);
    }

    @Override public void onNext(@NotNull SearchAllowableRecipientListType key, @NotNull PaginatedAllowableRecipientDTO value)
    {
        allowableRecipientCache.get().onNext(value.getData());
        super.onNext(key, value);
    }
}
