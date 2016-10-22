package com.androidth.general.models.sms.nexmo;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

public interface NexmoServiceRx
{
    @FormUrlEncoded @POST("/sms/json")
    Observable<NexmoSMSSentConfirmationDTO> sendMessage(
            @Field("api_key") String apiKey,
            @Field("api_secret") String apiSecret,
            @Field("from") String fromNumberOrName,
            @Field("to") String toNumber,
            @Field("text") String messageBody);

    @FormUrlEncoded @POST("/sms/json")
    Observable<NexmoSMSSentConfirmationDTO> sendMessage(
            @Field("api_key") String apiKey,
            @Field("api_secret") String apiSecret,
            @Field("from") String fromNumberOrName,
            @Field("to") String toNumber,
            @Field("text") String messageBody,
            @Field("lg") String language);
}
