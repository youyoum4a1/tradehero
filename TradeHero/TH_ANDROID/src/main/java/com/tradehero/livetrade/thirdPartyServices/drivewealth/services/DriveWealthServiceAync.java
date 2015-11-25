package com.tradehero.livetrade.thirdPartyServices.drivewealth.services;

import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthLoginBody;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSessionResultDTO;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupBody;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupLiveBody;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupResultDTO;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthUploadResultDTO;

import retrofit.Callback;
import retrofit.http.Body;
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
    @Headers({
            "Content-Type: application/json; charset=UTF-8"
    })
    void signup(
            @Body DriveWealthSignupBody body,
            Callback<DriveWealthSignupResultDTO> cb
    );


    /**
     * Sign up a Live trading account.
     */
    @POST("/v1/signups/live")
    @Headers({
            "Content-Type: application/json; charset=UTF-8"
    })
    void signupLive(
            @Body DriveWealthSignupLiveBody body,
            Callback<DriveWealthSignupResultDTO> cb
    );


    /**
     * Account login.
     */
    @POST("/v1/userSessions")
    @Headers({
            "Content-Type: application/json; charset=UTF-8"
    })
    void login(
            @Body DriveWealthLoginBody body,
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
