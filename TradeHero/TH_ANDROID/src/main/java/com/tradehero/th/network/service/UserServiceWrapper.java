package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.analytics.BatchAnalyticsEventForm;
import com.tradehero.th.api.billing.PurchaseReportDTO;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.social.HeroDTOList;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.UserFriendsDTOList;
import com.tradehero.th.api.social.key.FriendsListKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.PaginatedAllowableRecipientDTO;
import com.tradehero.th.api.users.SearchAllowableRecipientListType;
import com.tradehero.th.api.users.SearchUserListType;
import com.tradehero.th.api.users.SuggestHeroesListType;
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
import com.tradehero.th.models.user.DTOProcessorFollowFreeUser;
import com.tradehero.th.models.user.DTOProcessorFollowFreeUserBatch;
import com.tradehero.th.models.user.DTOProcessorFollowPremiumUser;
import com.tradehero.th.models.user.DTOProcessorSignInUpUserProfile;
import com.tradehero.th.models.user.DTOProcessorUpdateCountryCode;
import com.tradehero.th.models.user.DTOProcessorUpdateReferralCode;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfileDeep;
import com.tradehero.th.models.user.payment.DTOProcessorUpdateAlipayAccount;
import com.tradehero.th.models.user.payment.DTOProcessorUpdatePayPalEmail;
import com.tradehero.th.persistence.DTOCacheUtilImpl;
import com.tradehero.th.persistence.competition.ProviderCacheRx;
import com.tradehero.th.persistence.competition.ProviderListCacheRx;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.position.GetPositionsCacheRx;
import com.tradehero.th.persistence.social.HeroListCacheRx;
import com.tradehero.th.persistence.user.AllowableRecipientPaginatedCacheRx;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

@Singleton public class UserServiceWrapper
{
    @NonNull private final UserService userService;
    @NonNull private final UserServiceRx userServiceRx;
    @NonNull private final Provider<UserFormDTO.Builder2> userFormBuilderProvider;
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final DTOCacheUtilImpl dtoCacheUtil;
    @NonNull private final Lazy<UserProfileCacheRx> userProfileCache;
    @NonNull private final Lazy<UserMessagingRelationshipCacheRx> userMessagingRelationshipCache;
    @NonNull private final Lazy<HeroListCacheRx> heroListCache;
    @NonNull private final Lazy<GetPositionsCacheRx> getPositionsCache;
    @NonNull private final Lazy<ProviderListCacheRx> providerListCache;
    @NonNull private final Lazy<ProviderCacheRx> providerCache;
    @NonNull private final Lazy<AllowableRecipientPaginatedCacheRx> allowableRecipientPaginatedCache;
    @NonNull private final Lazy<HomeContentCacheRx> homeContentCache;
    @NonNull private final Provider<DTOProcessorUpdateUserProfile> dtoProcessorUpdateUserProfileProvider;

    //<editor-fold desc="Constructors">
    @Inject public UserServiceWrapper(
            @NonNull UserService userService,
            @NonNull UserServiceRx userServiceRx,
            @NonNull CurrentUserId currentUserId,
            @NonNull DTOCacheUtilImpl dtoCacheUtil,
            @NonNull Lazy<UserProfileCacheRx> userProfileCache,
            @NonNull Lazy<UserMessagingRelationshipCacheRx> userMessagingRelationshipCache,
            @NonNull Lazy<HeroListCacheRx> heroListCache,
            @NonNull Lazy<GetPositionsCacheRx> getPositionsCache,
            @NonNull Lazy<ProviderListCacheRx> providerListCache,
            @NonNull Lazy<ProviderCacheRx> providerCache,
            @NonNull Lazy<AllowableRecipientPaginatedCacheRx> allowableRecipientPaginatedCache,
            @NonNull Provider<UserFormDTO.Builder2> userFormBuilderProvider,
            @NonNull Lazy<HomeContentCacheRx> homeContentCache,
            @NonNull Provider<DTOProcessorUpdateUserProfile> dtoProcessorUpdateUserProfileProvider)
    {
        this.userService = userService;
        this.currentUserId = currentUserId;
        this.dtoCacheUtil = dtoCacheUtil;
        this.userProfileCache = userProfileCache;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
        this.heroListCache = heroListCache;
        this.getPositionsCache = getPositionsCache;
        this.providerListCache = providerListCache;
        this.providerCache = providerCache;
        this.allowableRecipientPaginatedCache = allowableRecipientPaginatedCache;
        this.userServiceRx = userServiceRx;
        this.userFormBuilderProvider = userFormBuilderProvider;
        this.homeContentCache = homeContentCache;
        this.dtoProcessorUpdateUserProfileProvider = dtoProcessorUpdateUserProfileProvider;
    }
    //</editor-fold>

