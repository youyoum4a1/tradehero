package com.tradehero.th.network.service;

import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.users.password.ForgotPasswordFormDTO;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.users.*;

import com.tradehero.th.api.users.password.ForgotPasswordDTO;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailDTO;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailFormDTO;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Body;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 6:01 PM Copyright (c) TradeHero */

public interface UserService
{

    // TODO @retrofit does not accept to pass a Map as multiple fields
    //{
    //    "biography": null,
    //        "deviceToken": null,
    //        "displayName": "Hello moto23",
    //        "email": "testttt1@ttt.com",
    //        "emailNotificationsEnabled": null,
    //        "firstName": "",
    //        "lastName": "",
    //        "location": null,
    //        "password": "asd123",
    //        "passwordConfirmation": "asd123",
    //        "pushNotificationsEnabled": null,
    //        "username": null,
    //        "website": null
    //}
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

    @FormUrlEncoded
    @POST("/users/{userId}/updateUser")
    void updateProfile(@Header("Authorization") String authorization,
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

    @Multipart
    @POST("/SignupWithEmail")
    void signUpWithEmailWithProfilePicture();

    //<editor-fold desc="Signup">
    @POST("/users")
    UserProfileDTO signUp(
            @Header("Authorization") String authorization,
            @Body UserFormDTO user)
        throws RetrofitError;

    @POST("/users")
    void signUp(
            @Header("Authorization") String authorization,
            @Body UserFormDTO user,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Signin">
    @POST("users/signin")
    Response signIn(
            @Body WebSignInFormDTO webSignInFormDTO)
        throws RetrofitError;

    @POST("users/signin")
    void signIn(
            @Body WebSignInFormDTO webSignInFormDTO,
            Callback<Response> callback);
    //</editor-fold>

    //<editor-fold desc="Login">
    @POST("/login")
    UserLoginDTO signIn(
            @Header("Authorization") String authorization,
            @Body UserFormDTO user)
        throws RetrofitError;

    @POST("/login")
    void signIn(
            @Header("Authorization") String authorization,
            @Body UserFormDTO user,
            Callback<UserLoginDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Update Authorization Tokens">
    @POST("/updateAuthorizationTokens")
    Response updateAuthorizationTokens(
            @Body UserFormDTO userFormDTO)
        throws RetrofitError;

    @POST("/updateAuthorizationTokens")
    void updateAuthorizationTokens(
            @Body UserFormDTO userFormDTO,
            Callback<Response> callback)
        throws RetrofitError;
    //</editor-fold>

    //<editor-fold desc="Update Device">
    @POST("/updateDevice")
    UserProfileDTO updateDevice()
        throws RetrofitError;

    @POST("/updateDevice")
    void updateDevice(
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Logout">
    @POST("/logout")
    Response signOut(
            @Header("Authorization") String authorization)
        throws RetrofitError;

    @POST("/logout")
    void signOut(
            @Header("Authorization") String authorization,
            Callback<Response> callback);
    //</editor-fold>

    //<editor-fold desc="Check Display Name Available">
    @GET("/checkDisplayNameAvailable")
    UserAvailabilityDTO checkDisplayNameAvailable(
            @Query("displayName") String username)
        throws RetrofitError;

    @GET("/checkDisplayNameAvailable")
    void checkDisplayNameAvailable(
            @Query("displayName") String username,
            Callback<UserAvailabilityDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Forgot Password">
    @POST("/forgotPassword")
    ForgotPasswordDTO forgotPassword(
            @Body ForgotPasswordFormDTO forgotPasswordFormDTO)
        throws RetrofitError;

    @POST("/forgotPassword")
    void forgotPassword(
            @Body ForgotPasswordFormDTO forgotPasswordFormDTO,
            Callback<ForgotPasswordDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Search Users">
    @GET("/users/search")
    List<UserSearchResultDTO> searchUsers(
            @Query("q") String searchString)
        throws RetrofitError;

    @GET("/users/search")
    void searchUsers(
            @Query("q") String searchString,
            Callback<List<UserSearchResultDTO>> callback);

    @GET("/users/search")
    List<UserSearchResultDTO> searchUsers(
            @Query("q") String searchString,
            @Query("page") int page)
        throws RetrofitError;

    @GET("/users/search")
    void searchUsers(
            @Query("q") String searchString,
            @Query("page") int page,
            Callback<List<UserSearchResultDTO>> callback);

    @GET("/users/search")
    List<UserSearchResultDTO> searchUsers(
            @Query("q") String searchString,
            @Query("page") int page,
            @Query("perPage") int perPage)
        throws RetrofitError;

    @GET("/users/search")
    void searchUsers(
            @Query("q") String searchString,
            @Query("page") int page,
            @Query("perPage") int perPage,
            Callback<List<UserSearchResultDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Get User">
    @GET("/users/{userId}")
    UserProfileDTO getUser(
            @Path("userId") int userId)
        throws RetrofitError;

    @GET("/users/{userId}")
    void getUser(
            @Path("userId") int userId,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Get User Transactions History">
    @GET("/users/{userId}/transactionHistory")
    List<UserTransactionHistoryDTO> getUserTransactions(
            @Path("userId") int userId)
        throws RetrofitError;

    @GET("/users/{userId}/transactionHistory")
    void getUserTransactions(
            @Path("userId") int userId,
            Callback<List<UserTransactionHistoryDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Update PayPal Email">
    @POST("/users/{userId}/updatePayPalEmail")
    UpdatePayPalEmailDTO updatePayPalEmail(
            @Path("userId") int userId,
            @Body UpdatePayPalEmailFormDTO updatePayPalEmailFormDTO)
        throws RetrofitError;

    @POST("/users/{userId}/updatePayPalEmail")
    void updatePayPalEmail(
            @Path("userId") int userId,
            @Body UpdatePayPalEmailFormDTO updatePayPalEmailFormDTO,
            Callback<UpdatePayPalEmailDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Delete User">
    @DELETE("/users/{userId}")
    Response deleteUser(
            @Path("userId") int userId)
        throws RetrofitError;

    @DELETE("/users/{userId}")
    void deleteUser(
            @Path("userId") int userId,
            Callback<Response> callback);
    //</editor-fold>

    //<editor-fold desc="Get Friends">
    @GET("/users/{userId}/getFriends")
    List<UserFriendsDTO> getFriends(
            @Path("userId") int userId)
        throws RetrofitError;

    @GET("/users/{userId}/getFriends")
    void getFriends(
            @Path("userId") int userId,
            Callback<List<UserFriendsDTO>> callback);
    //</editor-fold>

    //<editor-fold desc="Invite Friends">
    @POST("/users/{userId}/inviteFriends")
    Response inviteFriends(@Path("userId") int userId,
            @Body InviteFormDTO inviteFormDTO)
        throws RetrofitError;

    @POST("/users/{userId}/inviteFriends")
    void inviteFriends(@Path("userId") int userId,
            @Body InviteFormDTO inviteFormDTO,
            Callback<Response> callback);
    //</editor-fold>
}
