package com.ayondo.academyapp.network.service.ayondo;

import com.ayondo.academyapp.api.kyc.BrokerApplicationDTO;
import com.ayondo.academyapp.api.kyc.KYCForm;
import com.ayondo.academyapp.api.kyc.StepStatusesDTO;
import com.ayondo.academyapp.api.kyc.ayondo.AyondoAccountCreationDTO;
import com.ayondo.academyapp.api.kyc.ayondo.AyondoAddressCheckDTO;
import com.ayondo.academyapp.api.kyc.ayondo.AyondoCurrentApplicationDTO;
import com.ayondo.academyapp.api.kyc.ayondo.AyondoIDCheckDTO;
import com.ayondo.academyapp.api.kyc.ayondo.AyondoLeadAddressDTO;
import com.ayondo.academyapp.api.kyc.ayondo.AyondoLeadDTO;
import com.ayondo.academyapp.api.kyc.ayondo.AyondoLeadUserIdentityDTO;
import com.ayondo.academyapp.api.kyc.ayondo.AyondoLiveAvailabilityDTO;
import com.ayondo.academyapp.api.kyc.ayondo.UsernameValidationResultDTO;
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
