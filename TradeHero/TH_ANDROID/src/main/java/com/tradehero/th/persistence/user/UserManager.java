package com.tradehero.th.persistence.user;

import com.tradehero.common.cache.DatabaseCache;
import com.tradehero.common.persistence.Query;
import com.tradehero.th.api.users.UserProfileDTO;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class UserManager
{
    @Inject DatabaseCache dbCache;
    @Inject AbstractUserStore userStore;

    public UserProfileDTO getUser(int userId, boolean forceReload) throws IOException
    {
        Query query = new Query();
        query.setId(userId);
        return getUsers(query, forceReload).get(0);
    }

    public List<UserProfileDTO> getUsers(Query query, boolean forceReload) throws IOException
    {
        userStore.setQuery(query);
        return forceReload ? dbCache.requestAndStore(userStore) : dbCache.loadOrRequest(userStore);
    }
}
