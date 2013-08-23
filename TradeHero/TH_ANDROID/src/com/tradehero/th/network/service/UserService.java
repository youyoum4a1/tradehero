package com.tradehero.th.network.service;

import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import retrofit.Callback;
import retrofit.http.Header;
import retrofit.http.Body;
import retrofit.http.HEAD;
import retrofit.http.POST;
import retrofit.http.Path;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 6:01 PM Copyright (c) TradeHero */

public interface UserService
{
    @POST("/{mode}")
    void authenticate(@Header("Authorization") String authorization, @Path("mode") String mode, @Body UserFormDTO user, Callback<UserProfileDTO> cb);
}
