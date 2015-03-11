package com.tradehero.th.network.service;

import com.tradehero.th.api.users.LoginFormDTO;
import com.tradehero.th.api.users.UserLoginDTO;
import retrofit.http.Body;
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

}
