package com.tradehero.th.network.service;

import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.social.SocialNetworkFormDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by xavier on 3/7/14.
 */
interface SocialServiceAsync
{
    //<editor-fold desc="Connect User">
    @POST("/users/{userId}/connect")
    void connect(
            @Path("userId") int userId,
            @Body UserFormDTO userFormDTO,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Disconnect User">
    @POST("/users/{userId}/disconnect")
    void disconnect(
            @Path("userId") int userId,
            @Body SocialNetworkFormDTO socialNetworkFormDTO,
            Callback<UserProfileDTO> callback);
    //</editor-fold>
}
