package com.tradehero.th.network.service;

import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 6:01 PM Copyright (c) TradeHero */
public interface UserService
{
    @POST("/users")
    void authenticate(@Body UserFormDTO user, Callback<UserProfileDTO> cb);
}
