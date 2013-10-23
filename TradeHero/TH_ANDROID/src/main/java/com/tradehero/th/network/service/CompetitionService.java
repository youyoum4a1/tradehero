package com.tradehero.th.network.service;

import com.tradehero.th.api.competition.CompetitionFormDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.GET;

/** Created with IntelliJ IDEA. User: xavier Date: 10/10/13 Time: 6:12 PM To change this template use File | Settings | File Templates. */
public interface CompetitionService
{
    //<editor-fold desc="Enroll">
    @GET("/providers/enroll")
    UserProfileDTO enroll(
            CompetitionFormDTO form)
            throws RetrofitError;

    @GET("/providers/enroll")
    void enroll(
            CompetitionFormDTO form,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Outbound">
    @GET("/providers/outbound")
    UserProfileDTO outbound(
            CompetitionFormDTO form)
            throws RetrofitError;

    @GET("/providers/outbound")
    void outbound(
            CompetitionFormDTO form,
            Callback<UserProfileDTO> callback);
    //</editor-fold>
}
