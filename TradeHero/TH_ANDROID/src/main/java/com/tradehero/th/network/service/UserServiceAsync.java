package com.tradehero.th.network.service;

import com.tradehero.common.billing.googleplay.GooglePlayPurchaseDTO;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.social.HeroDTOList;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.UserAvailabilityDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.api.users.UserTransactionHistoryDTO;
import com.tradehero.th.api.users.WebSignInFormDTO;
import com.tradehero.th.api.users.password.ForgotPasswordDTO;
import com.tradehero.th.api.users.password.ForgotPasswordFormDTO;
import com.tradehero.th.api.users.payment.UpdateAlipayAccountDTO;
import com.tradehero.th.api.users.payment.UpdateAlipayAccountFormDTO;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailDTO;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailFormDTO;
import java.util.List;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

interface UserServiceAsync
{
    //<editor-fold desc="Sign-Up With Email">
    @FormUrlEncoded
    @POST("/SignupWithEmail")
    void signUpWithEmail(@Header("Authorization") String authorization,
            @Field("biography") String biography,
            @Field("deviceToken") String deviceToken,
            @Field("displayName") String displayName,
            @Field("email") String email,
            @Field("emailNotificationsEnabled") Boolean emailNotificationsEnabled,
            @Field("firstName") String firstName,
            @Field("lastName") String lastName,
            @Field("location") String location,
            @Field("password") String password,
            @Field("passwordConfirmation") String passwordConfirmation,
            @Field("pushNotificationsEnabled") Boolean pushNotificationsEnabled,
            @Field("username") String username,
            @Field("website") String website,
            Callback<UserProfileDTO> cb);
    //</editor-fold>

    //<editor-fold desc="Update Profile">
    @FormUrlEncoded
    @PUT("/users/{userId}/updateUser")
    void updateProfile(
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
            @Field("website") String website,
            Callback<UserProfileDTO> cb);
    //</editor-fold>

    @Multipart
    @POST("/SignupWithEmail")
    void signUpWithEmailWithProfilePicture();

    //<editor-fold desc="Signup">
    @POST("/users")
    void signUp(
            @Header("Authorization") String authorization,
            @Body UserFormDTO user,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Signin">
    @POST("users/signin")
    void signIn(
            @Body WebSignInFormDTO webSignInFormDTO,
            Callback<Response> callback);
    //</editor-fold>

    //<editor-fold desc="Check Display Name Available">
    @GET("/checkDisplayNameAvailable")
    void checkDisplayNameAvailable(
            @Query("displayName") String username,
            Callback<UserAvailabilityDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Forgot Password">
    @POST("/forgotPassword")
    void forgotPassword(
            @Body ForgotPasswordFormDTO forgotPasswordFormDTO,
            Callback<ForgotPasswordDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Search Users">
    @GET("/users/search")
    void searchUsers(
            @Query("q") String searchString,
            Callback<List<UserSearchResultDTO>> callback);

    @GET("/users/search")
    void searchUsers(
            @Query("q") String searchString,
            @Query("page") int page,
            Callback<List<UserSearchResultDTO>> callback);

    @GET("/users/search")
    void searchUsers(
            @Query("q") String searchString,
            @Query("page") int page,
            @Query("perPage") int perPage,
            Callback<List<UserSearchResultDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Search Allowable Recipients">
    @GET("/users/allowableRecipients")
    void searchAllowableRecipients(
            Callback<PaginatedDTO<AllowableRecipientDTO>> callback);

    @GET("/users/allowableRecipients")
    void searchAllowableRecipients(
            @Query("searchTerm") String searchString,
            Callback<PaginatedDTO<AllowableRecipientDTO>> callback);

    @GET("/users/allowableRecipients")
    void searchAllowableRecipients(
            @Query("searchTerm") String searchString,
            @Query("page") int page,
            Callback<PaginatedDTO<AllowableRecipientDTO>> callback);

    @GET("/users/allowableRecipients")
    void searchAllowableRecipients(
            @Query("searchTerm") String searchString,
            @Query("page") int page,
            @Query("perPage") int perPage,
            Callback<PaginatedDTO<AllowableRecipientDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Get User">
    @GET("/users/{userId}")
    void getUser(
            @Path("userId") int userId,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Get User Transactions History">
    @GET("/users/{userId}/transactionHistory")
    void getUserTransactions(
            @Path("userId") int userId,
            Callback<List<UserTransactionHistoryDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Update PayPal Email">
    @POST("/users/{userId}/updatePayPalEmail")
    void updatePayPalEmail(
            @Path("userId") int userId,
            @Body UpdatePayPalEmailFormDTO updatePayPalEmailFormDTO,
            Callback<UpdatePayPalEmailDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Update Alipay Email">
    @POST("/users/{userId}/updateAlipayAccount")
    void updateAlipayAccount(
            @Path("userId") int userId,
            @Body UpdateAlipayAccountFormDTO updateAlipayAccountFormDTO,
            Callback<UpdateAlipayAccountDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Delete User">
    @DELETE("/users/{userId}")
    void deleteUser(
            @Path("userId") int userId,
            Callback<Response> callback);
    //</editor-fold>

    //<editor-fold desc="Get Friends">
    @GET("/users/{userId}/getFriends")
    void getFriends(
            @Path("userId") int userId,
            Callback<List<UserFriendsDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Invite Friends">
    @POST("/users/{userId}/inviteFriends")
    void inviteFriends(
            @Path("userId") int userId,
            @Body InviteFormDTO inviteFormDTO,
            Callback<Response> callback);
    //</editor-fold>

    //<editor-fold desc="Add Follow Credit">
    @POST("/users/{userId}/addCredit")
    void addCredit(
            @Path("userId") int userId,
            @Body GooglePlayPurchaseDTO purchaseDTO,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Follow Hero">
    @POST("/users/{userId}/follow")
    void follow(
            @Path("userId") int userId,
            Callback<UserProfileDTO> callback);

    @POST("/users/{userId}/follow")
    void follow(
            @Path("userId") int userId,
            @Body GooglePlayPurchaseDTO purchaseDTO,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Unfollow Hero">
    @POST("/users/{userId}/unfollow")
    void unfollow(
            @Path("userId") int userId,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Get Heroes">
    @GET("/users/{userId}/heroes")
    void getHeroes(
            @Path("userId") int userId,
            Callback<HeroDTOList> callback);
    //</editor-fold>
}
