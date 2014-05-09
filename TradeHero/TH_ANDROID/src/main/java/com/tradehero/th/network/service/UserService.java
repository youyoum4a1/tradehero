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
import retrofit.RetrofitError;
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
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedOutput;

public interface UserService
{
    //<editor-fold desc="Sign-Up With Email">
    @FormUrlEncoded @POST("/SignupWithEmail")
    UserProfileDTO signUpWithEmail(
            @Header("Authorization") String authorization,
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
            @Field("website") String website);

    @Multipart @POST("/SignupWithEmail")
    UserProfileDTO signUpWithEmail(
            @Header("Authorization") String authorization,
            @Part("biography") String biography,
            @Part("deviceToken") String deviceToken,
            @Part("displayName") String displayName,
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
    UserProfileDTO signUp(
            @Header("Authorization") String authorization,
            @Body UserFormDTO user);
    //</editor-fold>

    //<editor-fold desc="Update Profile">
    @FormUrlEncoded @PUT("/users/{userId}/updateUser")
    UserProfileDTO updateProfile(
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
    UserProfileDTO updateProfile(
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

    //<editor-fold desc="Signin">
    @POST("users/signin")
    Response signIn(
            @Body WebSignInFormDTO webSignInFormDTO);
    //</editor-fold>

    //<editor-fold desc="Check Display Name Available">
    @GET("/checkDisplayNameAvailable")
    UserAvailabilityDTO checkDisplayNameAvailable(
            @Query("displayName") String username);
    //</editor-fold>

    //<editor-fold desc="Forgot Password">
    @POST("/forgotPassword")
    ForgotPasswordDTO forgotPassword(
            @Body ForgotPasswordFormDTO forgotPasswordFormDTO);
    //</editor-fold>

    //<editor-fold desc="Search Users">
    @GET("/users/search")
    List<UserSearchResultDTO> searchUsers(
            @Query("q") String searchString)
        throws RetrofitError;

    @GET("/users/search")
    List<UserSearchResultDTO> searchUsers(
            @Query("q") String searchString,
            @Query("page") int page)
        throws RetrofitError;

    @GET("/users/search")
    List<UserSearchResultDTO> searchUsers(
            @Query("q") String searchString,
            @Query("page") int page,
            @Query("perPage") int perPage)
        throws RetrofitError;
    //</editor-fold>

    //<editor-fold desc="Search Allowable Recipients">
    @GET("/users/allowableRecipients")
    PaginatedDTO<AllowableRecipientDTO> searchAllowableRecipients();

    @GET("/users/allowableRecipients")
    PaginatedDTO<AllowableRecipientDTO> searchAllowableRecipients(
            @Query("searchTerm") String searchString);

    @GET("/users/allowableRecipients")
    PaginatedDTO<AllowableRecipientDTO> searchAllowableRecipients(
            @Query("searchTerm") String searchString,
            @Query("page") int page);

    @GET("/users/allowableRecipients")
    PaginatedDTO<AllowableRecipientDTO> searchAllowableRecipients(
            @Query("searchTerm") String searchString,
            @Query("page") int page,
            @Query("perPage") int perPage);
    //</editor-fold>

    //<editor-fold desc="Get User">
    @GET("/users/{userId}")
    UserProfileDTO getUser(
            @Path("userId") int userId)
        throws RetrofitError;
    //</editor-fold>

    //<editor-fold desc="Get User Transactions History">
    @GET("/users/{userId}/transactionHistory")
    List<UserTransactionHistoryDTO> getUserTransactions(
            @Path("userId") int userId)
        throws RetrofitError;
    //</editor-fold>

    //<editor-fold desc="Update PayPal Email">
    @POST("/users/{userId}/updatePayPalEmail")
    UpdatePayPalEmailDTO updatePayPalEmail(
            @Path("userId") int userId,
            @Body UpdatePayPalEmailFormDTO updatePayPalEmailFormDTO)
        throws RetrofitError;
    //</editor-fold>

    //<editor-fold desc="Update Alipay Account">
    @POST("/users/{userId}/updateAlipayAccount")
    UpdateAlipayAccountDTO updateAlipayAccount(
            @Path("userId") int userId,
            @Body UpdateAlipayAccountFormDTO updateAlipayAccountFormDTO);
    //</editor-fold>

    //<editor-fold desc="Delete User">
    @DELETE("/users/{userId}")
    Response deleteUser(
            @Path("userId") int userId)
        throws RetrofitError;
    //</editor-fold>

    //<editor-fold desc="Get Friends">
    @GET("/users/{userId}/getFriends")
    List<UserFriendsDTO> getFriends(
            @Path("userId") int userId)
        throws RetrofitError;
    //</editor-fold>

    //<editor-fold desc="Invite Friends">
    @POST("/users/{userId}/inviteFriends")
    Response inviteFriends(
            @Path("userId") int userId,
            @Body InviteFormDTO inviteFormDTO)
        throws RetrofitError;

    // TODO use UserServiceWrapper and UserServiceAsync
    @Deprecated
    @POST("/users/{userId}/inviteFriends")
    void inviteFriends(
            @Path("userId") int userId,
            @Body InviteFormDTO inviteFormDTO,
            Callback<Response> callback);
    //</editor-fold>

    //<editor-fold desc="Add Follow Credit">
    @POST("/users/{userId}/addCredit")
    UserProfileDTO addCredit(
            @Path("userId") int userId,
            @Body GooglePlayPurchaseDTO purchaseDTO)
        throws RetrofitError;

    // TODO use UserServiceWrapper and UserServiceAsync
    @Deprecated
    @POST("/users/{userId}/addCredit")
    void addCredit(
            @Path("userId") int userId,
            @Body GooglePlayPurchaseDTO purchaseDTO,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Follow Hero">
    @POST("/users/{userId}/follow")
    UserProfileDTO follow(
            @Path("userId") int userId)
        throws RetrofitError;

    // TODO use UserServiceWrapper and UserServiceAsync
    @Deprecated
    @POST("/users/{userId}/follow")
    void follow(
            @Path("userId") int userId,
            Callback<UserProfileDTO> callback);

    @POST("/users/{userId}/follow/free")
    UserProfileDTO freeFollow(
            @Path("userId") int userId);

    @POST("/users/{userId}/follow/free")
    void freeFollow(
            @Path("userId") int userId,
            Callback<UserProfileDTO> callback);

    @POST("/users/{userId}/follow")
    UserProfileDTO follow(
            @Path("userId") int userId,
            @Body GooglePlayPurchaseDTO purchaseDTO)
        throws RetrofitError;
    //</editor-fold>

    //<editor-fold desc="Unfollow Hero">
    @POST("/users/{userId}/unfollow")
    UserProfileDTO unfollow(
            @Path("userId") int userId)
        throws RetrofitError;
    //</editor-fold>

    //<editor-fold desc="Get Heroes">
    @GET("/users/{userId}/heroes")
    HeroDTOList getHeroes(
            @Path("userId") int userId)
        throws RetrofitError;
    //</editor-fold>
}
