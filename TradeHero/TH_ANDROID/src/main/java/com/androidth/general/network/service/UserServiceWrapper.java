package com.androidth.general.network.service;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.prefs.BooleanPreference;
import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.analytics.BatchAnalyticsEventForm;
import com.androidth.general.api.billing.PurchaseReportDTO;
import com.androidth.general.api.form.UserFormDTO;
import com.androidth.general.api.leaderboard.LeaderboardUserDTOList;
import com.androidth.general.api.portfolio.PortfolioCompactDTOList;
import com.androidth.general.api.portfolio.PortfolioDTO;
import com.androidth.general.api.social.BatchFollowFormDTO;
import com.androidth.general.api.social.HeroDTOList;
import com.androidth.general.api.social.InviteFormDTO;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.api.social.UserFriendsDTOList;
import com.androidth.general.api.social.key.FriendsListKey;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.PaginatedAllowableRecipientDTO;
import com.androidth.general.api.users.SearchAllowableRecipientListType;
import com.androidth.general.api.users.SearchUserListType;
import com.androidth.general.api.users.SuggestHeroesListType;
import com.androidth.general.api.users.SuggestHeroesListTypeNew;
import com.androidth.general.api.users.UpdateCountryCodeDTO;
import com.androidth.general.api.users.UpdateCountryCodeFormDTO;
import com.androidth.general.api.users.UpdateReferralCodeDTO;
import com.androidth.general.api.users.UserAvailabilityDTO;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserListType;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.api.users.UserSearchResultDTOList;
import com.androidth.general.api.users.UserTransactionHistoryDTOList;
import com.androidth.general.api.users.password.ForgotPasswordDTO;
import com.androidth.general.api.users.password.ForgotPasswordFormDTO;
import com.androidth.general.api.users.payment.UpdateAlipayAccountDTO;
import com.androidth.general.api.users.payment.UpdateAlipayAccountFormDTO;
import com.androidth.general.api.users.payment.UpdatePayPalEmailDTO;
import com.androidth.general.api.users.payment.UpdatePayPalEmailFormDTO;
import com.androidth.general.auth.AuthData;
import com.androidth.general.models.user.DTOProcessorFollowFreeUser;
import com.androidth.general.models.user.DTOProcessorFollowFreeUserBatch;
import com.androidth.general.models.user.DTOProcessorSignInUpUserProfile;
import com.androidth.general.models.user.DTOProcessorUpdateCountryCode;
import com.androidth.general.models.user.DTOProcessorUpdateReferralCode;
import com.androidth.general.models.user.DTOProcessorUpdateUserProfile;
import com.androidth.general.models.user.payment.DTOProcessorUpdateAlipayAccount;
import com.androidth.general.models.user.payment.DTOProcessorUpdatePayPalEmail;
import com.androidth.general.persistence.competition.ProviderListCacheRx;
import com.androidth.general.persistence.portfolio.PortfolioCompactListCacheRx;
import com.androidth.general.persistence.prefs.IsOnBoardShown;
import com.androidth.general.persistence.social.HeroListCacheRx;
import com.androidth.general.persistence.user.UserMessagingRelationshipCacheRx;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import retrofit.client.Response;
import rx.Observable;
import rx.functions.Func1;

@Singleton public class UserServiceWrapper
{
    @NonNull private final Context context;
    @NonNull private final UserServiceRx userServiceRx;
    @NonNull private final Provider<UserFormDTO.Builder2> userFormBuilderProvider;
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final DTOCacheUtilRx dtoCacheUtil;
    @NonNull private final Lazy<UserProfileCacheRx> userProfileCache;
    @NonNull private final Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache;
    @NonNull private final Lazy<UserMessagingRelationshipCacheRx> userMessagingRelationshipCache;
    @NonNull private final Lazy<HeroListCacheRx> heroListCache;
    @NonNull private final Lazy<ProviderListCacheRx> providerListCache;
    @NonNull private final BooleanPreference isOnBoardShown;

