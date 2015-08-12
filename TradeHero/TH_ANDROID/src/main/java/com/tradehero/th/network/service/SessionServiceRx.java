package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.system.SystemStatusDTO;
import com.tradehero.th.api.users.LoginSignUpFormDTO;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.utils.Constants;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import rx.Observable;

import static com.tradehero.th.utils.Constants.AUTHORIZATION;

interface SessionServiceRx
{
    //<editor-fold desc="Get System Status">
    @GET("/systemStatus")
    Observable<SystemStatusDTO> getSystemStatus();
    //</editor-fold>

    //<editor-fold desc="Login and social register">
    @POST("/login")
    Observable<UserLoginDTO> login(
            @Header(AUTHORIZATION) String authorization,
            @Body LoginSignUpFormDTO loginFormDTO);

    @POST("/signupAndLogin")
    Observable<UserLoginDTO> signupAndLogin(
            @Header(AUTHORIZATION) String authorization, @Body LoginSignUpFormDTO loginSignUpFormDTO);
    //</editor-fold>

    //<editor-fold desc="Update Authorization Tokens">
    @POST("/updateAuthorizationTokens")
    Observable<BaseResponseDTO> updateAuthorizationTokens(
            @Header(Constants.AUTHORIZATION) String authorization,
            @Body LoginSignUpFormDTO userFormDTO);
    //</editor-fold>

    //<editor-fold desc="Update Device">
    @FormUrlEncoded
    @POST("/updateDevice")
    Observable<UserProfileDTO> updateDevice(
            @Field("token") String deviceToken);
    //</editor-fold>

    //<editor-fold desc="Logout">
    @POST("/logout")
    Observable<UserProfileDTO> logout(@Body String emptyBody); //HACK, retrofit POST expects a BODY
    //</editor-fold>
}
