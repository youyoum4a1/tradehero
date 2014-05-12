package com.tradehero.th.network.service;

import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.social.SocialNetworkFormDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

public interface SocialService
{
    //<editor-fold desc="Connect User">
    @POST("/users/{userId}/connect")
    UserProfileDTO connect(
            @Path("userId") int userId,
            @Body UserFormDTO userFormDTO);
    //</editor-fold>

    //<editor-fold desc="Disconnect User">
    @POST("/users/{userId}/disconnect")
    UserProfileDTO disconnect(
            @Path("userId") int userId,
            @Body SocialNetworkFormDTO socialNetworkFormDTO);
    //</editor-fold>
}
