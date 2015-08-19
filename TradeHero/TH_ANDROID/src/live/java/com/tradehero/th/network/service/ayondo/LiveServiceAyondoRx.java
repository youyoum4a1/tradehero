package com.tradehero.th.network.service.ayondo;

import com.tradehero.th.api.kyc.BrokerApplicationDTO;
import com.tradehero.th.api.kyc.KYCForm;
import com.tradehero.th.api.kyc.StepStatusesDTO;
import com.tradehero.th.api.kyc.ayondo.AyondoAccountCreationDTO;
import com.tradehero.th.api.kyc.ayondo.AyondoAddressCheckDTO;
import com.tradehero.th.api.kyc.ayondo.AyondoIDCheckDTO;
import com.tradehero.th.api.kyc.ayondo.AyondoLeadAddressDTO;
import com.tradehero.th.api.kyc.ayondo.AyondoLeadDTO;
import com.tradehero.th.api.kyc.ayondo.AyondoLeadUserIdentityDTO;
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

    @POST("/kyc/ayondo/createOrUpdateLead")
    Observable<BrokerApplicationDTO> createOrUpdateLead(@Body AyondoLeadDTO ayondoLeadDTO);

    @POST("/kyc/ayondo/checkidentity")
    Observable<AyondoIDCheckDTO> checkNeedIdentity(
            @Body AyondoLeadUserIdentityDTO ayondoLeadUserIdentityDTO);

    @POST("/kyc/ayondo/checkaddress")
    Observable<AyondoAddressCheckDTO> checkNeedResidency(
            @Body AyondoLeadAddressDTO ayondoLeadAddressDTO);

    @POST("/kyc/ayondo/createAccount")
    Observable<BrokerApplicationDTO> submitApplication(@Body AyondoAccountCreationDTO ayondoAccountCreationDTO);
}
