package com.tradehero.th.network.service;

import com.tradehero.chinabuild.data.*;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by palmer on 15/2/2.
 */
public interface AdministratorManageTimelineServiceAsync {

    @POST("/users/{userId}/timeline/{timelineId}/operation")
    void operateProduction(@Path("userId")int userId, @Path("timelineId") int timelineId, @Body ManageProductionDTO dto, Callback<Response> callback);

    @POST("/users/{userId}/timeline/{timelineId}/operation")
    void operateLearning(@Path("userId")int userId, @Path("timelineId") int timelineId, @Body ManageLearningDTO dto, Callback<Response> callback);

    @POST("/users/{userId}/timeline/{timelineId}/operation")
    void operateEssential(@Path("userId")int userId, @Path("timelineId") int timelineId, @Body ManageEssentialDTO dto, Callback<Response> callback);

    @POST("/users/{userId}/timeline/{timelineId}/operation")
    void operateTop(@Path("userId")int userId, @Path("timelineId") int timelineId, @Body ManageTopDTO dto, Callback<Response> callback);

    @POST("/users/{userId}/timeline/{timelineId}/operation")
    void operateDeleteTimeLine(@Path("userId")int userId, @Path("timelineId") int timelineId, @Body ManageDeleteTimeLineDTO dto, Callback<Response> callback);
}
