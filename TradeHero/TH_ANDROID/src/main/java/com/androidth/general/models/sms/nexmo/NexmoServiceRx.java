package com.androidth.general.models.sms.nexmo;

import com.androidth.general.models.sms.twilio.TwilioSMSSentConfirmationDTO;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
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
}
