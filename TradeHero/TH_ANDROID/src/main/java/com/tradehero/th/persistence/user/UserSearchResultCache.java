package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserSearchResultDTO;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

@Singleton public class UserSearchResultCache extends StraightDTOCache<UserBaseKey, UserSearchResultDTO>
{
    public static final int DEFAULT_MAX_SIZE = 5000;

    //<editor-fold desc="Constructors">
    @Inject public UserSearchResultCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected UserSearchResultDTO fetch(UserBaseKey key) throws Throwable
    {
        throw new IllegalStateException("There is no fetch mechanism on this cache");
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public List<UserSearchResultDTO> put(@Nullable List<UserSearchResultDTO> values)
    {
        if (values == null)
        {
            return null;
        }

        List<UserSearchResultDTO> previousValues = new ArrayList<>();

        for (UserSearchResultDTO securityCompactDTO: values)
        {
            if (securityCompactDTO != null && securityCompactDTO.getUserBaseKey() != null)
            {
                previousValues.add(put(securityCompactDTO.getUserBaseKey(), securityCompactDTO));
            }
            else
            {
                previousValues.add(null);
            }
        }

        return previousValues;
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public List<UserSearchResultDTO> get(@Nullable List<UserBaseKey> keys)
    {
        if (keys == null)
        {
            return null;
        }

        List<UserSearchResultDTO> values = new ArrayList<>();

        for (UserBaseKey userBaseKey: keys)
        {
            if (userBaseKey != null)
            {
                values.add(get(userBaseKey));
            }
            else
            {
                values.add(null);
            }
        }

        return values;
    }
}
