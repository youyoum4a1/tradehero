package com.tradehero.th.persistence.user;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.UserBaseKey;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton @UserCache
public class AllowableRecipientCache extends StraightCutDTOCacheNew<UserBaseKey, AllowableRecipientDTO, AllowableRecipientCutDTO>
{
    public static final int DEFAULT_MAX_SIZE = 300;

    @NotNull private final Lazy<UserMessagingRelationshipCache> userMessagingRelationshipCache;

    //<editor-fold desc="Constructors">
    @Inject public AllowableRecipientCache(
            @NotNull Lazy<UserMessagingRelationshipCache> userMessagingRelationshipCache,
            @NotNull DTOCacheUtilNew dtoCacheUtil)
    {
        this(DEFAULT_MAX_SIZE, userMessagingRelationshipCache, dtoCacheUtil);
    }

    public AllowableRecipientCache(int maxSize,
            @NotNull Lazy<UserMessagingRelationshipCache> userMessagingRelationshipCache,
            @NotNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(maxSize, dtoCacheUtil);
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
    }
    //</editor-fold>

    @NotNull @Override public AllowableRecipientDTO fetch(@NotNull UserBaseKey key) throws Throwable
    {
        throw new IllegalArgumentException("There is no fetcher on this cache");
    }

    @NotNull @Override protected AllowableRecipientCutDTO cutValue(@NotNull UserBaseKey key, @NotNull AllowableRecipientDTO value)
    {
        return new AllowableRecipientCutDTO(value, userMessagingRelationshipCache.get());
    }

    @Nullable @Override protected AllowableRecipientDTO inflateValue(@NotNull UserBaseKey key, @Nullable AllowableRecipientCutDTO cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        return cutValue.inflate(userMessagingRelationshipCache.get());
    }

    @NotNull public BaseArrayList<AllowableRecipientDTO> put(@NotNull List<AllowableRecipientDTO> allowableRecipientDTOs)
    {
        BaseArrayList<AllowableRecipientDTO> previous = new BaseArrayList<>();
        for (AllowableRecipientDTO allowableRecipientDTO : allowableRecipientDTOs)
        {
            previous.add(put(allowableRecipientDTO.user.getBaseKey(), allowableRecipientDTO));
        }
        return previous;
    }

    @NotNull public BaseArrayList<AllowableRecipientDTO> get(@NotNull List<UserBaseKey> userBaseKeys)
    {
        BaseArrayList<AllowableRecipientDTO> cached = new BaseArrayList<>();
        for (UserBaseKey userBaseKey : userBaseKeys)
        {
            cached.add(get(userBaseKey));
        }
        return cached;
    }
}
