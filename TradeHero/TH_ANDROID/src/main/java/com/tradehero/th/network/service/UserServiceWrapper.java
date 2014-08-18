package com.tradehero.th.network.service;

import com.tradehero.common.billing.googleplay.GooglePlayPurchaseDTO;
import com.tradehero.th.api.analytics.BatchAnalyticsEventForm;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.social.HeroDTOList;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.UserFriendsDTOList;
import com.tradehero.th.api.social.key.FriendsListKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.PaginatedAllowableRecipientDTO;
import com.tradehero.th.api.users.SearchAllowableRecipientListType;
import com.tradehero.th.api.users.SearchUserListType;
import com.tradehero.th.api.users.UpdateCountryCodeDTO;
import com.tradehero.th.api.users.UpdateCountryCodeFormDTO;
import com.tradehero.th.api.users.UpdateReferralCodeDTO;
import com.tradehero.th.api.users.UserAvailabilityDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserListType;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserSearchResultDTOList;
import com.tradehero.th.api.users.UserTransactionHistoryDTOList;
import com.tradehero.th.api.users.password.ForgotPasswordDTO;
import com.tradehero.th.api.users.password.ForgotPasswordFormDTO;
import com.tradehero.th.api.users.payment.UpdateAlipayAccountDTO;
import com.tradehero.th.api.users.payment.UpdateAlipayAccountFormDTO;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailDTO;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailFormDTO;
import com.tradehero.th.fragments.social.friend.BatchFollowFormDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.social.DTOProcessorFriendInvited;
import com.tradehero.th.models.user.DTOProcessorFollowFreeUser;
import com.tradehero.th.models.user.DTOProcessorFollowPremiumUser;
import com.tradehero.th.models.user.DTOProcessorSignInUpUserProfile;
import com.tradehero.th.models.user.DTOProcessorUnfollowUser;
import com.tradehero.th.models.user.DTOProcessorUpdateCountryCode;
import com.tradehero.th.models.user.DTOProcessorUpdateReferralCode;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import com.tradehero.th.models.user.DTOProcessorUserDeleted;
import com.tradehero.th.models.user.payment.DTOProcessorUpdateAlipayAccount;
import com.tradehero.th.models.user.payment.DTOProcessorUpdatePayPalEmail;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.DTOCacheUtil;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.persistence.competition.ProviderCompactCache;
import com.tradehero.th.persistence.competition.ProviderListCache;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardFriendsCache;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.AllowableRecipientPaginatedCache;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.client.Response;

@Singleton public class UserServiceWrapper
{
    @NotNull private final UserService userService;
    @NotNull private final UserServiceAsync userServiceAsync;
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final DTOCacheUtil dtoCacheUtil;
    @NotNull private final UserProfileCache userProfileCache;
    @NotNull private final UserMessagingRelationshipCache userMessagingRelationshipCache;
    @NotNull private final Lazy<HeroListCache> heroListCache;
    @NotNull private final GetPositionsCache getPositionsCache;
    @NotNull private final Lazy<LeaderboardFriendsCache> leaderboardFriendsCache;
    @NotNull private final Lazy<ProviderListCache> providerListCache;
    @NotNull private final Lazy<ProviderCache> providerCache;
    @NotNull private final Lazy<ProviderCompactCache> providerCompactCache;
    @NotNull private final Lazy<AllowableRecipientPaginatedCache> allowableRecipientPaginatedCache;

