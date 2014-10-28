package com.tradehero.th.network.service;

import com.tradehero.th.api.system.SystemStatusDTO;
import com.tradehero.th.api.users.LoginSignUpFormDTO;
import com.tradehero.th.api.users.UserLoginDTO;
import retrofit.http.Body;
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
}
