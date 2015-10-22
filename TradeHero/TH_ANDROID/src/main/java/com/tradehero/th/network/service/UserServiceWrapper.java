package com.tradehero.th.network.service;

import com.tradehero.chinabuild.data.AppInfoDTO;
import com.tradehero.chinabuild.data.FollowStockForm;
import com.tradehero.chinabuild.data.LoginContinuallyTimesDTO;
import com.tradehero.chinabuild.data.RecommendItems;
import com.tradehero.chinabuild.data.VideoDTOList;
import com.tradehero.chinabuild.fragment.stocklearning.QuestionDTO;
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
import com.tradehero.th.api.users.UpdateReferralCodeDTO;
import com.tradehero.th.api.users.UserAvailabilityDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserListType;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserSearchResultDTOList;
import com.tradehero.th.api.users.UserTransactionHistoryDTOList;
import com.tradehero.th.api.users.password.ForgotPasswordDTO;
import com.tradehero.th.api.users.password.ForgotPasswordFormDTO;
import com.tradehero.th.api.users.password.PhoneNumberBindDTO;
import com.tradehero.th.api.users.password.ResetPasswordDTO;
import com.tradehero.th.api.users.password.ResetPasswordFormDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.fragments.social.friend.FollowFriendsForm;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.social.DTOProcessorFriendInvited;
import com.tradehero.th.models.user.DTOProcessorFollowFreeUser;
import com.tradehero.th.models.user.DTOProcessorFollowPremiumUser;
import com.tradehero.th.models.user.DTOProcessorSignInUpUserProfile;
import com.tradehero.th.models.user.DTOProcessorUnfollowUser;
import com.tradehero.th.models.user.DTOProcessorUpdateReferralCode;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
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
import java.util.List;
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
                    userFormDTO.phoneNumber,
                    userFormDTO.verifyCode,
                    userFormDTO.deviceAccessToken,
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
                    userFormDTO.phoneNumber,
                    userFormDTO.verifyCode,
                    userFormDTO.deviceAccessToken,
                    userFormDTO.profilePicture,
                    middleCallback);
        }
        return middleCallback;
    }
    //</editor-fold>

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

    public MiddleCallback<UserProfileDTO> updatePhoto(
            UserBaseKey userBaseKey,
            UserFormDTO userFormDTO,
            Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createUpdateProfileProcessor());
            userServiceAsync.updatePhoto(
                    userBaseKey.key,
                    userFormDTO.profilePicture,
                    middleCallback);
        return middleCallback;
    }

    public MiddleCallback<UserProfileDTO> updateName(
            UserBaseKey userBaseKey,
            UserFormDTO userFormDTO,
            Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createUpdateProfileProcessor());
        userServiceAsync.updateName(
                userBaseKey.key,
                userFormDTO.displayName,
                middleCallback);
        return middleCallback;
    }

    public MiddleCallback<UserProfileDTO> updateSign(
            UserBaseKey userBaseKey,
            UserFormDTO userFormDTO,
            Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createUpdateProfileProcessor());
        userServiceAsync.updateSign(
                userBaseKey.key,
                userFormDTO.signature,
                middleCallback);
        return middleCallback;
    }

    public MiddleCallback<UserProfileDTO> uploadCollege(
            UserBaseKey userBaseKey,
            UserFormDTO userFormDTO,
            Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createUpdateProfileProcessor());
        userServiceAsync.uploadCollege(
                userBaseKey.key,
                userFormDTO.school,
                middleCallback);
        return middleCallback;
    }

    public MiddleCallback<UserProfileDTO> updateAccount(
            UserBaseKey userBaseKey,
            UserFormDTO userFormDTO,
            Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createUpdateProfileProcessor());
        userServiceAsync.updateAccount(
                userBaseKey.key,
                userFormDTO.email,
                userFormDTO.password,
                userFormDTO.passwordConfirmation,
                middleCallback);
        return middleCallback;
    }

    public MiddleCallback<Response> sendCode(String phoneNumber, Callback<Response> callback)
    {
        MiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(callback);
        userServiceAsync.sendCode(phoneNumber, middleCallback);
        return middleCallback;
    }

    public MiddleCallback<PhoneNumberBindDTO> phoneNumBind(String phoneNumber, String verifyCode, Callback<PhoneNumberBindDTO> callback)
    {
        MiddleCallback<PhoneNumberBindDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userServiceAsync.bindPhoneNumber(phoneNumber, verifyCode, middleCallback);
        return middleCallback;
    }

    //<editor-fold desc="Check Display Name Available">
    public UserAvailabilityDTO checkDisplayNameAvailable(String username)
    {
        return userService.checkDisplayNameAvailable(username);
    }

    //<editor-fold desc="Forgot Password">
    public MiddleCallback<ForgotPasswordDTO> forgotPasswordEmail(
            ForgotPasswordFormDTO forgotPasswordFormDTO,
            Callback<ForgotPasswordDTO> callback)
    {
        MiddleCallback<ForgotPasswordDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userServiceAsync.forgotPasswordEmail(forgotPasswordFormDTO, middleCallback);
        return middleCallback;
    }

    public Callback<ResetPasswordDTO> resetPasswordMobile(ResetPasswordFormDTO resetPasswordFormDTO, Callback<ResetPasswordDTO> callback){
        userServiceAsync.resetPasswordMobile(resetPasswordFormDTO, callback);
        return callback;
    }
    //</editor-fold>

    //<editor-fold desc="Search Users">
    public UserSearchResultDTOList searchUsers(UserListType key)
    {
        if (key instanceof SearchUserListType)
        {
            return searchUsers((SearchUserListType) key);
        }
        throw new IllegalArgumentException("Unhandled type " + ((Object) key).getClass().getName());
    }

    protected UserSearchResultDTOList searchUsers(SearchUserListType key)
    {
        if (key.searchString == null)
        {
            return this.userService.searchUsers(null, null, null);
        }
        return this.userService.searchUsers(key.searchString, key.page, key.perPage);
    }


    //<editor-fold desc="Search Allowable Recipients">
    public PaginatedAllowableRecipientDTO searchAllowableRecipients(SearchAllowableRecipientListType key)
    {
        if (key == null)
        {
            return userService.searchAllowableRecipients(null, null, null);
        }
         return userService.searchAllowableRecipients(key.searchString, key.page, key.perPage);
    }

    //<editor-fold desc="Get User">
    public UserProfileDTO getUser(UserBaseKey userKey)
    {
        return userService.getUser(userKey.key);
    }

    //<editor-fold desc="Get User Transactions History">
    @NotNull public UserTransactionHistoryDTOList getUserTransactions(
            @NotNull UserBaseKey userBaseKey)
    {
        return userService.getUserTransactions(userBaseKey.key);
    }


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
            if(friendsListKey.socialNetworkEnum == SocialNetworkEnum.WB)
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


    //<editor-fold desc="Search Social Friends">
    public MiddleCallback<UserFriendsDTOList> searchSocialFriends(
            @NotNull UserBaseKey userKey,
            @Nullable SocialNetworkEnum socialNetworkEnum,
            @NotNull String query,
            @Nullable Callback<UserFriendsDTOList> callback)
    {
        MiddleCallback<UserFriendsDTOList> middleCallback = new BaseMiddleCallback<>(callback);
        userServiceAsync.searchSocialFriends(userKey.key, socialNetworkEnum, query, middleCallback);
        return middleCallback;
    }

    public MiddleCallback<UserProfileDTO> followBatchFree(FollowFriendsForm followFriendsForm,Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback);
        userServiceAsync.followBatchFree(followFriendsForm,middleCallback);
        return middleCallback;
    }

    public void followStocks(FollowStockForm followStockForm,Callback<List<WatchlistPositionDTO>> callback)
    {
        userServiceAsync.followStock(followStockForm, callback);
    }
    //</editor-fold>

    //<editor-fold desc="Invite Friends">
    protected DTOProcessor<Response> createDTOProcessorFriendInvited()
    {
        return new DTOProcessorFriendInvited(this.leaderboardFriendsCache.get());
    }

    public Response inviteFriends(UserBaseKey userKey, InviteFormDTO inviteFormDTO)
    {
        return createDTOProcessorFriendInvited().process(userService.inviteFriends(userKey.key, inviteFormDTO));
    }

    public MiddleCallback<Response> inviteFriends(UserBaseKey userKey, InviteFormDTO inviteFormDTO, Callback<Response> callback)
    {
        MiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(callback, createDTOProcessorFriendInvited());
        userServiceAsync.inviteFriends(userKey.key, inviteFormDTO, middleCallback);
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

    @NotNull public MiddleCallback<UserProfileDTO> freeFollow(
            @NotNull UserBaseKey userBaseKey,
            @Nullable Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createFollowFreeUserProcessor(userBaseKey));
        userServiceAsync.freeFollow(userBaseKey.key, callback);
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

    public MiddleCallback<UserProfileDTO> unfollow(
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


    //<editor-fold desc="Update Referral Code">
    @NotNull protected DTOProcessor<Response> createUpdateReferralCodeProcessor(
            @NotNull UpdateReferralCodeDTO updateReferralCodeDTO,
            @NotNull UserBaseKey invitedUserId)
    {
        return new DTOProcessorUpdateReferralCode(userProfileCache, updateReferralCodeDTO, invitedUserId);
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

    //Check whether the current application is the newest one.
    public void downloadAppVersionInfo(@Nullable Callback<AppInfoDTO> callback){
        userServiceAsync.downloadAppVersion(callback);
    }

    //Track when user share to the wechat
    public void trackShare(String eventName, @Nullable Callback<Response> callback){
        userServiceAsync.trackShare(eventName, callback);
    }

    //Check whether the current user logs in 3 times continually
    public void isLoginThreeTimesContinually(int userId, @Nullable Callback<LoginContinuallyTimesDTO> callback){
        userServiceAsync.getContinuallyLoginTimes(String.valueOf(userId), callback);
    }

    //Download recommend items
    public void downloadRecommendItems(@NotNull Callback<RecommendItems> callback){
        userServiceAsync.downloadRecommendItems(callback);
    }

    //Download videos
    public void downloadVideoList(@NotNull Callback<VideoDTOList> callback){
        userServiceAsync.downloadVideoList(callback);
    }

    //Download questions
    public void downloadQuestions(long updatedAtTicks, Callback<QuestionDTO> callback){
        userServiceAsync.downloadQuestions(updatedAtTicks, callback);
    }
}
