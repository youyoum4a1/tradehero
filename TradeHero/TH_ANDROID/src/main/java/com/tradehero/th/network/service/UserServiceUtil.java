package com.tradehero.th.network.service;

import com.tradehero.th.api.users.SearchUserListType;
import com.tradehero.th.api.users.UserListType;
import com.tradehero.th.api.users.UserSearchResultDTO;
import java.util.List;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Repurposes queries
 * Created by xavier on 12/12/13.
 */
public class UserServiceUtil
{
    public static final String TAG = UserServiceUtil.class.getSimpleName();

    //<editor-fold desc="Search Users">
    public static List<UserSearchResultDTO> searchUsers(UserService userService, UserListType key)
            throws RetrofitError
    {
        if (key instanceof SearchUserListType)
        {
            return searchUsers(userService, (SearchUserListType) key);
        }
        throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
    }

    public static void searchUsers(UserService userService, UserListType key, Callback<List<UserSearchResultDTO>> callback)
    {
        if (key instanceof SearchUserListType)
        {
            searchUsers(userService, (SearchUserListType) key, callback);
        }
        throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
    }

    public static List<UserSearchResultDTO> searchUsers(UserService userService, SearchUserListType key)
            throws RetrofitError
    {
        if (key.searchString == null)
        {
            throw new IllegalArgumentException("SearchUserListType.searchString cannot be null");
        }
        else if (key.page == null)
        {
            return userService.searchUsers(key.searchString);
        }
        else if (key.perPage == null)
        {
            return userService.searchUsers(key.searchString, key.page);
        }
        return userService.searchUsers(key.searchString, key.page, key.perPage);
    }

    public static void searchUsers(UserService userService, SearchUserListType key, Callback<List<UserSearchResultDTO>> callback)
    {
        if (key.searchString == null)
        {
            throw new IllegalArgumentException("SearchUserListType.searchString cannot be null");
        }
        else if (key.page == null)
        {
            userService.searchUsers(key.searchString, callback);
        }
        else if (key.perPage == null)
        {
            userService.searchUsers(key.searchString, key.page, callback);
        }
        else
        {
            userService.searchUsers(key.searchString, key.page, key.perPage, callback);
        }
    }
    //</editor-fold>
}
