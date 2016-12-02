package com.androidth.general.network.service;

import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.analytics.BatchAnalyticsEventForm;
import com.androidth.general.api.billing.PurchaseReportDTO;
import com.androidth.general.api.form.UserFormDTO;
import com.androidth.general.api.leaderboard.LeaderboardUserDTOList;
import com.androidth.general.api.portfolio.PortfolioDTO;
import com.androidth.general.api.social.BatchFollowFormDTO;
import com.androidth.general.api.social.HeroDTOList;
import com.androidth.general.api.social.InviteFormDTO;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.api.social.UserFriendsDTOList;
import com.androidth.general.api.users.PaginatedAllowableRecipientDTO;
import com.androidth.general.api.users.UpdateCountryCodeDTO;
import com.androidth.general.api.users.UpdateCountryCodeFormDTO;
import com.androidth.general.api.users.UpdateReferralCodeDTO;
import com.androidth.general.api.users.UserAvailabilityDTO;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.api.users.UserSearchResultDTOList;
import com.androidth.general.api.users.UserTransactionHistoryDTOList;
import com.androidth.general.api.users.password.ForgotPasswordDTO;
import com.androidth.general.api.users.password.ForgotPasswordFormDTO;
import com.androidth.general.api.users.payment.UpdateAlipayAccountDTO;
import com.androidth.general.api.users.payment.UpdateAlipayAccountFormDTO;
import com.androidth.general.api.users.payment.UpdatePayPalEmailDTO;
import com.androidth.general.api.users.payment.UpdatePayPalEmailFormDTO;

import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

import static com.androidth.general.network.NetworkConstants.AUTHORIZATION;

public interface UserServiceRx
{
    //<editor-fold desc="Sign-Up With Email">
    @FormUrlEncoded
    @POST("api/SignupWithEmail") Observable<UserProfileDTO> signUpWithEmail(
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


    @Multipart
    @POST("api/SignupWithEmail") Observable<UserProfileDTO> signUpWithEmail(
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
            @Part("profilePicture") RequestBody profilePicture);
    //</editor-fold>

    //<editor-fold desc="Signup">
    @POST("api/users")
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
            @Part("profilePicture") RequestBody profilePicture);
    //</editor-fold>

    //<editor-fold desc="Check Display Name Available">
    @GET("api/checkDisplayNameAvailable")
    Observable<UserAvailabilityDTO> checkDisplayNameAvailable(
            @Query("displayName") String username);
    //</editor-fold>

    //<editor-fold desc="Check Email Available">
    @GET("api/checkEmailAvailable")
    Observable<UserAvailabilityDTO> checkEmailAvailable(
            @Query("email") String email);
    //</editor-fold>

    //<editor-fold desc="Forgot Password">
    @POST("api/forgotPassword")
    Observable<ForgotPasswordDTO> forgotPassword(
            @Body ForgotPasswordFormDTO forgotPasswordFormDTO);
    //</editor-fold>