    //<editor-fold desc="Constructors">
    @Inject public UserServiceWrapper(
            @NonNull Context context,
            @NonNull UserServiceRx userServiceRx,
            @NonNull CurrentUserId currentUserId,
            @NonNull DTOCacheUtilRx dtoCacheUtil,
            @NonNull Lazy<UserProfileCacheRx> userProfileCache,
            @NonNull Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache,
            @NonNull Lazy<UserMessagingRelationshipCacheRx> userMessagingRelationshipCache,
            @NonNull Lazy<HeroListCacheRx> heroListCache,
            @NonNull Lazy<ProviderListCacheRx> providerListCache,
            @NonNull Provider<UserFormDTO.Builder2> userFormBuilderProvider,
            @NonNull @IsOnBoardShown BooleanPreference isOnBoardShown)
    {
        this.context = context;
        this.currentUserId = currentUserId;
        this.dtoCacheUtil = dtoCacheUtil;
        this.userProfileCache = userProfileCache;
        this.portfolioCompactListCache = portfolioCompactListCache;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
        this.heroListCache = heroListCache;
        this.providerListCache = providerListCache;
        this.userServiceRx = userServiceRx;
        this.userFormBuilderProvider = userFormBuilderProvider;
        this.isOnBoardShown = isOnBoardShown;
    }
    //</editor-fold>

