package com.tradehero.th.network.service;

import com.tradehero.common.billing.googleplay.GooglePlayPurchaseDTO;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.social.HeroDTOList;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.SearchAllowableRecipientListType;
import com.tradehero.th.api.users.SearchUserListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserListType;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.api.users.UserTransactionHistoryDTO;
import com.tradehero.th.api.users.payment.UpdateAlipayAccountDTO;
import com.tradehero.th.api.users.payment.UpdateAlipayAccountFormDTO;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailDTO;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailFormDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.user.DTOProcessorFollowUser;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import com.tradehero.th.models.user.payment.DTOProcessorUpdateAlipayAccount;
import com.tradehero.th.models.user.payment.DTOProcessorUpdatePayPalEmail;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.RetrofitError;

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
    //</editor-fold>

    //<editor-fold desc="Sign-Up With Email">
    public UserProfileDTO signUpWithEmail(
            String authorization,
            UserFormDTO userFormDTO)
            throws RetrofitError
    {
        return createUpdateProfileProcessor().process(userService.signUpWithEmail(
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
                userFormDTO.website));
    }

    public MiddleCallback<UserProfileDTO> signUpWithEmail(
            String authorization,
            UserFormDTO userFormDTO,
            Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createUpdateProfileProcessor());
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
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Update Profile">
    public UserProfileDTO updateProfile(
            UserBaseKey userBaseKey,
            UserFormDTO userFormDTO)
    {
        return createUpdateProfileProcessor().process(userService.updateProfile(
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
                userFormDTO.profilePicture));
    }

    public MiddleCallback<UserProfileDTO> updateProfile(
            UserBaseKey userBaseKey,
            UserFormDTO userFormDTO,
            Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createUpdateProfileProcessor());
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
            throws RetrofitError
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

    public BaseMiddleCallback<PaginatedDTO<AllowableRecipientDTO>> searchAllowableRecipients(SearchAllowableRecipientListType key, Callback<PaginatedDTO<AllowableRecipientDTO>> callback)
    {
        BaseMiddleCallback<PaginatedDTO<AllowableRecipientDTO>>
                middleCallback = new BaseMiddleCallback<>(callback);
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
