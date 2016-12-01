package com.androidth.general.network.service;


import com.androidth.general.api.competition.JumioVerifyBodyDTO;
import com.androidth.general.api.competition.referral.MyProviderReferralDTO;
import com.androidth.general.api.kyc.BrokerDocumentUploadResponseDTO;
import com.androidth.general.api.kyc.KYCForm;
import com.androidth.general.api.kyc.KYCFormOptionsDTO;
import com.androidth.general.api.kyc.StepStatusesDTO;
import com.androidth.general.api.kyc.ayondo.AyondoLeadDTO;
import com.androidth.general.api.kyc.ayondo.KYCAyondoForm;
import com.androidth.general.api.kyc.ayondo.ProviderQuestionnaireDTO;
import com.androidth.general.api.kyc.ayondo.UsernameValidationResultDTO;
import com.androidth.general.api.live.LiveTradingSituationDTO;
import com.androidth.general.api.kyc.CountryDocumentTypes;

import java.util.ArrayList;

import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface LiveServiceRx {
    @GET("api/liveTradingSituation")
    Observable<LiveTradingSituationDTO> getLiveTradingSituation();

    @POST("api/applyBroker/{liveBrokerId}")
    Observable<StepStatusesDTO> applyLiveBroker(
            @Path("liveBrokerId") int brokerId,
            @Body KYCForm kycForm);

    @GET("api/kycFormOptions/{liveBrokerId}")
    Observable<KYCFormOptionsDTO> getKYCFormOptions(
            @Path("liveBrokerId") int brokerId);

    @GET("api/kyc/{liveBrokerId}/checkusername")
    Observable<UsernameValidationResultDTO> validateUserName(
            @Path("liveBrokerId") int brokerId,
            @Query("username") String username);

    @Multipart
    @POST("api/documentsUpload")
    Observable<BrokerDocumentUploadResponseDTO> uploadDocument(
            @Part("image") RequestBody image
    );

 //https://live.tradehero.mobi/api//kyc/proofs/ic/my
    //https://live.tradehero.mobi/api/kyc/proofs/documents/my
    @GET("api/kyc/proofs/documents/{countrycode}")
    Observable<ArrayList<CountryDocumentTypes>> documentsForCountry(
            @Path("countrycode") String countrycode);

    @GET("api/competition/enroll/{providerId}/{userId}")
    Observable<Boolean>enrollCompetition(
            @Path("providerId") int providerId,
            @Path("userId") int userId
    );

    @GET("api/{validateURL}")
    Observable<Boolean>validateData(
            @Path("validateURL") String validateURL
    );

    @GET("api/email/confirmation/{userId}/{email}/{providerId}")
    Observable<Response> verifyEmail(
            @Path("userId") Integer userId,
            @Path("email") String email,
            @Path("providerId") Integer providerId
    );

    @GET("api/kyc/idProof/scanResult/{scanReference}")
    Observable<Boolean>scanJumioResult(
            @Path("scanReference") String scanReference
    );

    @POST("api/kyc/ayondo/scanReference/{providerId}")
    Observable<Response> uploadScanReference(
            @Body JumioVerifyBodyDTO jumioVerifyBodyDTO,
            @Path("providerId") int providerId
    );

    @GET("api/kyc/ayondo/getAdditionalQuestionnaires/{providerId}")
    Observable<ArrayList<ProviderQuestionnaireDTO>> getAdditionalQuestionnaires(
            @Path("providerId") int providerId
    );

    //<editor-fold desc="Get Competition Referral Status">
    @GET("api/competition/myreferralcode/{providerId}")
    Observable<MyProviderReferralDTO> getMyProviderReferralStatus(
            @Path("providerId") int providerId
    );
    //</editor-fold>

    @GET("api/competition/rewardreferrar/{referralCode}/{providerId}")
    Observable<String> redeemReferralCode(
            @Path("referralCode") String referralCode,
            @Path("providerId") int providerId
    );

    @GET("api/kyc/ayondo/getLead/{providerId}")
    Observable<AyondoLeadDTO> getLead(
            @Path("providerId") int providerId
    );
}
