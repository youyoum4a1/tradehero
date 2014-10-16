package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.users.LoginSignUpFormDTO;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

import static com.tradehero.th.utils.Constants.AUTHORIZATION;

interface SessionServiceAsync
{
    //<editor-fold desc="Login">
    @POST("/login")
    void login(
            @Header(AUTHORIZATION) String authorization,
            @Body LoginSignUpFormDTO loginFormDTO,
            Callback<UserLoginDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Login and social register">
    @POST("/signupAndLogin")
    void signupAndLogin(
            @Header(AUTHORIZATION) String authorization,
            @Body LoginSignUpFormDTO loginSignUpFormDTO,
            Callback<UserLoginDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Update Authorization Tokens">
    @POST("/updateAuthorizationTokens")
    void updateAuthorizationTokens(
            @Header(AUTHORIZATION) String authorization,
            @Body LoginSignUpFormDTO userFormDTO,
            Callback<BaseResponseDTO> callback);
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
