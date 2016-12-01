package com.androidth.general.models.sms.twilio;

import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface TwilioServiceRx
{
    @FormUrlEncoded @POST("api/Messages.json")
    Observable<TwilioSMSSentConfirmationDTO> sendMessage(
            @Field("From") String fromNumberOrName,
            @Field("To") String toNumber,
            @Field("Body") String messageBody);

    @GET("api/Messages/{sid}.json")
    Observable<TwilioSMSSentConfirmationDTO> getMessageStatus(
            @Path("sid") String sid);
}
