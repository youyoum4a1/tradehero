package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.SearchAllowableRecipientListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.UserServiceWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class AllowableRecipientPaginatedCache extends StraightDTOCacheNew<SearchAllowableRecipientListType, PaginatedDTO<AllowableRecipientDTO>>
{
    public static final int DEFAULT_MAX_SIZE = 20;

    @NotNull private final UserServiceWrapper userServiceWrapper;
    @NotNull private final UserMessagingRelationshipCache userRelationCache;
    @NotNull private final UserProfileCompactCache userProfileCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public AllowableRecipientPaginatedCache(
            @NotNull UserServiceWrapper userServiceWrapper,
            @NotNull UserMessagingRelationshipCache userRelationCache,
            @NotNull UserProfileCompactCache userProfileCompactCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.userServiceWrapper = userServiceWrapper;
        this.userRelationCache = userRelationCache;
        this.userProfileCompactCache = userProfileCompactCache;
    }
    //</editor-fold>

    @Override public PaginatedDTO<AllowableRecipientDTO> fetch(@NotNull SearchAllowableRecipientListType key)
            throws Throwable
    {
        return putInternal(key, userServiceWrapper.searchAllowableRecipients(key));
    }

    @Contract("_, null -> null; _, !null -> !null") @Nullable
    private PaginatedDTO<AllowableRecipientDTO> putInternal(
            @NotNull SearchAllowableRecipientListType key,
            PaginatedDTO<AllowableRecipientDTO> value)
    {
        if (value == null)
        {
            return null;
        }

        PaginatedDTO<AllowableRecipientDTO> reprocessed = new PaginatedDTO<>();
        reprocessed.setPagination(value.getPagination());
        if (value.getData() != null)
        {
            List<AllowableRecipientDTO> data = new ArrayList<>();
            for (AllowableRecipientDTO allowableRecipientDTO : value.getData())
            {
                //data.add(allowableRecipientDTO.user.getBaseKey());
                data.add(allowableRecipientDTO);
                put(allowableRecipientDTO);
            }
            reprocessed.setData(data);
        }
        return reprocessed;
    }

    @Contract("null -> null; !null -> !null") @Nullable
    private AllowableRecipientDTO get(@Nullable UserBaseKey userBaseKey)
    {
        if (userBaseKey == null)
        {
            return null;
        }
        AllowableRecipientDTO allowableRecipientDTO = new AllowableRecipientDTO();
        allowableRecipientDTO.user = userProfileCompactCache.get(userBaseKey);
        allowableRecipientDTO.relationship = userRelationCache.get(userBaseKey);
        return allowableRecipientDTO;
    }

    @Contract("null -> null; !null -> !null") @Nullable
    private AllowableRecipientDTO put(@Nullable AllowableRecipientDTO value)
    {
        if (value == null || value.user == null)
        {
            return null;
        }
        UserBaseKey userBaseKey = value.user.getBaseKey();
        AllowableRecipientDTO previous = new AllowableRecipientDTO();
        previous.user = userProfileCompactCache.put(userBaseKey, value.user);
        previous.relationship = userRelationCache.put(userBaseKey, value.relationship);
        return previous;
    }
}
