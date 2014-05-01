package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.SearchAllowableRecipientListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.UserServiceWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AllowableRecipientPaginatedCache extends StraightDTOCache<SearchAllowableRecipientListType, PaginatedDTO<UserBaseKey>>
{
    public static final int DEFAULT_MAX_SIZE = 20;

    @Inject UserServiceWrapper userServiceWrapper;
    @Inject UserMessagingRelationshipCache userRelationCache;
    @Inject UserProfileCompactCache userProfileCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public AllowableRecipientPaginatedCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected PaginatedDTO<UserBaseKey> fetch(SearchAllowableRecipientListType key)
            throws Throwable
    {
        return putInternal(key, userServiceWrapper.searchAllowableRecipients(key));
    }

    private PaginatedDTO<UserBaseKey> putInternal(SearchAllowableRecipientListType key, PaginatedDTO<AllowableRecipientDTO> value)
    {
        if (value == null)
        {
            return null;
        }

        PaginatedDTO<UserBaseKey> reprocessed = new PaginatedDTO<>();
        reprocessed.setPagination(value.getPagination());
        if (value.getData() != null)
        {
            List<UserBaseKey> data = new ArrayList<>();
            for (AllowableRecipientDTO allowableRecipientDTO : value.getData())
            {
                data.add(allowableRecipientDTO.user.getBaseKey());
                put(allowableRecipientDTO);
            }
            reprocessed.setData(data);
        }
        return reprocessed;
    }

    private AllowableRecipientDTO get(UserBaseKey userBaseKey)
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

    private AllowableRecipientDTO put(AllowableRecipientDTO value)
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
