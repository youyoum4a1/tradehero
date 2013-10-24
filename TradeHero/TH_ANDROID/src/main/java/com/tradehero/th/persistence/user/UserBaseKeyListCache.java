package com.tradehero.th.persistence.user;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.users.SearchUserListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserBaseKeyList;
import com.tradehero.th.api.users.UserListType;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.network.BasicRetrofitErrorHandler;
import com.tradehero.th.network.service.UserService;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.RetrofitError;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 5:04 PM To change this template use File | Settings | File Templates. */
@Singleton public class UserBaseKeyListCache extends StraightDTOCache<UserListType, UserBaseKeyList>
{
    public static final String TAG = UserBaseKeyListCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 50;

    @Inject protected Lazy<UserService> userService;
    @Inject protected Lazy<UserSearchResultCache> userSearchResultCache;

    //<editor-fold desc="Constructors">
    @Inject public UserBaseKeyListCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected UserBaseKeyList fetch(UserListType key)
    {
        THLog.d(TAG, "fetch " + key);
        try
        {
            if (key instanceof SearchUserListType)
            {
                return putInternal(key, fetch((SearchUserListType) key));
            }
            throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
        }
        catch (RetrofitError retrofitError)
        {
            BasicRetrofitErrorHandler.handle(retrofitError);
            THLog.e(TAG, "Error requesting key " + key.toString(), retrofitError);
        }
        return null;
    }

    protected List<UserSearchResultDTO> fetch(SearchUserListType key) throws RetrofitError
    {
        if (key == null)
        {
            return null;
        }
        else if (key.getPerPage() != null)
        {
            return userService.get().searchUsers(key.getSearchString(), key.getPage(), key.getPerPage());
        }
        else if (key.getPage() != null)
        {
            return userService.get().searchUsers(key.getSearchString(), key.getPage());
        }
        else if (key.getSearchString() != null)
        {
            return userService.get().searchUsers(key.getSearchString());
        }
        else
        {
            return null;
        }
    }

    protected UserBaseKeyList putInternal(UserListType key, List<UserSearchResultDTO> fleshedValues)
    {
        UserBaseKeyList userBaseKeys = null;
        if (fleshedValues != null)
        {
            userBaseKeys = new UserBaseKeyList();
            UserBaseKey userBaseKey;
            for (UserSearchResultDTO userSearchResultDTO: fleshedValues)
            {
                if (userSearchResultDTO != null)
                {
                    userBaseKey = userSearchResultDTO.getUserBaseKey();
                    userBaseKeys.add(userBaseKey);
                    userSearchResultCache.get().put(userBaseKey, userSearchResultDTO);
                }
            }
            put(key, userBaseKeys);
        }
        return userBaseKeys;
    }
}
