package com.tradehero.th.network.service;

import com.tradehero.common.billing.googleplay.GooglePlayPurchaseDTO;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.social.HeroDTOList;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.users.*;
import com.tradehero.th.api.users.password.ForgotPasswordDTO;
import com.tradehero.th.api.users.password.ForgotPasswordFormDTO;
import com.tradehero.th.api.users.payment.UpdateAlipayAccountDTO;
import com.tradehero.th.api.users.payment.UpdateAlipayAccountFormDTO;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailDTO;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailFormDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.user.DTOProcessorFollowUser;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import com.tradehero.th.models.user.DTOProcessorUserDeleted;
import com.tradehero.th.models.user.payment.DTOProcessorUpdateAlipayAccount;
import com.tradehero.th.models.user.payment.DTOProcessorUpdatePayPalEmail;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import retrofit.Callback;
import retrofit.client.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton public class UserServiceWrapper
{
    private final UserService userService;
    private final UserServiceAsync userServiceAsync;
    private final UserProfileCache userProfileCache;
    private final UserMessagingRelationshipCache userMessagingRelationshipCache;
    private final Lazy<HeroListCache> heroListCache;
    private final GetPositionsCache getPositionsCache;

    @Inject public UserServiceWrapper(
            UserService userService,
            UserServiceAsync userServiceAsync,
            UserProfileCache userProfileCache,
            UserMessagingRelationshipCache userMessagingRelationshipCache,
            Lazy<HeroListCache> heroListCache,
            GetPositionsCache getPositionsCache)
    {
        this.userService = userService;
        this.userServiceAsync = userServiceAsync;
        this.userProfileCache = userProfileCache;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
        this.heroListCache = heroListCache;
        this.getPositionsCache = getPositionsCache;
    }

    //<editor-fold desc="DTO Processors">
    protected DTOProcessor<UserProfileDTO> createUpdateProfileProcessor()
    {
        return new DTOProcessorUpdateUserProfile(userProfileCache);
    }

    protected DTOProcessor<UserProfileDTO> createFollowUserProcessor(UserBaseKey userToFollow)
    {
        return new DTOProcessorFollowUser(userProfileCache,
                heroListCache.get(), getPositionsCache, userMessagingRelationshipCache,
                userToFollow);
    }

    protected DTOProcessor<UpdatePayPalEmailDTO> createUpdatePaypalEmailProcessor(UserBaseKey playerId)
    {
        return new DTOProcessorUpdatePayPalEmail(userProfileCache, playerId);
    }

    protected DTOProcessor<UpdateAlipayAccountDTO> createUpdateAlipayAccountProcessor(UserBaseKey playerId)
    {
        return new DTOProcessorUpdateAlipayAccount(userProfileCache, playerId);
    }

    protected DTOProcessor<Response> createUserDeletedProcessor(UserBaseKey playerId)
    {
        return new DTOProcessorUserDeleted(userProfileCache, playerId);
    }
    //</editor-fold>

    //<editor-fold desc="Sign-Up With Email">
    public UserProfileDTO signUpWithEmail(
            String authorization,
            UserFormDTO userFormDTO)
    {
        UserProfileDTO created;
        if (userFormDTO.profilePicture == null)
        {
            created = userService.signUpWithEmail(
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
        else
        {
            created = userService.signUpWithEmail(
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
                    userFormDTO.profilePicture);
        }
        return createUpdateProfileProcessor().process(created);
    }

    public MiddleCallback<UserProfileDTO> signUpWithEmail(
            String authorization,
            UserFormDTO userFormDTO,
            Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createUpdateProfileProcessor());
        if (userFormDTO.profilePicture == null)
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
                    middleCallback);
        }
        else
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
                    userFormDTO.profilePicture,
                    middleCallback);
        }
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Sign-Up">
    public UserProfileDTO signUp(
            String authorization,
            UserFormDTO userFormDTO)
    {
        return createUpdateProfileProcessor().process(userService.signUp(authorization, userFormDTO));
    }

    public MiddleCallback<UserProfileDTO> signUp(
            String authorization,
            UserFormDTO userFormDTO,
            Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createUpdateProfileProcessor());
        userServiceAsync.signUp(authorization, userFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Update Profile">
    public UserProfileDTO updateProfile(
            UserBaseKey userBaseKey,
            UserFormDTO userFormDTO)
    {
        UserProfileDTO updated;
        if (userFormDTO.profilePicture == null)
        {
            updated = userService.updateProfile(
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
                    userFormDTO.website);
        }
        else
        {
            updated = userService.updateProfile(
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
                    userFormDTO.profilePicture);
        }
        return createUpdateProfileProcessor().process(updated);
    }

    public MiddleCallback<UserProfileDTO> updateProfile(
            UserBaseKey userBaseKey,
            UserFormDTO userFormDTO,
            Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createUpdateProfileProcessor());
        if (userFormDTO.profilePicture == null)
        {
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
                    middleCallback);
        }
        else
        {
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
                    userFormDTO.profilePicture,
                    middleCallback);
        }
        return middleCallback;
    }

    public UserProfileDTO updateProfilePropertyEmailNotifications(
            UserBaseKey userBaseKey,
            Boolean emailNotificationsEnabled)
    {
        UserFormDTO userFormDTO = new UserFormDTO();
        userFormDTO.emailNotificationsEnabled = emailNotificationsEnabled;
        return this.updateProfile(userBaseKey, userFormDTO);
    }

    public MiddleCallback<UserProfileDTO> updateProfilePropertyEmailNotifications(
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
    {
        UserFormDTO userFormDTO = new UserFormDTO();
        userFormDTO.pushNotificationsEnabled = pushNotificationsEnabled;
        return this.updateProfile(userBaseKey, userFormDTO);
    }

    public MiddleCallback<UserProfileDTO> updateProfilePropertyPushNotifications(
            UserBaseKey userBaseKey,
            Boolean pushNotificationsEnabled,
            Callback<UserProfileDTO> callback)
    {
        UserFormDTO userFormDTO = new UserFormDTO();
        userFormDTO.pushNotificationsEnabled = pushNotificationsEnabled;
        return this.updateProfile(userBaseKey, userFormDTO, callback);
    }
    //</editor-fold>

    //<editor-fold desc="Check Display Name Available">
    public UserAvailabilityDTO checkDisplayNameAvailable(String username)
    {
        return userService.checkDisplayNameAvailable(username);
    }

    public MiddleCallback<UserAvailabilityDTO> checkDisplayNameAvailable(
            String username,
            Callback<UserAvailabilityDTO> callback)
    {
        MiddleCallback<UserAvailabilityDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userServiceAsync.checkDisplayNameAvailable(username, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Forgot Password">
    public ForgotPasswordDTO forgotPassword(ForgotPasswordFormDTO forgotPasswordFormDTO)
    {
        return userService.forgotPassword(forgotPasswordFormDTO);
    }

    public MiddleCallback<ForgotPasswordDTO> forgotPassword(
            ForgotPasswordFormDTO forgotPasswordFormDTO,
            Callback<ForgotPasswordDTO> callback)
    {
        MiddleCallback<ForgotPasswordDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userServiceAsync.forgotPassword(forgotPasswordFormDTO, middleCallback);
        return middleCallback;
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

    protected List<UserSearchResultDTO> searchUsers(SearchUserListType key)
    {
        if (key.searchString == null)
        {
            return this.userService.searchUsers(null, null, null);
        }
        return this.userService.searchUsers(key.searchString, key.page, key.perPage);
    }

    public MiddleCallback<List<UserSearchResultDTO>> searchUsers(UserListType key, Callback<List<UserSearchResultDTO>> callback)
    {
        if (key instanceof SearchUserListType)
        {
            return searchUsers((SearchUserListType) key, callback);
        }
        throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
    }

    protected MiddleCallback<List<UserSearchResultDTO>> searchUsers(SearchUserListType key, Callback<List<UserSearchResultDTO>> callback)
    {
        MiddleCallback<List<UserSearchResultDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        if (key.searchString == null)
        {
            this.userServiceAsync.searchUsers(null, null, null, middleCallback);
        }
        else
        {
            this.userServiceAsync.searchUsers(key.searchString, key.page, key.perPage, middleCallback);
        }
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Search Allowable Recipients">
    public PaginatedDTO<AllowableRecipientDTO> searchAllowableRecipients(SearchAllowableRecipientListType key)
    {
        if (key == null)
        {
            return userService.searchAllowableRecipients(null, null, null);
        }
         return userService.searchAllowableRecipients(key.searchString, key.page, key.perPage);
    }

    public BaseMiddleCallback<PaginatedDTO<AllowableRecipientDTO>> searchAllowableRecipients(SearchAllowableRecipientListType key, Callback<PaginatedDTO<AllowableRecipientDTO>> callback)
    {
        BaseMiddleCallback<PaginatedDTO<AllowableRecipientDTO>>
                middleCallback = new BaseMiddleCallback<>(callback);
        if (key == null)
        {
            userServiceAsync.searchAllowableRecipients(null, null, null, middleCallback);
        }
        else
        {
            userServiceAsync.searchAllowableRecipients(key.searchString, key.page, key.perPage, middleCallback);
        }
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get User">
    public UserProfileDTO getUser(UserBaseKey userKey)
    {
        return userService.getUser(userKey.key);
    }

    public MiddleCallback<UserProfileDTO> getUser(
            UserBaseKey userKey,
            Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userServiceAsync.getUser(userKey.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get User Transactions History">
    public List<UserTransactionHistoryDTO> getUserTransactions(UserBaseKey userBaseKey)
    {
        return userService.getUserTransactions(userBaseKey.key);
    }

    public MiddleCallback<List<UserTransactionHistoryDTO>> getUserTransactions(UserBaseKey userBaseKey, Callback<List<UserTransactionHistoryDTO>> callback)
    {
        MiddleCallback<List<UserTransactionHistoryDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        userServiceAsync.getUserTransactions(userBaseKey.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Update PayPal Email">
    public UpdatePayPalEmailDTO updatePayPalEmail(UserBaseKey userBaseKey,
            UpdatePayPalEmailFormDTO updatePayPalEmailFormDTO)
    {
        return createUpdatePaypalEmailProcessor(userBaseKey).process(
                userService.updatePayPalEmail(userBaseKey.key, updatePayPalEmailFormDTO));
    }

    public MiddleCallback<UpdatePayPalEmailDTO> updatePayPalEmail(UserBaseKey userBaseKey,
            UpdatePayPalEmailFormDTO updatePayPalEmailFormDTO,
            Callback<UpdatePayPalEmailDTO> callback)
    {
        MiddleCallback<UpdatePayPalEmailDTO>
                middleCallback = new BaseMiddleCallback<>(callback, createUpdatePaypalEmailProcessor(userBaseKey));
        userServiceAsync.updatePayPalEmail(userBaseKey.key, updatePayPalEmailFormDTO,
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Update Alipay account">
    public UpdateAlipayAccountDTO updateAlipayAccount(
            UserBaseKey userBaseKey,
            UpdateAlipayAccountFormDTO updateAlipayAccountFormDTO)
    {
        return createUpdateAlipayAccountProcessor(userBaseKey).process(
                userService.updateAlipayAccount(userBaseKey.key, updateAlipayAccountFormDTO));
    }

    public MiddleCallback<UpdateAlipayAccountDTO> updateAlipayAccount(
            UserBaseKey userBaseKey,
            UpdateAlipayAccountFormDTO updateAlipayAccountFormDTO,
            Callback<UpdateAlipayAccountDTO> callback)
    {
        MiddleCallback<UpdateAlipayAccountDTO>
                middleCallback = new BaseMiddleCallback<>(callback, createUpdateAlipayAccountProcessor(userBaseKey));
        userServiceAsync.updateAlipayAccount(userBaseKey.key, updateAlipayAccountFormDTO,
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Delete User">
    public Response deleteUser(UserBaseKey userKey)
    {
        return createUserDeletedProcessor(userKey).process(userService.deleteUser(userKey.key));
    }

    public MiddleCallback<Response> deleteUser(UserBaseKey userKey, Callback<Response> callback)
    {
        MiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(callback, createUserDeletedProcessor(userKey));
        userServiceAsync.deleteUser(userKey.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Friends">
    public List<UserFriendsDTO> getFriends(UserBaseKey userKey)
    {
        return userService.getFriends(userKey.key);
    }

    public MiddleCallback<List<UserFriendsDTO>> getFriends(UserBaseKey userKey, Callback<List<UserFriendsDTO>> callback)
    {
        MiddleCallback<List<UserFriendsDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        userServiceAsync.getFriends(userKey.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Invite Friends">
    public Response inviteFriends(UserBaseKey userKey, InviteFormDTO inviteFormDTO)
    {
        return userService.inviteFriends(userKey.key, inviteFormDTO);
    }

    public MiddleCallback<Response> inviteFriends(UserBaseKey userKey, InviteFormDTO inviteFormDTO, Callback<Response> callback)
    {
        MiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(callback);
        userServiceAsync.inviteFriends(userKey.key, inviteFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Add Credit">
    public UserProfileDTO addCredit(UserBaseKey userKey, GooglePlayPurchaseDTO purchaseDTO)
    {
        return createUpdateProfileProcessor().process(userService.addCredit(userKey.key, purchaseDTO));
    }

    public MiddleCallback<UserProfileDTO> addCredit(UserBaseKey userKey, GooglePlayPurchaseDTO purchaseDTO, Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createUpdateProfileProcessor());
        userServiceAsync.addCredit(userKey.key, purchaseDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Follow Hero">
    public UserProfileDTO follow(UserBaseKey userBaseKey)
    {
        return createFollowUserProcessor(userBaseKey).process(userService.follow(userBaseKey.key));
    }

    public MiddleCallback<UserProfileDTO> follow(UserBaseKey userBaseKey, Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createFollowUserProcessor(userBaseKey));
        userServiceAsync.follow(userBaseKey.key, middleCallback);
        return middleCallback;
    }

    public UserProfileDTO freeFollow(UserBaseKey userBaseKey)
    {
        return createFollowUserProcessor(userBaseKey).process(userService.freeFollow(userBaseKey.key));
    }

    public MiddleCallback<UserProfileDTO> freeFollow(UserBaseKey userBaseKey, Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createFollowUserProcessor(userBaseKey));
        userService.freeFollow(userBaseKey.key, callback);
        return middleCallback;
    }

    public UserProfileDTO follow(UserBaseKey userBaseKey, GooglePlayPurchaseDTO purchaseDTO)
    {
        return createFollowUserProcessor(userBaseKey).process(userService.follow(userBaseKey.key, purchaseDTO));
    }

    public MiddleCallback<UserProfileDTO> follow(UserBaseKey userBaseKey, GooglePlayPurchaseDTO purchaseDTO, Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createFollowUserProcessor(userBaseKey));
        userServiceAsync.follow(userBaseKey.key, purchaseDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Unfollow Hero">
    public UserProfileDTO unfollow(UserBaseKey userBaseKey)
    {
        return createFollowUserProcessor(userBaseKey).process(userService.unfollow(userBaseKey.key));
    }

    public MiddleCallback<UserProfileDTO> unfollow(UserBaseKey userBaseKey, Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createFollowUserProcessor(userBaseKey));
        userServiceAsync.unfollow(userBaseKey.key, middleCallback);
        return middleCallback;

    }
    //</editor-fold>

    //<editor-fold desc="Get Heroes">
    public HeroDTOList getHeroes(UserBaseKey heroKey)
    {
        return userService.getHeroes(heroKey.key);
    }

    public BaseMiddleCallback<HeroDTOList> getHeroes(UserBaseKey heroKey, Callback<HeroDTOList> callback)
    {
        BaseMiddleCallback<HeroDTOList> middleCallback = new BaseMiddleCallback<>(callback);
        userServiceAsync.getHeroes(heroKey.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
