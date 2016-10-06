package com.androidth.general.models.sms.nexmo;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.androidth.general.models.sms.SMSId;
import com.androidth.general.models.sms.SMSRequest;
import com.androidth.general.models.sms.SMSSentConfirmationDTO;
import com.androidth.general.models.sms.SMSServiceWrapper;
import com.androidth.general.models.sms.twilio.TwilioConstants;
import com.androidth.general.models.sms.twilio.TwilioRetrofitException;
import com.androidth.general.models.sms.twilio.TwilioSMSId;
import com.androidth.general.models.sms.twilio.TwilioSMSRequest;
import com.androidth.general.models.sms.twilio.TwilioSMSSentConfirmationDTO;
import com.androidth.general.models.sms.twilio.TwilioServiceRx;

import javax.inject.Inject;

import retrofit.RetrofitError;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class NexmoServiceWrapper implements SMSServiceWrapper
{
    @NonNull private final NexmoServiceRx nexmoServiceRx;

    @Inject
    public NexmoServiceWrapper(@NonNull NexmoServiceRx nexmoServiceRx)
    {
        this.nexmoServiceRx = nexmoServiceRx;
    }

    @NonNull public Observable<NexmoSMSSentConfirmationDTO> sendMessage(
            @NonNull TwilioSMSRequest request)
    {
        return nexmoServiceRx.sendMessage(
                TwilioConstants.TWILIO_TH_ACCOUNT,
                TwilioConstants.TWILIO_TH_PASSWORD,
                request.getFromNumberOrName(),
                request.getToNumber(),
                request.getMessageBody())
                .onErrorResumeNext(getThrowableReprocessor());
    }

    @NonNull @Override public Observable<SMSSentConfirmationDTO> sendMessage(@NonNull SMSRequest request)
    {
        return sendMessage((TwilioSMSRequest) request)
                .cast(SMSSentConfirmationDTO.class)
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.v("", "!!! Nexmo error:"+throwable.getLocalizedMessage());
                    }
                });
    }

//    @NonNull public Observable<TwilioSMSSentConfirmationDTO> getMessageStatus(
//            @NonNull TwilioSMSId sid)
//    {
//        return nexmoServiceRx.getMessageStatus(sid.id)
//                .onErrorResumeNext(getThrowableReprocessor());
//    }

    @Nullable @Override public Observable<SMSSentConfirmationDTO> getMessageStatus(@NonNull SMSId id)
    {
        return null;
    }

    @Nullable @Override public Observable<SMSSentConfirmationDTO> getMessageStatus(@NonNull String id)
    {
        return getMessageStatus((String) id).cast(SMSSentConfirmationDTO.class);
    }

    @NonNull private static Func1<Throwable, Observable<? extends NexmoSMSSentConfirmationDTO>> getThrowableReprocessor()
    {
        return new Func1<Throwable, Observable<? extends NexmoSMSSentConfirmationDTO>>()
        {
            @Override public Observable<? extends NexmoSMSSentConfirmationDTO> call(Throwable throwable)
            {
                return throwable instanceof RetrofitError
                        ? Observable.<NexmoSMSSentConfirmationDTO>error(new TwilioRetrofitException((RetrofitError) throwable))
                        : Observable.<NexmoSMSSentConfirmationDTO>error(throwable);
            }
        };
    }
}