package com.ayondo.academy.network.service;

import com.ayondo.academy.api.kyc.BrokerDocumentUploadResponseDTO;
import com.ayondo.academy.api.kyc.KYCForm;
import com.ayondo.academy.api.kyc.KYCFormOptionsDTO;
import com.ayondo.academy.api.kyc.StepStatusesDTO;
import com.ayondo.academy.api.kyc.ayondo.UsernameValidationResultDTO;
import com.ayondo.academy.api.live.LiveTradingSituationDTO;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedOutput;
import rx.Observable;

public interface LiveServiceRx
{
    @GET("/liveTradingSituation")
    Observable<LiveTradingSituationDTO> getLiveTradingSituation();

    @POST("/applyBroker/{liveBrokerId}")
    Observable<StepStatusesDTO> applyLiveBroker(
            @Path("liveBrokerId") int brokerId,
            @Body KYCForm kycForm);

    @GET("/kycFormOptions/{liveBrokerId}")
    Observable<KYCFormOptionsDTO> getKYCFormOptions(
            @Path("liveBrokerId") int brokerId);

    @GET("/kyc/{liveBrokerId}/checkusername")
    Observable<UsernameValidationResultDTO> validateUserName(
            @Path("liveBrokerId") int brokerId,
            @Query("username") String username);

    @Multipart @POST("/documentsUpload")
    Observable<BrokerDocumentUploadResponseDTO> uploadDocument(
            @Part("image") TypedOutput image
    );
}
