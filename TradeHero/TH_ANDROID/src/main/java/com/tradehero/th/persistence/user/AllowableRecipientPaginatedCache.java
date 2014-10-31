package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.users.PaginatedAllowableRecipientDTO;
import com.tradehero.th.api.users.SearchAllowableRecipientListType;
import com.tradehero.th.network.service.UserServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton @UserCache
public class AllowableRecipientPaginatedCache extends StraightCutDTOCacheNew<SearchAllowableRecipientListType, PaginatedAllowableRecipientDTO, PaginatedUserBaseKey>
{
    public static final int DEFAULT_MAX_SIZE = 20;

    @NotNull private final UserServiceWrapper userServiceWrapper;
    @NotNull private final Lazy<AllowableRecipientCache> allowableRecipientCache;

    //<editor-fold desc="Constructors">
    @Inject public AllowableRecipientPaginatedCache(
            @NotNull UserServiceWrapper userServiceWrapper,
            @NotNull Lazy<AllowableRecipientCache> allowableRecipientCache,
            @NotNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(DEFAULT_MAX_SIZE, dtoCacheUtil);
        this.userServiceWrapper = userServiceWrapper;
        this.allowableRecipientCache = allowableRecipientCache;
    }
    //</editor-fold>

    @Override @NotNull public PaginatedAllowableRecipientDTO fetch(@NotNull SearchAllowableRecipientListType key)
            throws Throwable
    {
        return userServiceWrapper.searchAllowableRecipients(key);
    }

    @NotNull @Override protected PaginatedUserBaseKey cutValue(
            @NotNull SearchAllowableRecipientListType key,
            @NotNull PaginatedAllowableRecipientDTO value)
    {
        return new PaginatedUserBaseKey(value, allowableRecipientCache.get());
    }

    @Nullable @Override protected PaginatedAllowableRecipientDTO inflateValue(
            @NotNull SearchAllowableRecipientListType key,
            @Nullable PaginatedUserBaseKey cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        return cutValue.inflate(allowableRecipientCache.get());
    }
}
