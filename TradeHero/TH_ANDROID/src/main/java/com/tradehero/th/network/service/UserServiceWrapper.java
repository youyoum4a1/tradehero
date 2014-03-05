package com.tradehero.th.network.service;

import com.tradehero.common.billing.googleplay.GooglePlayPurchaseDTO;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.users.SearchUserListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserListType;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.api.users.UserTransactionHistoryDTO;
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

    //<editor-fold desc="Sign-Up With Email">
    public UserProfileDTO signUpWithEmail(
            String authorization,
            UserFormDTO userFormDTO)
            throws RetrofitError
    {
        return userService.signUpWithEmail(
                authorization,
                userFormDTO.biography,
                userFormDTO.deviceToken,
                userFormDTO.displayName,
                userFormDTO.email,
                userFormDTO.emailNotificationsEnabled,
                userFormDTO.firstName,
                userFormDTO.lastName,
                userFormDTO.location,
                userFormDTO.password,
                userFormDTO.passwordConfirmation,
                userFormDTO.pushNotificationsEnabled,
                userFormDTO.username,
                userFormDTO.website);
    }

    public void signUpWithEmail(
            String authorization,
            UserFormDTO userFormDTO,
            Callback<UserProfileDTO> callback)
    {
        userService.signUpWithEmail(
                authorization,
                userFormDTO.biography,
                userFormDTO.deviceToken,
                userFormDTO.displayName,
                userFormDTO.email,
                userFormDTO.emailNotificationsEnabled,
                userFormDTO.firstName,
                userFormDTO.lastName,
                userFormDTO.location,
                userFormDTO.password,
                userFormDTO.passwordConfirmation,
                userFormDTO.pushNotificationsEnabled,
                userFormDTO.username,
                userFormDTO.website,
                callback);
    }
    //</editor-fold>

    //<editor-fold desc="Update Profile">
    public UserProfileDTO updateProfile(UserBaseKey userBaseKey, UserFormDTO userFormDTO)
            throws RetrofitError
    {
        return userService.updateProfile(
                userBaseKey.key,
                userFormDTO.deviceToken,
                userFormDTO.displayName,
                userFormDTO.email,
                userFormDTO.firstName,
                userFormDTO.lastName,
                userFormDTO.password,
                userFormDTO.passwordConfirmation,
                userFormDTO.username,
                userFormDTO.emailNotificationsEnabled,
                userFormDTO.pushNotificationsEnabled,
                userFormDTO.biography,
                userFormDTO.location,
                userFormDTO.website
        );
    }

    public void updateProfile(UserBaseKey userBaseKey, UserFormDTO userFormDTO, Callback<UserProfileDTO> callback)
    {
        userService.updateProfile(
                userBaseKey.key,
                userFormDTO.deviceToken,
                userFormDTO.displayName,
                userFormDTO.email,
                userFormDTO.firstName,
                userFormDTO.lastName,
                userFormDTO.password,
                userFormDTO.passwordConfirmation,
                userFormDTO.username,
                userFormDTO.emailNotificationsEnabled,
                userFormDTO.pushNotificationsEnabled,
                userFormDTO.biography,
                userFormDTO.location,
                userFormDTO.website,
                callback
        );
    }

    public UserProfileDTO updateProfilePropertyEmailNotifications(
            UserBaseKey userBaseKey,
            Boolean emailNotificationsEnabled)
            throws RetrofitError
    {
        UserFormDTO userFormDTO = new UserFormDTO();
        userFormDTO.emailNotificationsEnabled = emailNotificationsEnabled;
        return this.updateProfile(userBaseKey, userFormDTO);
    }

    public void updateProfilePropertyEmailNotifications(
            UserBaseKey userBaseKey,
            Boolean emailNotificationsEnabled,
            Callback<UserProfileDTO> callback)
    {
        UserFormDTO userFormDTO = new UserFormDTO();
        userFormDTO.emailNotificationsEnabled = emailNotificationsEnabled;
        this.updateProfile(userBaseKey, userFormDTO, callback);
    }

    public UserProfileDTO updateProfilePropertyPushNotifications(
            UserBaseKey userBaseKey,
            Boolean pushNotificationsEnabled)
            throws RetrofitError
    {
        UserFormDTO userFormDTO = new UserFormDTO();
        userFormDTO.pushNotificationsEnabled = pushNotificationsEnabled;
        return this.updateProfile(userBaseKey, userFormDTO);
    }

    public void updateProfilePropertyPushNotifications(
            UserBaseKey userBaseKey,
            Boolean pushNotificationsEnabled,
            Callback<UserProfileDTO> callback)
    {
        UserFormDTO userFormDTO = new UserFormDTO();
        userFormDTO.pushNotificationsEnabled = pushNotificationsEnabled;
        this.updateProfile(userBaseKey, userFormDTO, callback);
    }
    //</editor-fold>

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

    //<editor-fold desc="Get User Transactions History">
    public List<UserTransactionHistoryDTO> getUserTransactions(UserBaseKey userBaseKey)
    {
        return userService.getUserTransactions(userBaseKey.key);
    }

    public void getUserTransactions(UserBaseKey userBaseKey, Callback<List<UserTransactionHistoryDTO>> callback)
    {
        userService.getUserTransactions(userBaseKey.key, callback);
    }
    //</editor-fold>

    //<editor-fold desc="Follow Hero">
    public UserProfileDTO follow(UserBaseKey userBaseKey)
    {
        return userService.follow(userBaseKey.key);
    }

    public void follow(UserBaseKey userBaseKey, Callback<UserProfileDTO> callback)
    {
        userService.follow(userBaseKey.key, callback);
    }

    public UserProfileDTO follow(UserBaseKey userBaseKey, GooglePlayPurchaseDTO purchaseDTO)
    {
        return userService.follow(userBaseKey.key, purchaseDTO);
    }

    public void follow(UserBaseKey userBaseKey, GooglePlayPurchaseDTO purchaseDTO, Callback<UserProfileDTO> callback)
    {
        userService.follow(userBaseKey.key, purchaseDTO, callback);
    }
    //</editor-fold>

    //<editor-fold desc="Unfollow Hero">
    public UserProfileDTO unfollow(UserBaseKey userBaseKey)
    {
        return userService.unfollow(userBaseKey.key);
    }

    public void unfollow(UserBaseKey userBaseKey, Callback<UserProfileDTO> callback)
    {
        userService.unfollow(userBaseKey.key, callback);
    }
    //</editor-fold>
}
