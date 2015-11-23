package com.tradehero.livetrade.thirdPartyServices.drivewealth.services;

import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSessionResultDTO;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupResultDTO;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthUploadResultDTO;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public interface DriveWealthServiceAync {

    /**
     * Sign up a practice account.
     */
    @POST("/v1/signups/live")
    @FormUrlEncoded
    @Headers({
            "Content-Type: application/json; charset=UTF-8"
    })
    void signup(
            @Field("emailAddress1") String emailAddress,
            @Field("firstName") String firstName,
            @Field("lastName") String lastName,
            @Field("username") String username,
            @Field("password") String password,
            @Field("usCitizen") boolean usCitizen,
            @Field("utm_campaign") String utm_campaign,
            Callback<DriveWealthSignupResultDTO> cb
    );


    /**
     * Sign up a Live trading account.
     */
    @POST("/v1/signups/live")
    @FormUrlEncoded
    @Headers({
            "Content-Type: application/json; charset=UTF-8"
    })
    void signupLive(
            @Field("userID") String userID,
            @Field("tradingType") String tradingType,
            @Field("ownershipType") String ownershipType,
            @Field("languageID") String languageID,

            @Field("firstName") String firstName,
            @Field("lastName") String lastName,
            @Field("idNo") String idNo,
            @Field("addressLine1") String address,
            @Field("emailAddress1") String emailAddress,
            @Field("usCitizen") boolean usCitizen,
            @Field("countryID") String countryID,
            @Field("citizenship") String citizenship,

            @Field("employmentStatus") String employmentStatus,
            @Field("employerBusiness") String employerBusiness,
            @Field("employerCompany") String employerCompany,
            @Field("employerIsBroker") boolean employerIsBroker,
            @Field("director") boolean director,
            @Field("politicallyExposed") boolean politicallyExposed,

            @Field("investmentObjectives") String investmentObjectives,
            @Field("investmentExperience") String investmentExperience,
            @Field("annualIncome") String annualIncome,
            @Field("networthLiquid") String networthLiquid,
            @Field("networthTotal") String networthTotal,
            @Field("riskTolerance") String riskTolerance,
            @Field("timeHorizon") String timeHorizon,
            @Field("liquidityNeeds") String liquidityNeeds,


            @Field("disclosureAck") boolean disclosureAck,
            @Field("disclosureRule14b") boolean disclosureRule14b,
            @Field("ackCustomerAgreement") boolean ackCustomerAgreement,
            @Field("ackMarketData") boolean ackMarketData,
            @Field("ackSweep") boolean ackSweep,
            @Field("ackFindersFee") boolean ackFindersFee,
            @Field("ackSigned") boolean ackSigned,
            @Field("ackSignedBy") String ackSignedBy,
            @Field("ackSignedWhen") String ackSignedWhen,

            Callback<DriveWealthSignupResultDTO> cb
    );


    /**
     * Account login.
     */
    @POST("/v1/userSessions")
    @FormUrlEncoded
    @Headers({
            "Content-Type: application/json; charset=UTF-8"
    })
    void login(
            @Field("appTypeID") int appTypeID,
            @Field("appVersion") String appVersion,
            @Field("languageID") String languageID,
            @Field("osType") String osType,
            @Field("osVersion") String osVersion,
            @Field("scrRes") String scrRes,
            @Field("username") String username,
            @Field("password") String password,
            @Field("guest") boolean guest,
            Callback<DriveWealthSessionResultDTO> cb
    );


    /**
     * Upload file.
     *
     * Example:
     *    TypedString author = new TypedString("cURL");
     *    File photoFile = new File("/home/user/Desktop/article-photo.png");
     *    TypedFile photoTypedFile = new TypedFile("image/*", photoFile);
     *    retrofitAdapter.uploadFile(author, photoTypedFile)
     */
    @POST("/v1/documents")
    @Multipart
    void uploadFile(
            @Header("x-mysolomeo-session-key") String authorization,
            @Part("userID") TypedString userID,
            @Part("documentType") TypedString documentType,
            @Part("documentImage") TypedFile documentImage,

            Callback<DriveWealthUploadResultDTO> cb
    );
}
