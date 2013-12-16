package com.tradehero.th.network.service;

import com.tradehero.th.api.users.SearchUserListType;
import com.tradehero.th.api.users.UserListType;
import com.tradehero.th.api.users.UserSearchResultDTO;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Repurposes queries
 * Created by xavier on 12/12/13.
 */
@Singleton public class UserServiceWrapper
{
    public static final String TAG = UserServiceWrapper.class.getSimpleName();

    @Inject UserService userService;

    @Inject public UserServiceWrapper()
    {
        super();
    }

    //<editor-fold desc="Search Users">
    public List<UserSearchResultDTO> searchUsers(UserListType key)
            throws RetrofitError
    {
        if (key instanceof SearchUserListType)
        {
            return searchUsers((SearchUserListType) key);
        }
        throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
    }

    public void searchUsers(UserListType key, Callback<List<UserSearchResultDTO>> callback)
    {
        if (key instanceof SearchUserListType)
        {
            searchUsers((SearchUserListType) key, callback);
        }
        throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
    }

    public List<UserSearchResultDTO> searchUsers(SearchUserListType key)
            throws RetrofitError
    {
        if (key.searchString == null)
        {
            throw new IllegalArgumentException("SearchUserListType.searchString cannot be null");
        }
        else if (key.page == null)
        {
            return this.userService.searchUsers(key.searchString);
        }
        else if (key.perPage == null)
        {
            return this.userService.searchUsers(key.searchString, key.page);
        }
        return this.userService.searchUsers(key.searchString, key.page, key.perPage);
    }

    public void searchUsers(SearchUserListType key, Callback<List<UserSearchResultDTO>> callback)
    {
        if (key.searchString == null)
        {
            throw new IllegalArgumentException("SearchUserListType.searchString cannot be null");
        }
        else if (key.page == null)
        {
            this.userService.searchUsers(key.searchString, callback);
        }
        else if (key.perPage == null)
        {
            this.userService.searchUsers(key.searchString, key.page, callback);
        }
        else
        {
            this.userService.searchUsers(key.searchString, key.page, key.perPage, callback);
        }
    }
    //</editor-fold>
}
