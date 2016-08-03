package com.androidth.general.network.service;


import com.androidth.general.api.competition.JumioVerifyBodyDTO;
import com.androidth.general.api.competition.referral.MyProviderReferralDTO;
import com.androidth.general.api.kyc.BrokerDocumentUploadResponseDTO;
import com.androidth.general.api.kyc.KYCForm;
import com.androidth.general.api.kyc.KYCFormOptionsDTO;
import com.androidth.general.api.kyc.StepStatusesDTO;
import com.androidth.general.api.kyc.ayondo.ProviderQuestionnaireDTO;
import com.androidth.general.api.kyc.ayondo.UsernameValidationResultDTO;
import com.androidth.general.api.live.LiveTradingSituationDTO;
import com.androidth.general.api.kyc.CountryDocumentTypes;

import java.util.ArrayList;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedOutput;
import rx.Observable;

public interface LiveServiceRx {
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

    @Multipart
    @POST("/documentsUpload")
    Observable<BrokerDocumentUploadResponseDTO> uploadDocument(
            @Part("image") TypedOutput image
    );

 //https://live.tradehero.mobi/api//kyc/proofs/ic/my
    //https://live.tradehero.mobi/api/kyc/proofs/documents/my
    @GET("/kyc/proofs/documents/{countrycode}")
    Observable<ArrayList<CountryDocumentTypes>> documentsForCountry(
            @Path("countrycode") String countrycode);

    @GET("/competition/enroll/{providerId}/{userId}")
    Observable<Boolean>enrollCompetition(
            @Path("providerId") int providerId,
            @Path("userId") int userId
    );

    @GET("/{validateURL}")
    Observable<Boolean>validateData(
            @Path("validateURL") String validateURL
    );

    @GET("/email/confirmation/{userId}/{email}/{providerId}")
    Observable<Response> verifyEmail(
            @Path("userId") Integer userId,
            @Path("email") String email,
            @Path("providerId") Integer providerId
    );

    @GET("/kyc/idProof/scanResult/{scanReference}")
    Observable<Boolean>scanJumioResult(
            @Path("scanReference") String scanReference
    );

    @POST("/kyc/ayondo/scanReference/{providerId}")
    Observable<Response> uploadScanReference(
            @Body JumioVerifyBodyDTO jumioVerifyBodyDTO,
            @Path("providerId") int providerId
    );

    @GET("/kyc/ayondo/getAdditionalQuestionnaires/{providerId}")
    Observable<ArrayList<ProviderQuestionnaireDTO>> getAdditionalQuestionnaires(
            @Path("providerId") int providerId
    );

    //<editor-fold desc="Get Competition Referral Status">
    @GET("/competition/myreferralcode/{providerId}")
    Observable<MyProviderReferralDTO> getMyProviderReferralStatus(
            @Path("providerId") int providerId
    );
    //</editor-fold>

    @GET("/competition/rewardreferrar/{referralCode}/{providerId}")
    Observable<String> redeemReferralCode(
            @Path("referralCode") String referralCode,
            @Path("providerId") int providerId
    );
}
