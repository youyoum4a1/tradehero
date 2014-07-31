package com.tradehero.th.network.service;

import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.users.LoginFormDTO;
import com.tradehero.th.api.users.LoginSignUpFormDTO;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

interface SessionServiceAsync
{
    //<editor-fold desc="Login">
    @POST("/login")
    void login(
            @Header("Authorization") String authorization,
            @Body LoginFormDTO loginFormDTO,
            Callback<UserLoginDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Login and social register">
    @POST("/signupAndLogin")
    void signupAndLogin(
            @Header("Authorization") String authorization,
            @Body LoginSignUpFormDTO loginSignUpFormDTO,
            Callback<UserLoginDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Update Authorization Tokens">
    @POST("/updateAuthorizationTokens")
    void updateAuthorizationTokens(
            @Body UserFormDTO userFormDTO,
            Callback<Response> callback);
    //</editor-fold>

    //<editor-fold desc="Update Device">
    @FormUrlEncoded
    @POST("/updateDevice")
    void updateDevice(
            @Field("token") String deviceToken,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Logout">
    @POST("/logout")
    void logout(
            Callback<UserProfileDTO> callback);
    //</editor-fold>
}