    //<editor-fold desc="Constructors">
    @Inject public UserServiceWrapper(
            @NotNull UserService userService,
            @NotNull UserServiceAsync userServiceAsync,
            @NotNull CurrentUserId currentUserId,
            @NotNull DTOCacheUtil dtoCacheUtil,
            @NotNull UserProfileCache userProfileCache,
            @NotNull UserMessagingRelationshipCache userMessagingRelationshipCache,
            @NotNull Lazy<HeroListCache> heroListCache,
            @NotNull GetPositionsCache getPositionsCache,
            @NotNull Lazy<LeaderboardFriendsCache> leaderboardFriendsCache,
            @NotNull Lazy<ProviderListCache> providerListCache,
            @NotNull Lazy<ProviderCache> providerCache,
            @NotNull Lazy<ProviderCompactCache> providerCompactCache,
            @NotNull Lazy<AllowableRecipientPaginatedCache> allowableRecipientPaginatedCache)
    {
        this.userService = userService;
        this.userServiceAsync = userServiceAsync;
        this.currentUserId = currentUserId;
        this.dtoCacheUtil = dtoCacheUtil;
        this.userProfileCache = userProfileCache;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
        this.heroListCache = heroListCache;
        this.getPositionsCache = getPositionsCache;
        this.leaderboardFriendsCache = leaderboardFriendsCache;
        this.providerListCache = providerListCache;
        this.providerCache = providerCache;
        this.providerCompactCache = providerCompactCache;
        this.allowableRecipientPaginatedCache = allowableRecipientPaginatedCache;
    }
    //</editor-fold>