    //<editor-fold desc="Sign-Up With Email">
    @NonNull public Observable<UserProfileDTO> signUpWithEmailRx(
            AuthData authData,
            UserFormDTO userFormDTO)
    {
        Observable<UserProfileDTO> created;
        if (userFormDTO.profilePicture == null)
        {
            created = userServiceRx.signUpWithEmail(
                    authData.getTHToken(),
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
                    authData.getTHToken(),
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

        return created.map(new DTOProcessorSignInUpUserProfile(
                context,
                userProfileCache.get(),
                currentUserId,
                authData,
                dtoCacheUtil,
                isOnBoardShown));
    }
    //</editor-fold>

    //<editor-fold desc="Update Profile">
    @NonNull public Observable<UserProfileDTO> updateProfileRx(
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

        return created.map(new DTOProcessorUpdateUserProfile(userProfileCache.get()));
    }

    @NonNull public Observable<UserProfileDTO> updateProfilePropertyEmailNotificationsRx(
            @NonNull UserBaseKey userBaseKey,
            @NonNull Boolean emailNotificationsEnabled)
    {
        UserFormDTO userFormDTO = userFormBuilderProvider.get()
                .emailNotificationsEnabled(emailNotificationsEnabled)
                .build();
        return this.updateProfileRx(userBaseKey, userFormDTO);
    }

    @NonNull public Observable<UserProfileDTO> updateProfilePropertyPushNotificationsRx(
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
    @NonNull public Observable<UserAvailabilityDTO> checkDisplayNameAvailableRx(@NonNull String username)
    {
        return userServiceRx.checkDisplayNameAvailable(username);
    }
    //</editor-fold>

    //<editor-fold desc="Forgot Password">
    @NonNull public Observable<ForgotPasswordDTO> forgotPasswordRx(@NonNull ForgotPasswordFormDTO forgotPasswordFormDTO)
    {
        return userServiceRx.forgotPassword(forgotPasswordFormDTO);
    }
    //</editor-fold>

    //<editor-fold desc="Search Users">
    @NonNull public Observable<UserSearchResultDTOList> searchUsersRx(@NonNull UserListType key)
    {
        if (key instanceof SearchUserListType)
        {
            SearchUserListType searchKey = (SearchUserListType) key;
            if (searchKey.searchString == null)
            {
                return this.userServiceRx.searchUsers(null, null, null);
            }
            return this.userServiceRx.searchUsers(searchKey.searchString, searchKey.page, searchKey.perPage);
        }
        throw new IllegalArgumentException("Unhandled type " + ((Object) key).getClass().getName());
    }
    //</editor-fold>

    //<editor-fold desc="Search Allowable Recipients">
    @NonNull public Observable<PaginatedAllowableRecipientDTO> searchAllowableRecipientsRx(@Nullable SearchAllowableRecipientListType key)
    {
        if (key == null)
        {
            return userServiceRx.searchAllowableRecipients(null, null, null);
        }
        return userServiceRx.searchAllowableRecipients(key.searchString, key.page, key.perPage);
    }
    //</editor-fold>

    //<editor-fold desc="Get User">
    @NonNull public Observable<UserProfileDTO> getUserRx(@NonNull UserBaseKey userKey)
    {
        return userServiceRx.getUser(userKey.key);
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
    @NonNull public Observable<UpdatePayPalEmailDTO> updatePayPalEmailRx(
            @NonNull UserBaseKey userBaseKey,
            @NonNull UpdatePayPalEmailFormDTO updatePayPalEmailFormDTO)
    {
        return userServiceRx.updatePayPalEmail(userBaseKey.key, updatePayPalEmailFormDTO)
                .map(new DTOProcessorUpdatePayPalEmail(userProfileCache.get(), userBaseKey, updatePayPalEmailFormDTO));
    }
    //</editor-fold>

    //<editor-fold desc="Update Alipay account">
    @NonNull public Observable<UpdateAlipayAccountDTO> updateAlipayAccountRx(
            @NonNull UserBaseKey userBaseKey,
            @NonNull UpdateAlipayAccountFormDTO updateAlipayAccountFormDTO)
    {
        return userServiceRx.updateAlipayAccount(userBaseKey.key, updateAlipayAccountFormDTO)
                .map(new DTOProcessorUpdateAlipayAccount(userProfileCache.get(), userBaseKey, updateAlipayAccountFormDTO));
    }
    //</editor-fold>

    //<editor-fold desc="Get Social Friends">
    @NonNull public Observable<UserFriendsDTOList> getFriendsRx(@NonNull final FriendsListKey friendsListKey)
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
            else if (friendsListKey.socialNetworkEnum == SocialNetworkEnum.FB)
            {
                received = userServiceRx.getSocialFacebookFriends(friendsListKey.userBaseKey.key)
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends UserFriendsDTOList>>()
                {
                    // It may not be deployed yet.
                    @Override public Observable<? extends UserFriendsDTOList> call(Throwable throwable)
                    {
                        return userServiceRx.getSocialFriends(
                                friendsListKey.userBaseKey.key,
                                friendsListKey.socialNetworkEnum);
                    }
                });
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
    @NonNull public Observable<UserFriendsDTOList> searchSocialFriendsRx(@NonNull UserBaseKey userKey, @NonNull SocialNetworkEnum socialNetworkEnum,
            @NonNull String query)
    {
        return userServiceRx.searchSocialFriends(userKey.key, socialNetworkEnum, query);
    }
    //</editor-fold>

    //<editor-fold desc="Follow Batch Free">
    @NonNull public Observable<UserProfileDTO> followBatchFreeRx(@NonNull BatchFollowFormDTO batchFollowFormDTO)
    {
        return userServiceRx.followBatchFree(batchFollowFormDTO)
                .map(new DTOProcessorFollowFreeUserBatch(
                        userProfileCache.get(),
                        userMessagingRelationshipCache.get(),
                        batchFollowFormDTO));
    }
    //</editor-fold>

    //<editor-fold desc="Invite Friends">
    @NonNull public Observable<BaseResponseDTO> inviteFriendsRx(
            @NonNull UserBaseKey userKey,
            @NonNull InviteFormDTO inviteFormDTO)
    {
        return userServiceRx.inviteFriends(userKey.key, inviteFormDTO);
    }
    //</editor-fold>

    //<editor-fold desc="Add Credit">
    @NonNull public Observable<UserProfileDTO> addCreditRx(
            @NonNull UserBaseKey userKey,
            @Nullable PurchaseReportDTO purchaseDTO)
    {
        return userServiceRx.addCredit(userKey.key, purchaseDTO);
    }
    //</editor-fold>

    //<editor-fold desc="Follow Hero">
    @NonNull public Observable<UserProfileDTO> freeFollowRx(@NonNull UserBaseKey heroId)
    {
        return userServiceRx.freeFollow(heroId.key, "")
                .map(new DTOProcessorFollowFreeUser(
                        userProfileCache.get(),
                        heroListCache.get(),
                        userMessagingRelationshipCache.get(),
                        currentUserId.toUserBaseKey(),
                        heroId));
    }
    //</editor-fold>

    //<editor-fold desc="Unfollow Hero">
    @NonNull public Observable<UserProfileDTO> unfollowRx(@NonNull UserBaseKey heroId)
    {
        return userServiceRx.unfollow(heroId.key, "");
    }
    //</editor-fold>

    //<editor-fold desc="Get Heroes">
    @NonNull public Observable<HeroDTOList> getHeroesRx(@NonNull UserBaseKey heroKey)
    {
        return userServiceRx.getHeroes(heroKey.key);
    }
    //</editor-fold>

    //<editor-fold desc="Suggest Heroes">
    @NonNull public Observable<LeaderboardUserDTOList> suggestHeroesRx(@NonNull UserListType userListType)
    {
        if (userListType instanceof SuggestHeroesListType)
        {
            return suggestHeroesRx((SuggestHeroesListType) userListType);
        }
        else if (userListType instanceof SuggestHeroesListTypeNew)
        {
            return suggestHeroesRx((SuggestHeroesListTypeNew) userListType);
        }
        throw new IllegalArgumentException("Unhandled UserListType: " + userListType.getClass().getSimpleName());
    }

    @NonNull protected Observable<LeaderboardUserDTOList> suggestHeroesRx(
            @NonNull SuggestHeroesListType suggestHeroesListType)
    {
        return userServiceRx.suggestHeroes(
                suggestHeroesListType.exchangeId == null ? null : suggestHeroesListType.exchangeId.key,
                suggestHeroesListType.sectorId == null ? null : suggestHeroesListType.sectorId.key,
                suggestHeroesListType.page,
                suggestHeroesListType.perPage);
    }

    @NonNull protected Observable<LeaderboardUserDTOList> suggestHeroesRx(
            @NonNull SuggestHeroesListTypeNew suggestHeroesListType)
    {
        return userServiceRx.suggestHeroes(
                suggestHeroesListType.getCommaSeparatedExchangeIds(),
                suggestHeroesListType.getCommaSeparatedSectorIds(),
                suggestHeroesListType.getPage(),
                suggestHeroesListType.perPage);
    }
    //</editor-fold>

    //<editor-fold desc="Update Country Code">
    @NonNull public Observable<UpdateCountryCodeDTO> updateCountryCodeRx(
            @NonNull UserBaseKey userKey,
            @NonNull UpdateCountryCodeFormDTO updateCountryCodeFormDTO)
    {
        return userServiceRx.updateCountryCode(userKey.key, updateCountryCodeFormDTO)
                .map(new DTOProcessorUpdateCountryCode(
                        userProfileCache.get(),
                        providerListCache.get(),
                        userKey,
                        updateCountryCodeFormDTO));
    }
    //</editor-fold>

    //<editor-fold desc="Update Referral Code">
    @NonNull public Observable<BaseResponseDTO> updateReferralCodeRx(
            @NonNull UserBaseKey invitedUserId,
            @NonNull UpdateReferralCodeDTO updateReferralCodeDTO)
    {
        return userServiceRx.updateReferralCode(invitedUserId.key, updateReferralCodeDTO)
                .map(new DTOProcessorUpdateReferralCode(userProfileCache.get(), updateReferralCodeDTO, invitedUserId));
    }
    //</editor-fold>

    //<editor-fold desc="Send Analytics">
    @NonNull public Observable<Response> sendAnalyticsRx(@NonNull BatchAnalyticsEventForm batchAnalyticsEventForm)
    {
        return userServiceRx.sendAnalytics(batchAnalyticsEventForm);
    }
    //</editor-fold>

    //<editor-fold desc="Create FX Portfolio">
    @NonNull public Observable<PortfolioDTO> createFXPortfolioRx(@NonNull final UserBaseKey userBaseKey)
    {
        return userServiceRx.createFXPortfolioRx(userBaseKey.getUserId(), "")
                .map(new Func1<PortfolioDTO, PortfolioDTO>()
                {
                    @Override public PortfolioDTO call(PortfolioDTO createdFXPortfolioDTO)
                    {
                        UserProfileDTO userProfile = userProfileCache.get().getCachedValue(userBaseKey);
                        if (userProfile != null)
                        {
                            userProfile.fxPortfolio = createdFXPortfolioDTO;
                        }
                        PortfolioCompactDTOList list = portfolioCompactListCache.get().getCachedValue(userBaseKey);
                        if (list != null && list.getDefaultFxPortfolio() == null)
                        {
                            list.add(createdFXPortfolioDTO);
                        }
                        return createdFXPortfolioDTO;
                    }
                });
    }
    //</editor-fold>
}
