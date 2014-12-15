package com.tradehero.th.network.service;

import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

interface UserTimelineServiceAsync
{
    //<editor-fold desc="Get Global Timeline">
    @GET("/timeline")
    void getGlobalTimeline(
            @Query("maxCount") Integer maxCount,
            @Query("maxId") Integer maxId,
            @Query("minId") Integer minId,
            Callback<TimelineDTO> callback);

    @GET("/timeline/{timelineId}") void getTimelineDetail(
            @Path("timelineId") int timelineId,
            Callback<TimelineItemDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Get User Timeline">
    @GET("/users/{userId}/timeline")
    void getTimeline(
            @Path("userId") int userId,
            @Query("maxCount") Integer maxCount,
            @Query("maxId") Integer maxId,
            @Query("minId") Integer minId,
            Callback<TimelineDTO> callback);
    //</editor-fold>

    //<editor-fold desc="Get User Timeline">
    @GET("/users/{userId}/timeline?type=filtered&includeComment=false&includeTrade=true")
    void getTimelineNew(
            @Path("userId") int userId,
            @Query("maxCount") Integer maxCount,
            @Query("maxId") Integer maxId,
            @Query("minId") Integer minId,
            Callback<TimelineDTO> callback);
    //</editor-fold>


    //最新动态
    //<editor-fold desc="Get User Timeline"> /users/552948/timeline?maxCount=10&type=original&includeComment=true
    //@GET("/users/{userId}/timeline?type=original&includeComment=true&includeTrade=true")
    @GET("/users/{userId}/timeline?type=original")
    void getTimelineSquare(
            @Path("userId") int userId,
            @Query("maxCount") Integer maxCount,
            @Query("maxId") Integer maxId,
            @Query("minId") Integer minId,
            Callback<TimelineDTO> callback);
    //</editor-fold>


    //热门话题
    //@GET("/users/{userId}/timeline?type=original&includeComment=true&includeTrade=true")
    //https://www.tradehero.mobi/api/users/552948/timeline?maxCount=10&type=recommended&includeComment=true&includeTrade=true
    @GET("/users/{userId}/timeline?type=hot")
    void getTimelineHotTopic(
            @Path("userId") int userId,
            @Query("maxCount") Integer maxCount,
            @Query("maxId") Integer maxId,
            @Query("minId") Integer minId,
            Callback<TimelineDTO> callback);
    //</editor-fold>

    //悬赏帖子
    //http://localhost/api/users/552948/timeline?maxCount=10&type=question
    //@GET("/users/{userId}/timeline?type=original&includeComment=true&includeTrade=true")
    //https://www.tradehero.mobi/api/users/552948/timeline?maxCount=10&type=recommended&includeComment=true&includeTrade=true
    @GET("/users/{userId}/timeline?type=question")
    void getTimelineReward(
            @Path("userId") int userId,
            @Query("maxCount") Integer maxCount,
            @Query("maxId") Integer maxId,
            @Query("minId") Integer minId,
            Callback<TimelineDTO> callback);
    //</editor-fold>

    //股神动态
    //@GET("/users/{userId}/timeline?type=original&includeComment=true&includeTrade=true")
    //https://www.tradehero.mobi/api/users/552948/timeline?maxCount=10&type=recommended&includeComment=true&includeTrade=true
    @GET("/users/{userId}/timeline?type=recommended&includeComment=false&includeTrade=true")
    void getTimelineStockGodNews(
            @Path("userId") int userId,
            @Query("maxCount") Integer maxCount,
            @Query("maxId") Integer maxId,
            @Query("minId") Integer minId,
            Callback<TimelineDTO> callback);
    //</editor-fold>



    //<editor-fold desc="Share Timeline Item">
    @POST("/users/{userId}/timeline/{timelineItemId}/share")
    void shareTimelineItem(
            @Path("userId") int userId,
            @Path("timelineItemId") int timelineItemId,
            @Body TimelineItemShareRequestDTO timelineItemShareRequestDTO,
            Callback<Response> callback);
    //</editor-fold>

    //<editor-fold desc="Delete Timeline Item">
    @DELETE("/users/{userId}/timeline/{timelineItemId}")
    void deleteTimelineItem(
            @Path("userId") int userId,
            @Path("timelineItemId") int timelineItemId,
            Callback<Response> callback);
    //</editor-fold>
}