    //<editor-fold desc="Search Users">
    @GET("api/users/search")
    Observable<UserSearchResultDTOList> searchUsers(
            @Query("q") String searchString,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Search Allowable Recipients">
    @GET("api/users/allowableRecipients")
    Observable<PaginatedAllowableRecipientDTO> searchAllowableRecipients(
            @Query("searchTerm") String searchString,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Get User">
    @GET("api/users/{userId}")
    Observable<UserProfileDTO> getUser(
            @Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Get User Transactions History">

    @GET("api/users/{userId}/transactionHistory")
    Observable<UserTransactionHistoryDTOList> getUserTransactions(
            @Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Update PayPal Email">
    @POST("api/users/{userId}/updatePayPalEmail")
    Observable<UpdatePayPalEmailDTO> updatePayPalEmail(
            @Path("userId") int userId,
            @Body UpdatePayPalEmailFormDTO updatePayPalEmailFormDTO);
    //</editor-fold>

    //<editor-fold desc="Update Alipay Account">
    @POST("api/users/{userId}/updateAlipayAccount")
    Observable<UpdateAlipayAccountDTO> updateAlipayAccount(
            @Path("userId") int userId,
            @Body UpdateAlipayAccountFormDTO updateAlipayAccountFormDTO);
    //</editor-fold>

    //<editor-fold desc="Get Friends">
    @GET("api/users/{userId}/getFriends")
    Observable<UserFriendsDTOList> getFriends(
            @Path("userId") int userId);

    @GET("api/users/{userId}/getWeiboFriends")
    Observable<UserFriendsDTOList> getSocialWeiboFriends(@Path("userId") int userId);

    @GET("api/users/{userId}/getFacebookFriends")
    Observable<UserFriendsDTOList> getSocialFacebookFriends(@Path("userId") int userId);

    @GET("api/users/{userId}/GetNewFriends")
    Observable<UserFriendsDTOList> getSocialFriends(
            @Path("userId") int userId,
            @Query("socialNetwork") SocialNetworkEnum socialNetwork);

    @GET("api/users/{userId}/SearchFriends")
    Observable<UserFriendsDTOList> searchSocialFriends(
            @Path("userId") int userId,
            @Query("socialNetwork") SocialNetworkEnum socialNetwork,
            @Query("q") String query);
    //</editor-fold>

    @POST("api/users/batchFollow/free")
    Observable<UserProfileDTO> followBatchFree(@Body BatchFollowFormDTO batchFollowFormDTO);

    //<editor-fold desc="Invite Friends">
    @POST("api/users/{userId}/inviteFriends")
    Observable<BaseResponseDTO> inviteFriends(
            @Path("userId") int userId,
            @Body InviteFormDTO inviteFormDTO);
    //</editor-fold>

    //<editor-fold desc="Add Follow Credit">
    @POST("api/users/{userId}/addCredit")
    Observable<UserProfileDTO> addCredit(
            @Path("userId") int userId,
            @Body PurchaseReportDTO purchaseReportDTO);
    //</editor-fold>

    //<editor-fold desc="Follow Hero">
    @POST("api/users/{userId}/follow/free")
    Observable<UserProfileDTO> freeFollow(
            @Path("userId") int userId, @Body String emptyBody);

    //<editor-fold desc="Unfollow Hero">
    @POST("api/users/{userId}/unfollow")
    Observable<UserProfileDTO> unfollow(
            @Path("userId") int userId, @Body String emptyBody);
    //</editor-fold>

    //<editor-fold desc="Get Heroes">
    @GET("api/users/{userId}/heroes")
    Observable<HeroDTOList> getHeroes(
            @Path("userId") int userId);
    //</editor-fold>

    //<editor-fold desc="Suggest Heroes">
    @GET("api/users/heroes/bySectorAndExchange")
    Observable<LeaderboardUserDTOList> suggestHeroes(
            @Query("exchange") Integer exchangeId,
            @Query("sector") Integer sectorId,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);

    @GET("api/users/heroes/bySectorsAndExchanges")
    Observable<LeaderboardUserDTOList> suggestHeroes(
            @Query("exchanges") String commaSeparatedExchangeIds,
            @Query("sectors") String commaSeparatedSectorIds,
            @Query("page") Integer page,
            @Query("perPage") Integer perPage);
    //</editor-fold>

    //<editor-fold desc="Update Country Code">
    @POST("api/users/{userId}/updateCountryCode")
    Observable<UpdateCountryCodeDTO> updateCountryCode(
            @Path("userId") int userId,
            @Body UpdateCountryCodeFormDTO updateCountryCodeFormDTO);
    //</editor-fold>

    //<editor-fold desc="Update Referral Code">
    @POST("api/users/{userId}/updateInviteCode")
    Observable<BaseResponseDTO> updateReferralCode(
            @Path("userId") int userId,
            @Body UpdateReferralCodeDTO updateReferralCodeDTO);
    //</editor-fold>

    //<editor-fold desc="Send Analytics">
    @POST("api/analytics")
    Observable<Response> sendAnalytics(
            @Body BatchAnalyticsEventForm batchAnalyticsEventForm);
    //</editor-fold>

    //<editor-fold desc="Create FX Portfolio">
    @POST("api/users/{userId}/portfolios/createFX")
    Observable<PortfolioDTO> createFXPortfolioRx(
            @Path("userId") int userId, @Body String emptyString);
    //</editor-fold>
}
