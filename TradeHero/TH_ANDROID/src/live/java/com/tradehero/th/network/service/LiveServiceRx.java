package com.tradehero.th.network.service;

import com.tradehero.th.api.kyc.KYCForm;
import com.tradehero.th.api.kyc.KYCFormOptionsDTO;
import com.tradehero.th.api.kyc.StepStatusesDTO;
import com.tradehero.th.api.kyc.ayondo.UsernameValidationResultDTO;
import com.tradehero.th.api.live.LiveTradingSituationDTO;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface LiveServiceRx
{
    @GET("/liveTradingSituation") Observable<LiveTradingSituationDTO> getLiveTradingSituation();

    //@POST("/applyBroker/{liveBrokerId}") //For generic API
    @POST("/applyBroker/ayondo") Observable<StepStatusesDTO> applyLiveBroker(
            //@Path("liveBrokerId")
            int brokerId,
            @Body KYCForm kycForm);

    @GET("/kycFormOptions/{liveBrokerId}") Observable<KYCFormOptionsDTO> getKYCFormOptions(
            //@Path("liveBrokerId")
            int brokerId);

    //@GET("/kyc/{liveBrokerId}/checkusername")
    @GET("/kyc/ayondo/checkusername")
    Observable<UsernameValidationResultDTO> validateUserName(
            @Query("username") String username);
}
