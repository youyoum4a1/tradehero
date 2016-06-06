package com.androidth.general.models.sms.twilio;

import android.support.annotation.NonNull;

import com.androidth.general.models.sms.SMSId;
import com.androidth.general.models.sms.SMSRequest;
import com.androidth.general.models.sms.SMSSentConfirmationDTO;
import com.androidth.general.models.sms.SMSServiceWrapper;

import javax.inject.Inject;

import retrofit.RetrofitError;
import rx.Observable;
import rx.functions.Func1;

public class TwilioServiceWrapper implements SMSServiceWrapper
{
    @NonNull private final TwilioServiceRx twilioServiceRx;

    @Inject public TwilioServiceWrapper(@NonNull TwilioServiceRx twilioServiceRx)
    {
        this.twilioServiceRx = twilioServiceRx;
    }

    @NonNull public Observable<TwilioSMSSentConfirmationDTO> sendMessage(
            @NonNull TwilioSMSRequest request)
    {
        return twilioServiceRx.sendMessage(
                request.getFromNumberOrName(),
                request.getToNumber(),
                request.getMessageBody())
                .onErrorResumeNext(getThrowableReprocessor());
    }

    @NonNull @Override public Observable<SMSSentConfirmationDTO> sendMessage(@NonNull SMSRequest request)
    {
        return sendMessage((TwilioSMSRequest) request)
                .cast(SMSSentConfirmationDTO.class);
    }

    @NonNull public Observable<TwilioSMSSentConfirmationDTO> getMessageStatus(
            @NonNull TwilioSMSId sid)
    {
        return twilioServiceRx.getMessageStatus(sid.id)
                .onErrorResumeNext(getThrowableReprocessor());
    }

    @NonNull @Override public Observable<SMSSentConfirmationDTO> getMessageStatus(@NonNull SMSId id)
    {
        return getMessageStatus((TwilioSMSId) id).cast(SMSSentConfirmationDTO.class);
    }

    @NonNull private static Func1<Throwable, Observable<? extends TwilioSMSSentConfirmationDTO>> getThrowableReprocessor()
    {
        return new Func1<Throwable, Observable<? extends TwilioSMSSentConfirmationDTO>>()
        {
            @Override public Observable<? extends TwilioSMSSentConfirmationDTO> call(Throwable throwable)
            {
                return throwable instanceof RetrofitError
                        ? Observable.<TwilioSMSSentConfirmationDTO>error(new TwilioRetrofitException((RetrofitError) throwable))
                        : Observable.<TwilioSMSSentConfirmationDTO>error(throwable);
            }
        };
    }
}