    //<editor-fold desc="Sign-Up With Email">
    @NonNull protected DTOProcessor<UserProfileDTO> createSignInUpProfileProcessor()
    {
        return new DTOProcessorSignInUpUserProfile(
                userProfileCache.get(),
                homeContentCache.get(),
                currentUserId,
                dtoCacheUtil);
    }

    public Observable<UserProfileDTO> signUpWithEmailRx(
            String authorization,
            UserFormDTO userFormDTO)
    {
        Observable<UserProfileDTO> created;
        if (userFormDTO.profilePicture == null)
        {
            created = userServiceRx.signUpWithEmail(
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
                    userFormDTO.website);
        }
        else
        {
            created = userServiceRx.signUpWithEmail(
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
                    userFormDTO.profilePicture);
        }

        return created.doOnNext(new Action1<UserProfileDTO>()
        {
            @Override public void call(UserProfileDTO userProfileDTO)
            {
                createSignInUpProfileProcessor().process(userProfileDTO);
            }
        });
    }
    //</editor-fold>

    //<editor-fold desc="Sign-Up">
    public Observable<UserProfileDTO> signUpRx(
            String authorization,
            UserFormDTO userFormDTO)
    {
        return userServiceRx.signUp(authorization, userFormDTO);
    }
    //</editor-fold>

    //<editor-fold desc="Update Profile">
    @NonNull protected DTOProcessor<UserProfileDTO> createUpdateProfileProcessor()
    {
        return new DTOProcessorUpdateUserProfileDeep(userProfileCache.get(), homeContentCache.get());
    }

