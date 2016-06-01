package com.ayondo.academy.network.service.ayondo;

import com.ayondo.academy.api.kyc.BrokerApplicationDTO;
import com.ayondo.academy.api.kyc.KYCForm;
import com.ayondo.academy.api.kyc.StepStatusesDTO;
import com.ayondo.academy.api.kyc.ayondo.AyondoAccountCreationDTO;
import com.ayondo.academy.api.kyc.ayondo.AyondoAddressCheckDTO;
import com.ayondo.academy.api.kyc.ayondo.AyondoCurrentApplicationDTO;
import com.ayondo.academy.api.kyc.ayondo.AyondoIDCheckDTO;
import com.ayondo.academy.api.kyc.ayondo.AyondoLeadAddressDTO;
import com.ayondo.academy.api.kyc.ayondo.AyondoLeadDTO;
import com.ayondo.academy.api.kyc.ayondo.AyondoLeadUserIdentityDTO;
import com.ayondo.academy.api.kyc.ayondo.AyondoLiveAvailabilityDTO;
import com.ayondo.academy.api.kyc.ayondo.UsernameValidationResultDTO;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface LiveServiceAyondoRx
{
    @GET("/kyc/ayondo/availability")
    Observable<AyondoLiveAvailabilityDTO> getAvailability();

    @GET("/kyc/ayondo/currentapplication")
    Observable<AyondoCurrentApplicationDTO> getCurrentApplication();

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
