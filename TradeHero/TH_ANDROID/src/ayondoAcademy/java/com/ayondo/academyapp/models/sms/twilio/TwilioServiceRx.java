package com.ayondo.academyapp.models.sms.twilio;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

public interface TwilioServiceRx
{
    @FormUrlEncoded @POST("/Messages.json")
    Observable<TwilioSMSSentConfirmationDTO> sendMessage(
            @Field("From") String fromNumberOrName,
            @Field("To") String toNumber,
            @Field("Body") String messageBody);

    @GET("/Messages/{sid}.json")
    Observable<TwilioSMSSentConfirmationDTO> getMessageStatus(
            @Path("sid") String sid);
}