    public Observable<UserProfileDTO> updateProfileRx(
            @NonNull UserBaseKey userBaseKey,
            @NonNull UserFormDTO userFormDTO)
    {
        Observable<UserProfileDTO> created;
        if (userFormDTO.profilePicture == null)
        {
            created = userServiceRx.updateProfile(
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
            created = userServiceRx.updateProfile(
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

        return created.map(new Func1<UserProfileDTO, UserProfileDTO>()
        {
            @Override public UserProfileDTO call(UserProfileDTO userProfileDTO)
            {
                return createUpdateProfileProcessor().process(userProfileDTO);
            }
        });
    }

    public Observable<UserProfileDTO> updateProfilePropertyEmailNotificationsRx(
            @NonNull UserBaseKey userBaseKey,
            @NonNull Boolean emailNotificationsEnabled)
    {
        UserFormDTO userFormDTO = userFormBuilderProvider.get()
                .emailNotificationsEnabled(emailNotificationsEnabled)
                .build();
        return this.updateProfileRx(userBaseKey, userFormDTO);
    }

    public Observable<UserProfileDTO> updateProfilePropertyPushNotificationsRx(
            @NonNull UserBaseKey userBaseKey,
            @NonNull Boolean pushNotificationsEnabled)
    {
        UserFormDTO userFormDTO = userFormBuilderProvider.get()
                .pushNotificationsEnabled(pushNotificationsEnabled)
                .build();
        return this.updateProfileRx(userBaseKey, userFormDTO);
    }
    //</editor-fold>

    //<editor-fold desc="Check Display Name Available">
    public Observable<UserAvailabilityDTO> checkDisplayNameAvailableRx(@NonNull String username)
    {
        return userServiceRx.checkDisplayNameAvailable(username);
    }
    //</editor-fold>

    //<editor-fold desc="Forgot Password">
    public Observable<ForgotPasswordDTO> forgotPasswordRx(@NonNull ForgotPasswordFormDTO forgotPasswordFormDTO)
    {
        return userServiceRx.forgotPassword(forgotPasswordFormDTO);
    }
    //</editor-fold>

    //<editor-fold desc="Search Users">
    public Observable<UserSearchResultDTOList> searchUsersRx(@NonNull UserListType key)
    {
        if (key instanceof SearchUserListType)
        {
            return searchUsersRx((SearchUserListType) key);
        }
        throw new IllegalArgumentException("Unhandled type " + ((Object) key).getClass().getName());
    }

    protected Observable<UserSearchResultDTOList> searchUsersRx(@NonNull SearchUserListType key)
    {
        if (key.searchString == null)
        {
            return this.userServiceRx.searchUsers(null, null, null);
        }
        return this.userServiceRx.searchUsers(key.searchString, key.page, key.perPage);
    }
    //</editor-fold>

    //<editor-fold desc="Search Allowable Recipients">
    public Observable<PaginatedAllowableRecipientDTO> searchAllowableRecipientsRx(@Nullable SearchAllowableRecipientListType key)
    {
        if (key == null)
        {
            return userServiceRx.searchAllowableRecipients(null, null, null);
        }
        return userServiceRx.searchAllowableRecipients(key.searchString, key.page, key.perPage);
    }
    //</editor-fold>

    //<editor-fold desc="Get User">
    public UserProfileDTO getUser(@NonNull UserBaseKey userKey)
    {
        return userService.getUser(userKey.key);
    }

    public Observable<UserProfileDTO> getUserRx(@NonNull UserBaseKey userKey)
    {
        return userService.getUserRx(userKey.key);
    }
    //</editor-fold>

    //<editor-fold desc="Get User Transactions History">
    @NonNull public Observable<UserTransactionHistoryDTOList> getUserTransactionsRx(
            @NonNull UserBaseKey userBaseKey)
    {
        return userServiceRx.getUserTransactions(userBaseKey.key);
    }
    //</editor-fold>

    //<editor-fold desc="Update PayPal Email">
    @NonNull protected DTOProcessorUpdatePayPalEmail createUpdatePaypalEmailProcessor(@NonNull UserBaseKey userBaseKey)
    {
        return new DTOProcessorUpdatePayPalEmail(userProfileCache.get(), userBaseKey);
    }

    public Observable<UpdatePayPalEmailDTO> updatePayPalEmailRx(
            @NonNull UserBaseKey userBaseKey,
            @NonNull UpdatePayPalEmailFormDTO updatePayPalEmailFormDTO)
    {
        return userServiceRx.updatePayPalEmail(userBaseKey.key, updatePayPalEmailFormDTO)
                .doOnNext(createUpdatePaypalEmailProcessor(userBaseKey));
    }
    //</editor-fold>

    //<editor-fold desc="Update Alipay account">
    @NonNull protected DTOProcessorUpdateAlipayAccount createUpdateAlipayAccountProcessor(@NonNull UserBaseKey playerId)
    {
        return new DTOProcessorUpdateAlipayAccount(userProfileCache.get(), playerId);
    }

    public Observable<UpdateAlipayAccountDTO> updateAlipayAccountRx(
            @NonNull UserBaseKey userBaseKey,
            @NonNull UpdateAlipayAccountFormDTO updateAlipayAccountFormDTO)
    {
        return userServiceRx.updateAlipayAccount(userBaseKey.key, updateAlipayAccountFormDTO)
                .doOnNext(createUpdateAlipayAccountProcessor(userBaseKey));
    }
    //</editor-fold>

    //<editor-fold desc="Delete User">
    public Observable<BaseResponseDTO> deleteUserRx(@NonNull UserBaseKey userKey)
    {
        return userServiceRx.deleteUser(userKey.key);
    }
    //</editor-fold>

    //<editor-fold desc="Get Social Friends">
    public UserFriendsDTOList getFriends(@NonNull FriendsListKey friendsListKey)
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

    public Observable<UserFriendsDTOList> getFriendsRx(@NonNull FriendsListKey friendsListKey)
    {
        Observable<UserFriendsDTOList> received;
        if (friendsListKey.searchQuery != null)
        {
            received = userServiceRx.searchSocialFriends(
                    friendsListKey.userBaseKey.key,
                    friendsListKey.socialNetworkEnum,
                    friendsListKey.searchQuery);
        }
        else if (friendsListKey.socialNetworkEnum != null)
        {
            if (friendsListKey.socialNetworkEnum == SocialNetworkEnum.WB)
            {
                received = userServiceRx.getSocialWeiboFriends(friendsListKey.userBaseKey.key);
            }
            else
            {
                received = userServiceRx.getSocialFriends(
                        friendsListKey.userBaseKey.key,
                        friendsListKey.socialNetworkEnum);
            }
        }
        else
        {
            received = userServiceRx.getFriends(
                    friendsListKey.userBaseKey.key);
        }
        return received;
    }
    //</editor-fold>

    //<editor-fold desc="Search Social Friends">
    public Observable<UserFriendsDTOList> searchSocialFriendsRx(@NonNull UserBaseKey userKey, @NonNull SocialNetworkEnum socialNetworkEnum, @NonNull String query)
    {
        return userServiceRx.searchSocialFriends(userKey.key, socialNetworkEnum, query);
    }
    //</editor-fold>

    //<editor-fold desc="Follow Batch Free">
    protected DTOProcessorFollowFreeUserBatch createBatchFollowFreeProcessor(@NonNull BatchFollowFormDTO batchFollowFormDTO)
    {
        return new DTOProcessorFollowFreeUserBatch(
                userProfileCache.get(),
                homeContentCache.get(),
                heroListCache.get(),
                getPositionsCache.get(),
                userMessagingRelationshipCache.get(),
                allowableRecipientPaginatedCache.get(),
                batchFollowFormDTO);
    }

    @NonNull public Observable<UserProfileDTO> followBatchFreeRx(@NonNull BatchFollowFormDTO batchFollowFormDTO)
    {
        return userServiceRx.followBatchFree(batchFollowFormDTO)
                .doOnNext(createBatchFollowFreeProcessor(batchFollowFormDTO));
    }
    //</editor-fold>

    //<editor-fold desc="Invite Friends">
    public Observable<BaseResponseDTO> inviteFriendsRx(
            @NonNull UserBaseKey userKey,
            @NonNull InviteFormDTO inviteFormDTO)
    {
        return userServiceRx.inviteFriends(userKey.key, inviteFormDTO);
    }
    //</editor-fold>

    //<editor-fold desc="Add Credit">
    public Observable<UserProfileDTO> addCreditRx(
            @NonNull UserBaseKey userKey,
            @Nullable PurchaseReportDTO purchaseDTO)
    {
        return userServiceRx.addCredit(userKey.key, purchaseDTO);
    }
    //</editor-fold>

    //<editor-fold desc="Follow Hero">
    @NonNull protected DTOProcessor<UserProfileDTO> createFollowPremiumUserProcessor(@NonNull UserBaseKey heroId)
    {
        return new DTOProcessorFollowPremiumUser(
                userProfileCache.get(),
                homeContentCache.get(),
                heroListCache.get(),
                getPositionsCache.get(),
                userMessagingRelationshipCache.get(),
                allowableRecipientPaginatedCache.get(),
                currentUserId.toUserBaseKey(),
                heroId);
    }

    public Observable<UserProfileDTO> followRx(@NonNull UserBaseKey heroId)
    {
        return userServiceRx.follow(heroId.key);
    }

    public Observable<UserProfileDTO> followRx(
            @NonNull UserBaseKey heroId,
            @NonNull PurchaseReportDTO purchaseDTO)
    {
        return userServiceRx.follow(heroId.key, purchaseDTO);
    }

    @NonNull protected DTOProcessorFollowFreeUser createFollowFreeUserProcessor(@NonNull UserBaseKey heroId)
    {
        return new DTOProcessorFollowFreeUser(
                userProfileCache.get(),
                homeContentCache.get(),
                heroListCache.get(),
                getPositionsCache.get(),
                userMessagingRelationshipCache.get(),
                allowableRecipientPaginatedCache.get(),
                currentUserId.toUserBaseKey(),
                heroId);
    }

    public Observable<UserProfileDTO> freeFollowRx(@NonNull UserBaseKey heroId)
    {
        return userServiceRx.freeFollow(heroId.key).doOnNext(createFollowFreeUserProcessor(heroId));
    }
    //</editor-fold>

    //<editor-fold desc="Unfollow Hero">
    public Observable<UserProfileDTO> unfollowRx(@NonNull UserBaseKey heroId)
    {
        return userServiceRx.unfollow(heroId.key);
    }
    //</editor-fold>

    //<editor-fold desc="Get Heroes">
    public Observable<HeroDTOList> getHeroesRx(@NonNull UserBaseKey heroKey)
    {
        return userServiceRx.getHeroes(heroKey.key);
    }
    //</editor-fold>

    //<editor-fold desc="Suggest Heroes">
    @NonNull public Observable<LeaderboardUserDTOList> suggestHeroesRx(
            @NonNull SuggestHeroesListType suggestHeroesListType)
    {
        return userServiceRx.suggestHeroes(
                suggestHeroesListType.exchangeId == null ? null : suggestHeroesListType.exchangeId.key,
                suggestHeroesListType.sectorId == null ? null : suggestHeroesListType.sectorId.key,
                suggestHeroesListType.page,
                suggestHeroesListType.perPage);
    }
    //</editor-fold>

    //<editor-fold desc="Update Country Code">
    @NonNull protected DTOProcessorUpdateCountryCode createUpdateCountryCodeProcessor(
            @NonNull UserBaseKey playerId,
            @NonNull UpdateCountryCodeFormDTO updateCountryCodeFormDTO)
    {
        return new DTOProcessorUpdateCountryCode(
                userProfileCache.get(),
                providerListCache.get(),
                providerCache.get(),
                playerId,
                updateCountryCodeFormDTO);
    }

    @NonNull public Observable<UpdateCountryCodeDTO> updateCountryCodeRx(
            @NonNull UserBaseKey userKey,
            @NonNull UpdateCountryCodeFormDTO updateCountryCodeFormDTO)
    {
        return userServiceRx.updateCountryCode(userKey.key, updateCountryCodeFormDTO)
                .doOnNext(createUpdateCountryCodeProcessor(userKey, updateCountryCodeFormDTO));
    }
    //</editor-fold>

    //<editor-fold desc="Update Referral Code">
    @NonNull protected DTOProcessorUpdateReferralCode createUpdateReferralCodeProcessor(
            @NonNull UpdateReferralCodeDTO updateReferralCodeDTO,
            @NonNull UserBaseKey invitedUserId)
    {
        return new DTOProcessorUpdateReferralCode(userProfileCache.get(), updateReferralCodeDTO, invitedUserId);
    }

    @NonNull public Observable<BaseResponseDTO> updateReferralCodeRx(
            @NonNull UserBaseKey invitedUserId,
            @NonNull UpdateReferralCodeDTO updateReferralCodeDTO)
    {
        return userServiceRx.updateReferralCode(invitedUserId.key, updateReferralCodeDTO)
                .doOnNext(createUpdateReferralCodeProcessor(updateReferralCodeDTO, invitedUserId));
    }
    //</editor-fold>

    //<editor-fold desc="Send Analytics">
    @NonNull public Observable<BaseResponseDTO> sendAnalyticsRx(@NonNull BatchAnalyticsEventForm batchAnalyticsEventForm)
    {
        return userServiceRx.sendAnalytics(batchAnalyticsEventForm);
    }
    //</editor-fold>
}