    //<editor-fold desc="Sign-Up With Email">
    @NotNull protected DTOProcessor<UserProfileDTO> createSignInUpProfileProcessor()
    {
        return new DTOProcessorSignInUpUserProfile(
                userProfileCache,
                currentUserId,
                dtoCacheUtil);
    }

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
        return createSignInUpProfileProcessor().process(created);
    }

    public MiddleCallback<UserProfileDTO> signUpWithEmail(
            String authorization,
            UserFormDTO userFormDTO,
            Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createSignInUpProfileProcessor());
        if (userFormDTO.profilePicture == null)
        {
            userServiceAsync.signUpWithEmail(
                    authorization,
                    userFormDTO.biography,
                    userFormDTO.deviceToken,
                    userFormDTO.displayName,
                    userFormDTO.inviteCode,
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
                    userFormDTO.inviteCode,
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
        return createSignInUpProfileProcessor().process(userService.signUp(authorization, userFormDTO));
    }

    public MiddleCallback<UserProfileDTO> signUp(
            String authorization,
            UserFormDTO userFormDTO,
            Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createSignInUpProfileProcessor());
        userServiceAsync.signUp(authorization, userFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Update Profile">
    @NotNull protected DTOProcessor<UserProfileDTO> createUpdateProfileProcessor()
    {
        return new DTOProcessorUpdateUserProfile(userProfileCache);
    }

    public UserProfileDTO updateProfile(
            @NotNull UserBaseKey userBaseKey,
            @NotNull UserFormDTO userFormDTO)
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
            @NotNull UserBaseKey userBaseKey,
            @NotNull UserFormDTO userFormDTO,
            @Nullable Callback<UserProfileDTO> callback)
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
            @NotNull UserBaseKey userBaseKey,
            @NotNull Boolean emailNotificationsEnabled)
    {
        UserFormDTO userFormDTO = new UserFormDTO();
        userFormDTO.emailNotificationsEnabled = emailNotificationsEnabled;
        return this.updateProfile(userBaseKey, userFormDTO);
    }

    @NotNull public MiddleCallback<UserProfileDTO> updateProfilePropertyEmailNotifications(
            @NotNull UserBaseKey userBaseKey,
            @NotNull Boolean emailNotificationsEnabled,
            @Nullable Callback<UserProfileDTO> callback)
    {
        UserFormDTO userFormDTO = new UserFormDTO();
        userFormDTO.emailNotificationsEnabled = emailNotificationsEnabled;
        return this.updateProfile(userBaseKey, userFormDTO, callback);
    }

    public UserProfileDTO updateProfilePropertyPushNotifications(
            @NotNull UserBaseKey userBaseKey,
            @NotNull Boolean pushNotificationsEnabled)
    {
        UserFormDTO userFormDTO = new UserFormDTO();
        userFormDTO.pushNotificationsEnabled = pushNotificationsEnabled;
        return this.updateProfile(userBaseKey, userFormDTO);
    }

    @NotNull public MiddleCallback<UserProfileDTO> updateProfilePropertyPushNotifications(
            @NotNull UserBaseKey userBaseKey,
            @NotNull Boolean pushNotificationsEnabled,
            @Nullable Callback<UserProfileDTO> callback)
    {
        UserFormDTO userFormDTO = new UserFormDTO();
        userFormDTO.pushNotificationsEnabled = pushNotificationsEnabled;
        return this.updateProfile(userBaseKey, userFormDTO, callback);
    }
    //</editor-fold>

    //<editor-fold desc="Check Display Name Available">
    public UserAvailabilityDTO checkDisplayNameAvailable(@NotNull String username)
    {
        return userService.checkDisplayNameAvailable(username);
    }

    @NotNull public MiddleCallback<UserAvailabilityDTO> checkDisplayNameAvailable(
            @NotNull String username,
            @Nullable Callback<UserAvailabilityDTO> callback)
    {
        MiddleCallback<UserAvailabilityDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userServiceAsync.checkDisplayNameAvailable(username, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Forgot Password">
    public ForgotPasswordDTO forgotPassword(@NotNull ForgotPasswordFormDTO forgotPasswordFormDTO)
    {
        return userService.forgotPassword(forgotPasswordFormDTO);
    }

    @NotNull public MiddleCallback<ForgotPasswordDTO> forgotPassword(
            @NotNull ForgotPasswordFormDTO forgotPasswordFormDTO,
            @Nullable Callback<ForgotPasswordDTO> callback)
    {
        MiddleCallback<ForgotPasswordDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userServiceAsync.forgotPassword(forgotPasswordFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Search Users">
    public UserSearchResultDTOList searchUsers(@NotNull UserListType key)
    {
        if (key instanceof SearchUserListType)
        {
            return searchUsers((SearchUserListType) key);
        }
        throw new IllegalArgumentException("Unhandled type " + ((Object) key).getClass().getName());
    }

    protected UserSearchResultDTOList searchUsers(@NotNull SearchUserListType key)
    {
        if (key.searchString == null)
        {
            return this.userService.searchUsers(null, null, null);
        }
        return this.userService.searchUsers(key.searchString, key.page, key.perPage);
    }

    @NotNull public MiddleCallback<UserSearchResultDTOList> searchUsers(
            @NotNull UserListType key,
            @Nullable Callback<UserSearchResultDTOList> callback)
    {
        if (key instanceof SearchUserListType)
        {
            return searchUsers((SearchUserListType) key, callback);
        }
        throw new IllegalArgumentException("Unhandled type " + ((Object) key).getClass().getName());
    }

    @NotNull protected MiddleCallback<UserSearchResultDTOList> searchUsers(
            @NotNull SearchUserListType key,
            @Nullable Callback<UserSearchResultDTOList> callback)
    {
        MiddleCallback<UserSearchResultDTOList> middleCallback = new BaseMiddleCallback<>(callback);
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
    public PaginatedAllowableRecipientDTO searchAllowableRecipients(@Nullable SearchAllowableRecipientListType key)
    {
        if (key == null)
        {
            return userService.searchAllowableRecipients(null, null, null);
        }
        return userService.searchAllowableRecipients(key.searchString, key.page, key.perPage);
    }

    @NotNull public BaseMiddleCallback<PaginatedAllowableRecipientDTO> searchAllowableRecipients(
            @Nullable SearchAllowableRecipientListType key,
            @Nullable Callback<PaginatedAllowableRecipientDTO> callback)
    {
        BaseMiddleCallback<PaginatedAllowableRecipientDTO>
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
    public UserProfileDTO getUser(@NotNull UserBaseKey userKey)
    {
        return userService.getUser(userKey.key);
    }

    @NotNull public MiddleCallback<UserProfileDTO> getUser(
            @NotNull UserBaseKey userKey,
            @Nullable Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userServiceAsync.getUser(userKey.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get User Transactions History">
    @NotNull public UserTransactionHistoryDTOList getUserTransactions(
            @NotNull UserBaseKey userBaseKey)
    {
        return userService.getUserTransactions(userBaseKey.key);
    }

    @NotNull public MiddleCallback<UserTransactionHistoryDTOList> getUserTransactions(
            @NotNull UserBaseKey userBaseKey,
            @Nullable Callback<UserTransactionHistoryDTOList> callback)
    {
        MiddleCallback<UserTransactionHistoryDTOList> middleCallback = new BaseMiddleCallback<>(callback);
        userServiceAsync.getUserTransactions(userBaseKey.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Update PayPal Email">
    @NotNull protected DTOProcessor<UpdatePayPalEmailDTO> createUpdatePaypalEmailProcessor(@NotNull UserBaseKey playerId)
    {
        return new DTOProcessorUpdatePayPalEmail(userProfileCache, playerId);
    }

    public UpdatePayPalEmailDTO updatePayPalEmail(
            @NotNull UserBaseKey userBaseKey,
            @NotNull UpdatePayPalEmailFormDTO updatePayPalEmailFormDTO)
    {
        return createUpdatePaypalEmailProcessor(userBaseKey).process(
                userService.updatePayPalEmail(userBaseKey.key, updatePayPalEmailFormDTO));
    }

    @NotNull public MiddleCallback<UpdatePayPalEmailDTO> updatePayPalEmail(
            @NotNull UserBaseKey userBaseKey,
            @NotNull UpdatePayPalEmailFormDTO updatePayPalEmailFormDTO,
            @Nullable Callback<UpdatePayPalEmailDTO> callback)
    {
        MiddleCallback<UpdatePayPalEmailDTO>
                middleCallback = new BaseMiddleCallback<>(callback, createUpdatePaypalEmailProcessor(userBaseKey));
        userServiceAsync.updatePayPalEmail(userBaseKey.key, updatePayPalEmailFormDTO,
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Update Alipay account">
    @NotNull protected DTOProcessor<UpdateAlipayAccountDTO> createUpdateAlipayAccountProcessor(@NotNull UserBaseKey playerId)
    {
        return new DTOProcessorUpdateAlipayAccount(userProfileCache, playerId);
    }

    public UpdateAlipayAccountDTO updateAlipayAccount(
            @NotNull UserBaseKey userBaseKey,
            @NotNull UpdateAlipayAccountFormDTO updateAlipayAccountFormDTO)
    {
        return createUpdateAlipayAccountProcessor(userBaseKey).process(
                userService.updateAlipayAccount(userBaseKey.key, updateAlipayAccountFormDTO));
    }

    @NotNull public MiddleCallback<UpdateAlipayAccountDTO> updateAlipayAccount(
            @NotNull UserBaseKey userBaseKey,
            @NotNull UpdateAlipayAccountFormDTO updateAlipayAccountFormDTO,
            @Nullable Callback<UpdateAlipayAccountDTO> callback)
    {
        MiddleCallback<UpdateAlipayAccountDTO>
                middleCallback = new BaseMiddleCallback<>(callback, createUpdateAlipayAccountProcessor(userBaseKey));
        userServiceAsync.updateAlipayAccount(userBaseKey.key, updateAlipayAccountFormDTO,
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Delete User">
    @NotNull protected DTOProcessor<Response> createUserDeletedProcessor(@NotNull UserBaseKey playerId)
    {
        return new DTOProcessorUserDeleted(userProfileCache, playerId);
    }

    public Response deleteUser(@NotNull UserBaseKey userKey)
    {
        return createUserDeletedProcessor(userKey).process(userService.deleteUser(userKey.key));
    }

    @NotNull public MiddleCallback<Response> deleteUser(
            @NotNull UserBaseKey userKey,
            @Nullable Callback<Response> callback)
    {
        MiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(callback, createUserDeletedProcessor(userKey));
        userServiceAsync.deleteUser(userKey.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Social Friends">
    public UserFriendsDTOList getFriends(@NotNull FriendsListKey friendsListKey)
    {
        UserFriendsDTOList received;
        if (friendsListKey.searchQuery != null)
        {
            received = userService.searchSocialFriends(
                    friendsListKey.userBaseKey.key,
                    friendsListKey.socialNetworkEnum,
                    friendsListKey.searchQuery);
        }
        else if (friendsListKey.socialNetworkEnum != null)
        {
            if (friendsListKey.socialNetworkEnum == SocialNetworkEnum.WB)
            {
                received = userService.getSocialWeiboFriends(friendsListKey.userBaseKey.key);
            }
            else
            {
                received = userService.getSocialFriends(
                        friendsListKey.userBaseKey.key,
                        friendsListKey.socialNetworkEnum);
            }
        }
        else
        {
            received = userService.getFriends(
                    friendsListKey.userBaseKey.key);
        }
        return received;
    }

    @NotNull public MiddleCallback<UserFriendsDTOList> getFriends(
            @NotNull FriendsListKey friendsListKey,
            @Nullable Callback<UserFriendsDTOList> callback)
    {
        MiddleCallback<UserFriendsDTOList> middleCallback = new BaseMiddleCallback<>(callback);
        if (friendsListKey.searchQuery != null)
        {
            userServiceAsync.searchSocialFriends(
                    friendsListKey.userBaseKey.key,
                    friendsListKey.socialNetworkEnum,
                    friendsListKey.searchQuery,
                    middleCallback);
        }
        else if (friendsListKey.socialNetworkEnum != null)
        {
            userServiceAsync.getSocialFriends(
                    friendsListKey.userBaseKey.key,
                    friendsListKey.socialNetworkEnum,
                    middleCallback);
        }
        else
        {
            userServiceAsync.getFriends(
                    friendsListKey.userBaseKey.key,
                    middleCallback);
        }
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Search Social Friends">
    @NotNull public MiddleCallback<UserFriendsDTOList> searchSocialFriends(
            @NotNull UserBaseKey userKey,
            @Nullable SocialNetworkEnum socialNetworkEnum,
            @NotNull String query,
            @Nullable Callback<UserFriendsDTOList> callback)
    {
        MiddleCallback<UserFriendsDTOList> middleCallback = new BaseMiddleCallback<>(callback);
        userServiceAsync.searchSocialFriends(userKey.key, socialNetworkEnum, query, middleCallback);
        return middleCallback;
    }

    public UserFriendsDTOList searchSocialFriends(@NotNull UserBaseKey userKey, @NotNull SocialNetworkEnum socialNetworkEnum, @NotNull String query)
    {
        return userService.searchSocialFriends(userKey.key, socialNetworkEnum, query);
    }
    //</editor-fold>

    //<editor-fold desc="Follow Batch Free">
    public Response followBatchFree(@NotNull BatchFollowFormDTO batchFollowFormDTO)
    {
        return userService.followBatchFree(batchFollowFormDTO);
    }

    @NotNull public MiddleCallback<UserProfileDTO> followBatchFree(
            @NotNull BatchFollowFormDTO batchFollowFormDTO,
            @Nullable Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userServiceAsync.followBatchFree(batchFollowFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Invite Friends">
    @NotNull protected DTOProcessor<Response> createDTOProcessorFriendInvited()
    {
        return new DTOProcessorFriendInvited(this.leaderboardFriendsCache.get());
    }

    public Response inviteFriends(
            @NotNull UserBaseKey userKey,
            @NotNull InviteFormDTO inviteFormDTO)
    {
        return createDTOProcessorFriendInvited().process(userService.inviteFriends(userKey.key, inviteFormDTO));
    }

    @NotNull public MiddleCallback<Response> inviteFriends(
            @NotNull UserBaseKey userKey,
            @NotNull InviteFormDTO inviteFormDTO,
            @Nullable Callback<Response> callback)
    {
        MiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(callback, createDTOProcessorFriendInvited());
        userServiceAsync.inviteFriends(userKey.key, inviteFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Add Credit">
    public UserProfileDTO addCredit(
            @NotNull UserBaseKey userKey,
            @Nullable GooglePlayPurchaseDTO purchaseDTO)
    {
        return createUpdateProfileProcessor().process(userService.addCredit(userKey.key, purchaseDTO));
    }

    @NotNull public MiddleCallback<UserProfileDTO> addCredit(
            @NotNull UserBaseKey userKey,
            @Nullable GooglePlayPurchaseDTO purchaseDTO,
            @Nullable Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createUpdateProfileProcessor());
        userServiceAsync.addCredit(userKey.key, purchaseDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Follow Hero">
    @NotNull protected DTOProcessor<UserProfileDTO> createFollowPremiumUserProcessor(@NotNull UserBaseKey userToFollow)
    {
        return new DTOProcessorFollowPremiumUser(
                userProfileCache,
                heroListCache.get(),
                getPositionsCache,
                userMessagingRelationshipCache,
                allowableRecipientPaginatedCache.get(),
                userToFollow);
    }

    public UserProfileDTO follow(@NotNull UserBaseKey userBaseKey)
    {
        return createFollowPremiumUserProcessor(userBaseKey).process(userService.follow(userBaseKey.key));
    }

    @NotNull public MiddleCallback<UserProfileDTO> follow(
            @NotNull UserBaseKey userBaseKey,
            @Nullable Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createFollowPremiumUserProcessor(userBaseKey));
        userServiceAsync.follow(userBaseKey.key, middleCallback);
        return middleCallback;
    }

    public UserProfileDTO follow(
            @NotNull UserBaseKey userBaseKey,
            @NotNull GooglePlayPurchaseDTO purchaseDTO)
    {
        return createFollowPremiumUserProcessor(userBaseKey).process(userService.follow(userBaseKey.key, purchaseDTO));
    }

    @NotNull public MiddleCallback<UserProfileDTO> follow(
            @NotNull UserBaseKey userBaseKey,
            @NotNull GooglePlayPurchaseDTO purchaseDTO, @Nullable Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createFollowPremiumUserProcessor(userBaseKey));
        userServiceAsync.follow(userBaseKey.key, purchaseDTO, middleCallback);
        return middleCallback;
    }

    @NotNull protected DTOProcessor<UserProfileDTO> createFollowFreeUserProcessor(@NotNull UserBaseKey userToFollow)
    {
        return new DTOProcessorFollowFreeUser(
                userProfileCache,
                heroListCache.get(),
                getPositionsCache,
                userMessagingRelationshipCache,
                allowableRecipientPaginatedCache.get(),
                userToFollow);
    }

    public UserProfileDTO freeFollow(@NotNull UserBaseKey userBaseKey)
    {
        return createFollowFreeUserProcessor(userBaseKey).process(userService.freeFollow(userBaseKey.key));
    }

    @NotNull public MiddleCallback<UserProfileDTO> freeFollow(
            @NotNull UserBaseKey userBaseKey,
            @Nullable Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createFollowFreeUserProcessor(userBaseKey));
        userServiceAsync.freeFollow(userBaseKey.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Unfollow Hero">
    @NotNull protected DTOProcessor<UserProfileDTO> createUnfollowUserProcessor(@NotNull UserBaseKey userToFollow)
    {
        return new DTOProcessorUnfollowUser(
                userProfileCache,
                heroListCache.get(),
                getPositionsCache,
                userMessagingRelationshipCache,
                allowableRecipientPaginatedCache.get(),
                userToFollow);
    }

    public UserProfileDTO unfollow(@NotNull UserBaseKey userBaseKey)
    {
        return createUnfollowUserProcessor(userBaseKey).process(userService.unfollow(userBaseKey.key));
    }

    @NotNull public MiddleCallback<UserProfileDTO> unfollow(
            @NotNull UserBaseKey userBaseKey,
            @Nullable Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createUnfollowUserProcessor(userBaseKey));
        userServiceAsync.unfollow(userBaseKey.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Heroes">
    public HeroDTOList getHeroes(@NotNull UserBaseKey heroKey)
    {
        return userService.getHeroes(heroKey.key);
    }

    @NotNull public MiddleCallback<HeroDTOList> getHeroes(
            @NotNull UserBaseKey heroKey,
            @Nullable Callback<HeroDTOList> callback)
    {
        BaseMiddleCallback<HeroDTOList> middleCallback = new BaseMiddleCallback<>(callback);
        userServiceAsync.getHeroes(heroKey.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Update Country Code">
    @NotNull protected DTOProcessor<UpdateCountryCodeDTO> createUpdateCountryCodeProcessor(
            @NotNull UserBaseKey playerId,
            @NotNull UpdateCountryCodeFormDTO updateCountryCodeFormDTO)
    {
        return new DTOProcessorUpdateCountryCode(
                userProfileCache,
                providerListCache.get(),
                providerCache.get(),
                providerCompactCache.get(),
                playerId,
                updateCountryCodeFormDTO);
    }

    @NotNull public UpdateCountryCodeDTO updateCountryCode(
            @NotNull UserBaseKey userKey,
            @NotNull UpdateCountryCodeFormDTO updateCountryCodeFormDTO)
    {
        return createUpdateCountryCodeProcessor(userKey, updateCountryCodeFormDTO).process(
                userService.updateCountryCode(userKey.key, updateCountryCodeFormDTO));
    }

    @NotNull public MiddleCallback<UpdateCountryCodeDTO> updateCountryCode(
            @NotNull UserBaseKey userKey,
            @NotNull UpdateCountryCodeFormDTO updateCountryCodeFormDTO,
            @Nullable Callback<UpdateCountryCodeDTO> callback)
    {
        MiddleCallback<UpdateCountryCodeDTO> middleCallback = new BaseMiddleCallback<>(callback,
                createUpdateCountryCodeProcessor(userKey, updateCountryCodeFormDTO));
        userServiceAsync.updateCountryCode(userKey.key, updateCountryCodeFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Update Referral Code">
    @NotNull protected DTOProcessor<Response> createUpdateReferralCodeProcessor(
            @NotNull UpdateReferralCodeDTO updateReferralCodeDTO,
            @NotNull UserBaseKey invitedUserId)
    {
        return new DTOProcessorUpdateReferralCode(userProfileCache, updateReferralCodeDTO, invitedUserId);
    }

    @NotNull public Response updateReferralCode(
            @NotNull UserBaseKey invitedUserId,
            @NotNull UpdateReferralCodeDTO updateReferralCodeDTO)
    {
        return createUpdateReferralCodeProcessor(updateReferralCodeDTO, invitedUserId).process(
                userService.updateReferralCode(invitedUserId.key, updateReferralCodeDTO));
    }

    @NotNull public MiddleCallback<Response> updateReferralCode(
            @NotNull UserBaseKey invitedUserId,
            @NotNull UpdateReferralCodeDTO updateReferralCodeDTO,
            @Nullable Callback<Response> callback)
    {
        MiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(
                callback,
                createUpdateReferralCodeProcessor(updateReferralCodeDTO, invitedUserId));
        userServiceAsync.updateReferralCode(invitedUserId.key, updateReferralCodeDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Send Analytics">
    @NotNull public void sendAnalytics(@NotNull BatchAnalyticsEventForm batchAnalyticsEventForm)
    {
        userService.sendAnalytics(batchAnalyticsEventForm);
    }
    //</editor-fold>
}
