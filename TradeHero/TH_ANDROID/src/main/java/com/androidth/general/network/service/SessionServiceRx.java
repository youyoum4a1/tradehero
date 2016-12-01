package com.androidth.general.network.service;

import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.system.SystemStatusDTO;
import com.androidth.general.api.users.LoginSignUpFormDTO;
import com.androidth.general.api.users.UserLoginDTO;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.utils.Constants;
import com.fernandocejas.frodo.annotation.RxLogObservable;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

import static com.androidth.general.utils.Constants.AUTHORIZATION;

interface SessionServiceRx
{
    //<editor-fold desc="Get System Status">
    @GET("api/systemStatus")
    Observable<SystemStatusDTO> getSystemStatus();
    //</editor-fold>

    //<editor-fold desc="Login and social register">
    @POST("api/login")
    @RxLogObservable Observable<UserLoginDTO> login(
            @Header(AUTHORIZATION) String authorization,
            @Body LoginSignUpFormDTO loginFormDTO);

    @POST("api/signupAndLogin")
    @RxLogObservable Observable<UserLoginDTO> signupAndLogin(
            @Header(AUTHORIZATION) String authorization, @Body LoginSignUpFormDTO loginSignUpFormDTO);
    //</editor-fold>

    //<editor-fold desc="Update Authorization Tokens">
    @POST("api/updateAuthorizationTokens")
    Observable<BaseResponseDTO> updateAuthorizationTokens(
            @Header(Constants.AUTHORIZATION) String authorization,
            @Body LoginSignUpFormDTO userFormDTO);
    //</editor-fold>

    //<editor-fold desc="Update Device">
    @FormUrlEncoded
    @POST("api/updateDevice")
    Observable<UserProfileDTO> updateDevice(
            @Field("token") String deviceToken);
    //</editor-fold>

    //<editor-fold desc="Logout">
    @POST("api/logout")
    Observable<UserProfileDTO> logout(@Body String emptyBody); //HACK, retrofit POST expects a BODY
    //</editor-fold>
}
