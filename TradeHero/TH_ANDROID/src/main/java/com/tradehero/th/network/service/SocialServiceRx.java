package com.tradehero.th.network.service;

import com.tradehero.th.api.auth.AccessTokenForm;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.social.SocialNetworkFormDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

interface SocialServiceRx
{
    //<editor-fold desc="Connect User">
    @POST("/users/{userId}/connect")
    Observable<UserProfileDTO> connect(
            @Path("userId") int userId,
            @Body UserFormDTO userFormDTO);

    @POST("/users/{userId}/connect")
    Observable<UserProfileDTO> connect(
            @Path("userId") int userId,
            @Body AccessTokenForm accessTokenForm);
    //</editor-fold>

    //<editor-fold desc="Disconnect User">
    @POST("/users/{userId}/disconnect")
    Observable<UserProfileDTO> disconnect(
            @Path("userId") int userId,
            @Body SocialNetworkFormDTO socialNetworkFormDTO);
    //</editor-fold>
}
