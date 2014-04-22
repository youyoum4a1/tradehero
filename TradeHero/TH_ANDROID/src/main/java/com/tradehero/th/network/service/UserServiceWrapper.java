package com.tradehero.th.network.service;

import com.tradehero.common.billing.googleplay.GooglePlayPurchaseDTO;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.social.HeroDTO;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.SearchAllowableRecipientListType;
import com.tradehero.th.api.users.SearchUserListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserListType;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.api.users.UserTransactionHistoryDTO;
import com.tradehero.th.api.users.payment.UpdateAlipayAccountDTO;
import com.tradehero.th.api.users.payment.UpdateAlipayAccountFormDTO;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailDTO;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailFormDTO;
import com.tradehero.th.models.user.MiddleCallbackFollowUser;
import com.tradehero.th.models.user.MiddleCallbackUpdateUserProfile;
import com.tradehero.th.models.user.payment.MiddleCallbackUpdateAlipayAccount;
import com.tradehero.th.models.user.payment.MiddleCallbackUpdatePayPalEmail;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.social.HeroKey;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Repurposes queries
 */
@Singleton public class UserServiceWrapper
{
    private final UserService userService;
    private final UserServiceAsync userServiceAsync;
    private final UserMessagingRelationshipCache userMessagingRelationshipCache;

