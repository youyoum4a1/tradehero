package com.tradehero.th.network.service;

import com.tradehero.common.billing.googleplay.GooglePlayPurchaseDTO;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.social.HeroDTO;
import com.tradehero.th.api.users.SearchUserListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserListType;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserRelationsDTO;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.api.users.UserTransactionHistoryDTO;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailDTO;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailFormDTO;
import com.tradehero.th.models.user.MiddleCallbackUpdateUserProfile;
import com.tradehero.th.models.user.payment.MiddleCallbackUpdatePayPalEmail;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.social.HeroKey;
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
    private final UserService userService;
    private final UserServiceAsync userServiceAsync;

    @Inject public UserServiceWrapper(UserService userService, UserServiceAsync userServiceAsync)
    {
        this.userService = userService;
        this.userServiceAsync = userServiceAsync;
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

    // TODO use MiddleCallback
    @Deprecated
    public void signUpWithEmail(
            String authorization,
            UserFormDTO userFormDTO,
            Callback<UserProfileDTO> callback)
    {
        userServiceAsync.signUpWithEmail(
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

    public MiddleCallbackUpdateUserProfile updateProfile(UserBaseKey userBaseKey, UserFormDTO userFormDTO, Callback<UserProfileDTO> callback)
    {
        MiddleCallbackUpdateUserProfile middleCallback = new MiddleCallbackUpdateUserProfile(callback);
        userServiceAsync.updateProfile(
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
                middleCallback
        );
        return middleCallback;
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

    public MiddleCallbackUpdateUserProfile updateProfilePropertyEmailNotifications(
            UserBaseKey userBaseKey,
            Boolean emailNotificationsEnabled,
            Callback<UserProfileDTO> callback)
    {
        UserFormDTO userFormDTO = new UserFormDTO();
        userFormDTO.emailNotificationsEnabled = emailNotificationsEnabled;
        return this.updateProfile(userBaseKey, userFormDTO, callback);
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

    public MiddleCallbackUpdateUserProfile updateProfilePropertyPushNotifications(
            UserBaseKey userBaseKey,
            Boolean pushNotificationsEnabled,
            Callback<UserProfileDTO> callback)
    {
        UserFormDTO userFormDTO = new UserFormDTO();
        userFormDTO.pushNotificationsEnabled = pushNotificationsEnabled;
        return this.updateProfile(userBaseKey, userFormDTO, callback);
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
    //</editor-fold>

    //<editor-fold desc="Get User Transactions History">
    public List<UserTransactionHistoryDTO> getUserTransactions(UserBaseKey userBaseKey)
    {
        return userService.getUserTransactions(userBaseKey.key);
    }
    //</editor-fold>

    //<editor-fold desc="Update PayPal Email">
    public UpdatePayPalEmailDTO updatePayPalEmail(UserBaseKey userBaseKey, UpdatePayPalEmailFormDTO updatePayPalEmailFormDTO)
    {
        return userService.updatePayPalEmail(userBaseKey.key, updatePayPalEmailFormDTO);
    }

    public MiddleCallbackUpdatePayPalEmail updatePayPalEmail(UserBaseKey userBaseKey, UpdatePayPalEmailFormDTO updatePayPalEmailFormDTO, Callback<UpdatePayPalEmailDTO> callback)
    {
        MiddleCallbackUpdatePayPalEmail middleCallbackUpdatePayPalEmail = new MiddleCallbackUpdatePayPalEmail(callback);
        userServiceAsync.updatePayPalEmail(userBaseKey.key, updatePayPalEmailFormDTO, middleCallbackUpdatePayPalEmail);
        return middleCallbackUpdatePayPalEmail;
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

    public MiddleCallback<UserProfileDTO> freeFollow(UserBaseKey userBaseKey, Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new MiddleCallback<>(callback);
        userService.freeFollow(userBaseKey.key, callback);
        return middleCallback;
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

    public List<HeroDTO> getHeroes(HeroKey heroKey)
    {
        switch (heroKey.heroType)
        {
            case PREMIUM:
                return userService.getHeroes(heroKey.followerKey.key);
            case FREE:
                return userService.getHeroes(heroKey.followerKey.key);
            case ALL:
                return userService.getHeroes(heroKey.followerKey.key);
        }
        return null;
    }

    public void getHeroes(HeroKey heroKey,Callback<List<HeroDTO>> callback)
    {
        switch (heroKey.heroType)
        {
            case PREMIUM:
                userServiceAsync.getHeroes(heroKey.followerKey.key,callback);
            case FREE:
                userServiceAsync.getHeroes(heroKey.followerKey.key, callback);
            case ALL:
                userServiceAsync.getHeroes(heroKey.followerKey.key, callback);
        }
    }
    //</editor-fold>

    public MiddleCallback<UserRelationsDTO> getRelations(Callback<UserRelationsDTO> callback)
    {
        MiddleCallback<UserRelationsDTO> middleCallback = new MiddleCallback<>(callback);
        userServiceAsync.getRelations(callback);
        return middleCallback;
    }
}
