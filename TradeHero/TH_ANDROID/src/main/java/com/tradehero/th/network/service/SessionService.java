package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.users.LoginFormDTO;
import com.tradehero.th.api.users.LoginSignUpFormDTO;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

public interface SessionService
{
    //<editor-fold desc="Login">
    @POST("/login")
    UserLoginDTO login(
            @Header("Authorization") String authorization,
            @Body LoginFormDTO loginFormDTO);
    //</editor-fold>

    //<editor-fold desc="Login and social register">
    @POST("/signupAndLogin")
    UserLoginDTO signupAndLogin(
            @Header("Authorization") String authorization,
            @Body LoginSignUpFormDTO loginSignUpFormDTO);
    //</editor-fold>

    //<editor-fold desc="Update Authorization Tokens">
    @POST("/updateAuthorizationTokens")
    BaseResponseDTO updateAuthorizationTokens(
            @Body UserFormDTO userFormDTO);
    //</editor-fold>

    //<editor-fold desc="Update Device">
    @FormUrlEncoded
    @POST("/updateDevice")
    UserProfileDTO updateDevice(
            @Field("token") String deviceToken);
    //</editor-fold>

    //<editor-fold desc="Logout">
    @POST("/logout")
    UserProfileDTO logout();
    //</editor-fold>
}