    @Inject public UserServiceWrapper(
            UserService userService,
            UserServiceAsync userServiceAsync,
            UserMessagingRelationshipCache userMessagingRelationshipCache)
    {
        this.userService = userService;
        this.userServiceAsync = userServiceAsync;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
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

    public MiddleCallbackUpdateUserProfile updateProfile(UserBaseKey userBaseKey,
            UserFormDTO userFormDTO, Callback<UserProfileDTO> callback)
    {
        MiddleCallbackUpdateUserProfile middleCallback =
                new MiddleCallbackUpdateUserProfile(callback);
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

    //<editor-fold desc="Search Allowable Recipients">
    public PaginatedDTO<AllowableRecipientDTO> searchAllowableRecipients(SearchAllowableRecipientListType key)
    {
        if (key == null)
        {
            return userService.searchAllowableRecipients();
        }
        else if (key.page == null)
        {
            return userService.searchAllowableRecipients(key.searchString);
        }
        else if (key.perPage == null)
        {
            return userService.searchAllowableRecipients(key.searchString, key.page);
        }
        return userService.searchAllowableRecipients(key.searchString, key.page, key.perPage);
    }

    public MiddleCallback<PaginatedDTO<AllowableRecipientDTO>> searchAllowableRecipients(SearchAllowableRecipientListType key, Callback<PaginatedDTO<AllowableRecipientDTO>> callback)
    {
        MiddleCallback<PaginatedDTO<AllowableRecipientDTO>> middleCallback = new MiddleCallback<>(callback);
        if (key == null)
        {
            userServiceAsync.searchAllowableRecipients(middleCallback);
        }
        else if (key.page == null)
        {
            userServiceAsync.searchAllowableRecipients(key.searchString, middleCallback);
        }
        else if (key.perPage == null)
        {
            userServiceAsync.searchAllowableRecipients(key.searchString, key.page, middleCallback);
        }
        else
        {
            userServiceAsync.searchAllowableRecipients(key.searchString, key.page, key.perPage, middleCallback);
        }
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get User Transactions History">
    public List<UserTransactionHistoryDTO> getUserTransactions(UserBaseKey userBaseKey)
    {
        return userService.getUserTransactions(userBaseKey.key);
    }
    //</editor-fold>

    //<editor-fold desc="Update PayPal Email">
    public UpdatePayPalEmailDTO updatePayPalEmail(UserBaseKey userBaseKey,
            UpdatePayPalEmailFormDTO updatePayPalEmailFormDTO)
    {
        return userService.updatePayPalEmail(userBaseKey.key, updatePayPalEmailFormDTO);
    }

    public MiddleCallbackUpdatePayPalEmail updatePayPalEmail(UserBaseKey userBaseKey,
            UpdatePayPalEmailFormDTO updatePayPalEmailFormDTO,
            Callback<UpdatePayPalEmailDTO> callback)
    {
        MiddleCallbackUpdatePayPalEmail
                middleCallbackUpdatePayPalAccount = new MiddleCallbackUpdatePayPalEmail(callback);
        userServiceAsync.updatePayPalEmail(userBaseKey.key, updatePayPalEmailFormDTO,
                middleCallbackUpdatePayPalAccount);
        return middleCallbackUpdatePayPalAccount;
    }
    //</editor-fold>

    //<editor-fold desc="Update Alipay account">
    public MiddleCallbackUpdateAlipayAccount updateAlipayAccount(UserBaseKey userBaseKey,
            UpdateAlipayAccountFormDTO updateAlipayAccountFormDTO,
            Callback<UpdateAlipayAccountDTO> callback)
    {
        MiddleCallbackUpdateAlipayAccount
                middleCallbackUpdateAlipayAccount = new MiddleCallbackUpdateAlipayAccount(callback);
        userServiceAsync.updateAlipayAccount(userBaseKey.key, updateAlipayAccountFormDTO,
                middleCallbackUpdateAlipayAccount);
        return middleCallbackUpdateAlipayAccount;
    }
    //</editor-fold>

    //<editor-fold desc="Follow Hero">
    public UserProfileDTO follow(UserBaseKey userBaseKey)
    {
        UserProfileDTO myProfile = userService.follow(userBaseKey.key);
        userMessagingRelationshipCache.invalidate(userBaseKey);
        return myProfile;
    }

    public MiddleCallbackFollowUser follow(UserBaseKey userBaseKey, Callback<UserProfileDTO> callback)
    {
        MiddleCallbackFollowUser middleCallbackFollowUser = new MiddleCallbackFollowUser(userBaseKey, callback);
        userServiceAsync.follow(userBaseKey.key, middleCallbackFollowUser);
        return middleCallbackFollowUser;
    }

    public MiddleCallbackFollowUser freeFollow(UserBaseKey userBaseKey, Callback<UserProfileDTO> callback)
    {
        MiddleCallbackFollowUser middleCallback = new MiddleCallbackFollowUser(userBaseKey, callback);
        userService.freeFollow(userBaseKey.key, callback);
        return middleCallback;
    }

    public UserProfileDTO follow(UserBaseKey userBaseKey, GooglePlayPurchaseDTO purchaseDTO)
    {
        UserProfileDTO myProfile = userService.follow(userBaseKey.key, purchaseDTO);
        userMessagingRelationshipCache.invalidate(userBaseKey);
        return myProfile;
    }

    public MiddleCallbackFollowUser follow(UserBaseKey userBaseKey, GooglePlayPurchaseDTO purchaseDTO, Callback<UserProfileDTO> callback)
    {
        MiddleCallbackFollowUser middleCallbackFollowUser = new MiddleCallbackFollowUser(userBaseKey, callback);
        userServiceAsync.follow(userBaseKey.key, purchaseDTO, middleCallbackFollowUser);
        return middleCallbackFollowUser;
    }
    //</editor-fold>

    //<editor-fold desc="Unfollow Hero">
    public UserProfileDTO unfollow(UserBaseKey userBaseKey)
    {
        UserProfileDTO myProfile = userService.unfollow(userBaseKey.key);
        userMessagingRelationshipCache.invalidate(userBaseKey);
        return myProfile;
    }

    public MiddleCallbackFollowUser unfollow(UserBaseKey userBaseKey, Callback<UserProfileDTO> callback)
    {
        MiddleCallbackFollowUser middleCallbackFollowUser = new MiddleCallbackFollowUser(userBaseKey, callback);
        userServiceAsync.unfollow(userBaseKey.key, middleCallbackFollowUser);
        return middleCallbackFollowUser;

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

}
