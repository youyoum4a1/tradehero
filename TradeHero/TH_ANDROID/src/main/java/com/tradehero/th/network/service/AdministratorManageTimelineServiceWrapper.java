package com.tradehero.th.network.service;

import com.tradehero.chinabuild.data.*;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.client.Response;

import javax.inject.Inject;

/**
 * Created by palmer on 15/2/2.
 */
public class AdministratorManageTimelineServiceWrapper {

    @NotNull private final AdministratorManageTimelineServiceAsync administratorManageTimelineServiceAsync;

    @Inject public AdministratorManageTimelineServiceWrapper(@NotNull AdministratorManageTimelineServiceAsync administratorManageTimelineServiceAsync){
        super();
        this.administratorManageTimelineServiceAsync = administratorManageTimelineServiceAsync;
    }

    public void operationProduction(int userId, int timelineId, ManageProductionDTO dto, Callback<Response> callback){
        administratorManageTimelineServiceAsync.operateProduction(userId, timelineId, dto, callback);
    }

    public void operationEssential(int userId, int timelineId, ManageEssentialDTO dto, Callback<Response> callback){
        administratorManageTimelineServiceAsync.operateEssential(userId, timelineId, dto, callback);
    }

    public void operationTop(int userId, int timelineId, ManageTopDTO dto, Callback<Response> callback){
        administratorManageTimelineServiceAsync.operateTop(userId, timelineId, dto, callback);
    }

    public void operationLearning(int userId, int timelineId, ManageLearningDTO dto, Callback<Response> callback){
        administratorManageTimelineServiceAsync.operateLearning(userId, timelineId, dto, callback);
    }

    public void operationDeleteTimeLine(int userId, int timelineId, ManageDeleteTimeLineDTO dto, Callback<Response> callback){
        administratorManageTimelineServiceAsync.operateDeleteTimeLine(userId, timelineId, dto, callback);
    }
}
