package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.live.LiveTradingSituationDTO;
import com.tradehero.th.models.kyc.KYCForm;
import com.tradehero.th.models.kyc.StepStatusesDTO;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

public interface LiveServiceRx
{
    @GET("/liveTradingSituation")
    Observable<LiveTradingSituationDTO> getLiveTradingSituation();

    @POST("/applyBroker/{brokerId}")
    Observable<StepStatusesDTO> applyLiveBroker(
            @Path("brokerId") int brokerId,
            @Body KYCForm kycForm);
}
