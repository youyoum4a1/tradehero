package com.tradehero.th.network.service;

import com.tradehero.th.api.form.ForgotPasswordFormDTO;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.users.ForgotPasswordDTO;
import com.tradehero.th.api.users.UserAvailabilityDTO;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserSearchResultDTO;
import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Body;
import retrofit.http.HEAD;
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

    @Multipart
    @POST("/SignupWithEmail")
    void signUpWithEmailWithProfilePicture();

    @POST("/users")
    void signUp(@Header("Authorization") String authorization, @Body UserFormDTO user, Callback<UserProfileDTO> cb);

    @POST("/login")
    void signIn(@Header("Authorization") String authorization, @Body UserFormDTO user, Callback<UserLoginDTO> cb);

    @POST("/logout")
    void signOut(@Header("Authorization") String authorization, Callback<Object> cb);

    @GET("/checkDisplayNameAvailable")
    void checkDisplayNameAvailable(@Query("displayName") String username, Callback<UserAvailabilityDTO> callback);

    @POST("/forgotPassword")
    void forgotPassword(@Body ForgotPasswordFormDTO forgotPasswordFormDTO, Callback<ForgotPasswordDTO> callback);

    //<editor-fold desc="Search Users">
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
            Callback<UserProfileDTO> callback)
            throws RetrofitError;
    //</editor-fold>
}
