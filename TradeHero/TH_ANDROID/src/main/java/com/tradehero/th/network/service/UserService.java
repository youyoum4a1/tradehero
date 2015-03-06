package com.tradehero.th.network.service;

import com.tradehero.common.billing.googleplay.GooglePlayPurchaseDTO;
import com.tradehero.th.api.analytics.BatchAnalyticsEventForm;
import com.tradehero.th.api.social.HeroDTOList;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.UserFriendsDTOList;
import com.tradehero.th.api.users.*;
import retrofit.client.Response;
import retrofit.http.*;

public interface UserService
{

    //<editor-fold desc="Check Display Name Available">
    @GET("/checkDisplayNameAvailable") UserAvailabilityDTO checkDisplayNameAvailable(
            @Query("displayName") String username);
    //</editor-fold>

    //<editor-fold desc="Search Users">
    @GET("/users/search") UserSearchResultDTOList searchUsers(
            @Query("q") String searchString,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Search Allowable Recipients">
    @GET("/users/allowableRecipients") PaginatedAllowableRecipientDTO searchAllowableRecipients(
            @Query("searchTerm") String searchString,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get User">
    @GET("/users/{userId}") UserProfileDTO getUser(
            @Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Get User Transactions History">

    @GET("/users/{userId}/transactionHistory")
    UserTransactionHistoryDTOList getUserTransactions(
            @Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Get Friends">
    @GET("/users/{userId}/getFriends") UserFriendsDTOList getFriends(
            @Path("userId") int userId);

    @GET("/users/{userId}/getweibofriends") UserFriendsDTOList getSocialWeiboFriends(@Path("userId") int userId);

    @GET("/users/{userId}/GetNewFriends") UserFriendsDTOList getSocialFriends(
            @Path("userId") int userId,
            @Query("socialNetwork") SocialNetworkEnum socialNetwork);

    @GET("/users/{userId}/SearchFriends") UserFriendsDTOList searchSocialFriends(
            @Path("userId") int userId,
            @Query("socialNetwork") SocialNetworkEnum socialNetwork,
            @Query("q") String query);
    //</editor-fold>

    //<editor-fold desc="Invite Friends">
    @POST("/users/{userId}/inviteFriends") Response inviteFriends(
            @Path("userId") int userId,
            @Body InviteFormDTO inviteFormDTO);
    //</editor-fold>

    //<editor-fold desc="Add Follow Credit">
    @POST("/users/{userId}/addCredit") UserProfileDTO addCredit(
            @Path("userId") int userId,
            @Body GooglePlayPurchaseDTO purchaseDTO);
    //</editor-fold>

    //<editor-fold desc="Follow Hero">
    @POST("/users/{userId}/follow") UserProfileDTO follow(
            @Path("userId") int userId);

    @POST("/users/{userId}/follow") UserProfileDTO follow(
            @Path("userId") int userId,
            @Body GooglePlayPurchaseDTO purchaseDTO);
    //</editor-fold>

    //<editor-fold desc="Get Heroes">
    @GET("/users/{userId}/heroes") HeroDTOList getHeroes(
            @Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Send Analytics">
    @POST("/analytics")
    Response sendAnalytics(
            @Body BatchAnalyticsEventForm batchAnalyticsEventForm);
    //</editor-fold>
}
