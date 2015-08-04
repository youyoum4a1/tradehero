package com.tradehero.th.network.service.ayondo;

import com.tradehero.th.api.kyc.KYCForm;
import com.tradehero.th.api.kyc.StepStatusesDTO;
import com.tradehero.th.api.kyc.ayondo.AyondoAccountCreationDTO;
import com.tradehero.th.api.kyc.ayondo.AyondoLiveAvailabilityDTO;
import com.tradehero.th.api.kyc.ayondo.UsernameValidationResultDTO;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface LiveServiceAyondoRx
{
    @GET("/kyc/ayondo/available")
    Observable<AyondoLiveAvailabilityDTO> getAvailability();

    @GET("/kyc/ayondo/currentapplication")
    Observable<AyondoAccountCreationDTO> getCurrentApplication();

    @POST("/applyBroker/ayondo")
    Observable<StepStatusesDTO> applyLiveBroker(
            @Body KYCForm kycForm);

    @GET("/kyc/ayondo/checkusername")
    Observable<UsernameValidationResultDTO> validateUserName(
            @Query("username") String username);
}
