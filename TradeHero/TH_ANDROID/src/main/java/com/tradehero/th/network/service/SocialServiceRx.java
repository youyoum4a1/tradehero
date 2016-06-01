package com.ayondo.academy.network.service;

import com.ayondo.academy.api.BaseResponseDTO;
import com.ayondo.academy.api.auth.AccessTokenForm;
import com.ayondo.academy.api.form.UserFormDTO;
import com.ayondo.academy.api.social.ReferralCodeShareFormDTO;
import com.ayondo.academy.api.social.SocialNetworkFormDTO;
import com.ayondo.academy.api.users.UserProfileDTO;
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

    @POST("/social/shareReferralCode")
    Observable<BaseResponseDTO> shareReferralCode(
            @Body ReferralCodeShareFormDTO reqFormDTO);
}
