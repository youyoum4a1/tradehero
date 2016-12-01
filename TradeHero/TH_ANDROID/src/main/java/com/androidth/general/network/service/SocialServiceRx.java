package com.androidth.general.network.service;

import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.auth.AccessTokenForm;
import com.androidth.general.api.form.UserFormDTO;
import com.androidth.general.api.social.ReferralCodeShareFormDTO;
import com.androidth.general.api.social.SocialNetworkFormDTO;
import com.androidth.general.api.users.UserProfileDTO;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

interface SocialServiceRx
{
    //<editor-fold desc="Connect User">
    @POST("api/users/{userId}/connect")
    Observable<UserProfileDTO> connect(
            @Path("userId") int userId,
            @Body UserFormDTO userFormDTO);

    @POST("api/users/{userId}/connect")
    Observable<UserProfileDTO> connect(
            @Path("userId") int userId,
            @Body AccessTokenForm accessTokenForm);
    //</editor-fold>

    //<editor-fold desc="Disconnect User">
    @POST("api/users/{userId}/disconnect")
    Observable<UserProfileDTO> disconnect(
            @Path("userId") int userId,
            @Body SocialNetworkFormDTO socialNetworkFormDTO);
    //</editor-fold>

    @POST("api/social/shareReferralCode")
    Observable<BaseResponseDTO> shareReferralCode(
            @Body ReferralCodeShareFormDTO reqFormDTO);
}
