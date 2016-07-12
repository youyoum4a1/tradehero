package com.androidth.general.network.service.ayondo;


import com.androidth.general.api.kyc.BrokerApplicationDTO;
import com.androidth.general.api.kyc.KYCForm;
import com.androidth.general.api.kyc.StepStatusesDTO;
import com.androidth.general.api.kyc.ayondo.AyondoAccountCreationDTO;
import com.androidth.general.api.kyc.ayondo.AyondoAddressCheckDTO;
import com.androidth.general.api.kyc.ayondo.AyondoCurrentApplicationDTO;
import com.androidth.general.api.kyc.ayondo.AyondoIDCheckDTO;
import com.androidth.general.api.kyc.ayondo.AyondoLeadAddressDTO;
import com.androidth.general.api.kyc.ayondo.AyondoLeadDTO;
import com.androidth.general.api.kyc.ayondo.AyondoLeadUserIdentityDTO;
import com.androidth.general.api.kyc.ayondo.AyondoLiveAvailabilityDTO;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

public interface LiveServiceAyondoRx
{
    //Refer to AyondoKycServices.cs

    //Use this instead LiveServiceRx

    @GET("/kyc/ayondo/availability")
    Observable<AyondoLiveAvailabilityDTO> getAvailability();
    //If user available or not. UserId is sent in header. Returns boolean

    @GET("/kyc/ayondo/currentapplication")
    Observable<AyondoCurrentApplicationDTO> getCurrentApplication();
    //Returns JSON
    //applied

    @POST("/applyBroker/ayondo")
    Observable<StepStatusesDTO> applyLiveBroker(
            @Body KYCForm kycForm);

    @POST("/kyc/ayondo/createOrUpdateLead/{providerId}")
    Observable<BrokerApplicationDTO> createOrUpdateLead(
            @Path("providerId") int providerId,
            @Body AyondoLeadDTO ayondoLeadDTO);

    @POST("/kyc/ayondo/checkidentity")
    Observable<AyondoIDCheckDTO> checkNeedIdentity(
            @Body AyondoLeadUserIdentityDTO ayondoLeadUserIdentityDTO);

    @POST("/kyc/ayondo/checkaddress")
    Observable<AyondoAddressCheckDTO> checkNeedResidency(
            @Body AyondoLeadAddressDTO ayondoLeadAddressDTO);

    @POST("/kyc/ayondo/createAccount/{providerId}")
    Observable<BrokerApplicationDTO> submitApplication(@Body AyondoAccountCreationDTO ayondoAccountCreationDTO, @Path("providerId") int providerId);
}
