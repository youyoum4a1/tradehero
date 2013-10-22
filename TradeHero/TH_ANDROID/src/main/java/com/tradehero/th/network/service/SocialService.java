package com.tradehero.th.network.service;

import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.social.SocialNetworkFormDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 10:13 PM To change this template use File | Settings | File Templates. */
public interface SocialService
{
    //<editor-fold desc="Connect User">
    @POST("/users/{userId}/connect")
    UserProfileDTO connect(
            @Path("userId") int userId,
            @Body UserFormDTO userFormDTO)
        throws RetrofitError;

    @POST("/users/{userId}/connect")
    void connect(
            @Path("userId") int userId,
            @Body UserFormDTO userFormDTO,
            Callback<UserProfileDTO> callback)
        throws RetrofitError;
    //</editor-fold>

    //<editor-fold desc="Disconnect User">
    @POST("/users/{userId}/disconnect")
    UserProfileDTO disconnect(
            @Path("userId") int userId,
            @Body SocialNetworkFormDTO socialNetworkFormDTO)
        throws RetrofitError;

    @POST("/users/{userId}/disconnect")
    void disconnect(
            @Path("userId") int userId,
            @Body SocialNetworkFormDTO socialNetworkFormDTO,
            Callback<UserProfileDTO> callback)
        throws RetrofitError;
    //</editor-fold>
}
