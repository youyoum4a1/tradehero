package com.ayondo.academy.network.service;

import com.ayondo.academy.api.BaseResponseDTO;
import com.ayondo.academy.api.analytics.BatchAnalyticsEventForm;
import com.ayondo.academy.api.billing.PurchaseReportDTO;
import com.ayondo.academy.api.form.UserFormDTO;
import com.ayondo.academy.api.leaderboard.LeaderboardUserDTOList;
import com.ayondo.academy.api.portfolio.PortfolioDTO;
import com.ayondo.academy.api.social.BatchFollowFormDTO;
import com.ayondo.academy.api.social.HeroDTOList;
import com.ayondo.academy.api.social.InviteFormDTO;
import com.ayondo.academy.api.social.SocialNetworkEnum;
import com.ayondo.academy.api.social.UserFriendsDTOList;
import com.ayondo.academy.api.users.PaginatedAllowableRecipientDTO;
import com.ayondo.academy.api.users.UpdateCountryCodeDTO;
import com.ayondo.academy.api.users.UpdateCountryCodeFormDTO;
import com.ayondo.academy.api.users.UpdateReferralCodeDTO;
import com.ayondo.academy.api.users.UserAvailabilityDTO;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.api.users.UserSearchResultDTOList;
import com.ayondo.academy.api.users.UserTransactionHistoryDTOList;
import com.ayondo.academy.api.users.password.ForgotPasswordDTO;
import com.ayondo.academy.api.users.password.ForgotPasswordFormDTO;
import com.ayondo.academy.api.users.payment.UpdateAlipayAccountDTO;
import com.ayondo.academy.api.users.payment.UpdateAlipayAccountFormDTO;
import com.ayondo.academy.api.users.payment.UpdatePayPalEmailDTO;
import com.ayondo.academy.api.users.payment.UpdatePayPalEmailFormDTO;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedOutput;
import rx.Observable;

import static com.ayondo.academy.utils.Constants.AUTHORIZATION;

public interface UserServiceRx
{
    //<editor-fold desc="Sign-Up With Email">
    @FormUrlEncoded @POST("/SignupWithEmail") Observable<UserProfileDTO> signUpWithEmail(
            @Header(AUTHORIZATION) String authorization,
            @Field("biography") String biography,
            @Field("deviceToken") String deviceToken,
            @Field("displayName") String displayName,
            @Field("inviteCode") String inviteCode,
            @Field("email") String email,
            @Field("emailNotificationsEnabled") Boolean emailNotificationsEnabled,
            @Field("firstName") String firstName,
            @Field("lastName") String lastName,
            @Field("location") String location,
            @Field("password") String password,
            @Field("passwordConfirmation") String passwordConfirmation,
            @Field("pushNotificationsEnabled") Boolean pushNotificationsEnabled,
            @Field("username") String username,
            @Field("website") String website);

    @Multipart @POST("/SignupWithEmail") Observable<UserProfileDTO> signUpWithEmail(
            @Header(AUTHORIZATION) String authorization,
            @Part("biography") String biography,
            @Part("deviceToken") String deviceToken,
            @Part("displayName") String displayName,
            @Part("inviteCode") String inviteCode,
            @Part("email") String email,
            @Part("emailNotificationsEnabled") Boolean emailNotificationsEnabled,
            @Part("firstName") String firstName,
            @Part("lastName") String lastName,
            @Part("location") String location,
            @Part("password") String password,
            @Part("passwordConfirmation") String passwordConfirmation,
            @Part("pushNotificationsEnabled") Boolean pushNotificationsEnabled,
            @Part("username") String username,
            @Part("website") String website,
            @Part("profilePicture") TypedOutput profilePicture);
    //</editor-fold>

    //<editor-fold desc="Signup">
    @POST("/users")
    Observable<UserProfileDTO> signUp(
            @Header(AUTHORIZATION) String authorization,
            @Body UserFormDTO user);
    //</editor-fold>

    //<editor-fold desc="Update Profile">
    @FormUrlEncoded @PUT("/users/{userId}/updateUser")
    Observable<UserProfileDTO> updateProfile(
            @Path("userId") int userId,
            @Field("deviceToken") String deviceToken,
            @Field("displayName") String displayName,
            @Field("email") String email,
            @Field("firstName") String firstName,
            @Field("lastName") String lastName,
            @Field("password") String password,
            @Field("passwordConfirmation") String passwordConfirmation,
            @Field("username") String username,
            @Field("emailNotificationsEnabled") Boolean emailNotificationsEnabled,
            @Field("pushNotificationsEnabled") Boolean pushNotificationsEnabled,
            @Field("biography") String biography,
            @Field("location") String location,
            @Field("website") String website);

    @Multipart @PUT("/users/{userId}/updateUser")
    Observable<UserProfileDTO> updateProfile(
            @Path("userId") int userId,
            @Part("deviceToken") String deviceToken,
            @Part("displayName") String displayName,
            @Part("email") String email,
            @Part("firstName") String firstName,
            @Part("lastName") String lastName,
            @Part("password") String password,
            @Part("passwordConfirmation") String passwordConfirmation,
            @Part("username") String username,
            @Part("emailNotificationsEnabled") Boolean emailNotificationsEnabled,
            @Part("pushNotificationsEnabled") Boolean pushNotificationsEnabled,
            @Part("biography") String biography,
            @Part("location") String location,
            @Part("website") String website,
            @Part("profilePicture") TypedOutput profilePicture);
    //</editor-fold>

