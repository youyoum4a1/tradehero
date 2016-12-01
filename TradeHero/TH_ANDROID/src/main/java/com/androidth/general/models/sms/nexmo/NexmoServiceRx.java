package com.androidth.general.models.sms.nexmo;

import com.androidth.general.models.sms.twilio.TwilioSMSSentConfirmationDTO;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface NexmoServiceRx
{
    @FormUrlEncoded
    @POST("api/sms/json")
    Observable<NexmoSMSSentConfirmationDTO> sendMessage(
            @Field("api_key") String apiKey,
            @Field("api_secret") String apiSecret,
            @Field("from") String fromNumberOrName,
            @Field("to") String toNumber,
            @Field("text") String messageBody);

    @FormUrlEncoded @POST("api/sms/json")
    Observable<NexmoSMSSentConfirmationDTO> sendMessage(
            @Field("api_key") String apiKey,
            @Field("api_secret") String apiSecret,
            @Field("from") String fromNumberOrName,
            @Field("to") String toNumber,
            @Field("text") String messageBody,
            @Field("lg") String language);
}