    //<editor-fold desc="Check Display Name Available">
    @GET("/checkDisplayNameAvailable")
    Observable<UserAvailabilityDTO> checkDisplayNameAvailable(
            @Query("displayName") String username);
    //</editor-fold>

    //<editor-fold desc="Forgot Password">
    @POST("/forgotPassword")
    Observable<ForgotPasswordDTO> forgotPassword(
            @Body ForgotPasswordFormDTO forgotPasswordFormDTO);
    //</editor-fold>

    //<editor-fold desc="Search Users">
    @GET("/users/search")
    Observable<UserSearchResultDTOList> searchUsers(
            @Query("q") String searchString,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Search Allowable Recipients">
    @GET("/users/allowableRecipients")
    Observable<PaginatedAllowableRecipientDTO> searchAllowableRecipients(
            @Query("searchTerm") String searchString,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get User">
    @GET("/users/{userId}")
    Observable<UserProfileDTO> getUser(
            @Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Get User Transactions History">

    @GET("/users/{userId}/transactionHistory")
    Observable<UserTransactionHistoryDTOList> getUserTransactions(
            @Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Update PayPal Email">
    @POST("/users/{userId}/updatePayPalEmail")
    Observable<UpdatePayPalEmailDTO> updatePayPalEmail(
            @Path("userId") int userId,
            @Body UpdatePayPalEmailFormDTO updatePayPalEmailFormDTO);
    //</editor-fold>

    //<editor-fold desc="Update Alipay Account">
    @POST("/users/{userId}/updateAlipayAccount")
    Observable<UpdateAlipayAccountDTO> updateAlipayAccount(
            @Path("userId") int userId,
            @Body UpdateAlipayAccountFormDTO updateAlipayAccountFormDTO);
    //</editor-fold>

    //<editor-fold desc="Get Friends">
    @GET("/users/{userId}/getFriends")
    Observable<UserFriendsDTOList> getFriends(
            @Path("userId") int userId);

    @GET("/users/{userId}/getWeiboFriends")
    Observable<UserFriendsDTOList> getSocialWeiboFriends(@Path("userId") int userId);

    @GET("/users/{userId}/getFacebookFriends")
    Observable<UserFriendsDTOList> getSocialFacebookFriends(@Path("userId") int userId);

    @GET("/users/{userId}/GetNewFriends")
    Observable<UserFriendsDTOList> getSocialFriends(
            @Path("userId") int userId,
            @Query("socialNetwork") SocialNetworkEnum socialNetwork);

    @GET("/users/{userId}/SearchFriends")
    Observable<UserFriendsDTOList> searchSocialFriends(
            @Path("userId") int userId,
            @Query("socialNetwork") SocialNetworkEnum socialNetwork,
            @Query("q") String query);
    //</editor-fold>

    @POST("/users/batchFollow/free")
    Observable<UserProfileDTO> followBatchFree(@Body BatchFollowFormDTO batchFollowFormDTO);

    //<editor-fold desc="Invite Friends">
    @POST("/users/{userId}/inviteFriends")
    Observable<BaseResponseDTO> inviteFriends(
            @Path("userId") int userId,
            @Body InviteFormDTO inviteFormDTO);
    //</editor-fold>

    //<editor-fold desc="Add Follow Credit">
    @POST("/users/{userId}/addCredit")
    Observable<UserProfileDTO> addCredit(
            @Path("userId") int userId,
            @Body PurchaseReportDTO purchaseReportDTO);
    //</editor-fold>

    //<editor-fold desc="Follow Hero">
    @POST("/users/{userId}/follow/free")
    Observable<UserProfileDTO> freeFollow(
            @Path("userId") int userId, @Body String emptyBody);

    //<editor-fold desc="Unfollow Hero">
    @POST("/users/{userId}/unfollow")
    Observable<UserProfileDTO> unfollow(
            @Path("userId") int userId, @Body String emptyBody);
    //</editor-fold>

    //<editor-fold desc="Get Heroes">
    @GET("/users/{userId}/heroes")
    Observable<HeroDTOList> getHeroes(
            @Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Suggest Heroes">
    @GET("/users/heroes/bySectorAndExchange")
    Observable<LeaderboardUserDTOList> suggestHeroes(
            @Query("exchange") Integer exchangeId,
            @Query("sector") Integer sectorId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("/users/heroes/bySectorsAndExchanges")
    Observable<LeaderboardUserDTOList> suggestHeroes(
            @Query("exchanges") String commaSeparatedExchangeIds,
            @Query("sectors") String commaSeparatedSectorIds,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Update Country Code">
    @POST("/users/{userId}/updateCountryCode")
    Observable<UpdateCountryCodeDTO> updateCountryCode(
            @Path("userId") int userId,
            @Body UpdateCountryCodeFormDTO updateCountryCodeFormDTO);
    //</editor-fold>

    //<editor-fold desc="Update Referral Code">
    @POST("/users/{userId}/updateInviteCode")
    Observable<BaseResponseDTO> updateReferralCode(
            @Path("userId") int userId,
            @Body UpdateReferralCodeDTO updateReferralCodeDTO);
    //</editor-fold>

    //<editor-fold desc="Send Analytics">
    @POST("/analytics")
    Observable<Response> sendAnalytics(
            @Body BatchAnalyticsEventForm batchAnalyticsEventForm);
    //</editor-fold>

    //<editor-fold desc="Create FX Portfolio">
    @POST("/users/{userId}/portfolios/createFX")
    Observable<PortfolioDTO> createFXPortfolioRx(
            @Path("userId") int userId, @Body String emptyString);
    //</editor-fold>
}
