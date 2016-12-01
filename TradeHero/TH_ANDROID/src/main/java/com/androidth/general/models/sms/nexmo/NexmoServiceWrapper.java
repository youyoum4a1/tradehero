package com.androidth.general.models.sms.nexmo;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.androidth.general.models.retrofit2.THRetrofitException;
import com.androidth.general.models.sms.SMSId;
import com.androidth.general.models.sms.SMSRequest;
import com.androidth.general.models.sms.SMSSentConfirmationDTO;
import com.androidth.general.models.sms.SMSServiceWrapper;
import com.androidth.general.models.sms.twilio.TwilioRetrofitException;

import javax.inject.Inject;

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
            @NonNull NexmoSMSRequest request)
    {
        if(request.getLanguageCode()!=null && request.getLanguageCode().toLowerCase().contains("zh")){
            return nexmoServiceRx.sendMessage(
                    NexmoConstants.API_KEY,
                    NexmoConstants.API_SECRET,
                    request.getFromNumberOrName(),
                    request.getToNumber(),
                    request.getMessageBody(),
                    "zh-cn")
                    .onErrorResumeNext(getThrowableReprocessor());

        }else{
            return nexmoServiceRx.sendMessage(
                    NexmoConstants.API_KEY,
                    NexmoConstants.API_SECRET,
                    request.getFromNumberOrName(),
                    request.getToNumber(),
                    request.getMessageBody())
                    .onErrorResumeNext(getThrowableReprocessor());
        }

    }

    @NonNull @Override public Observable<SMSSentConfirmationDTO> sendMessage(@NonNull SMSRequest request)
    {
        return sendMessage((NexmoSMSRequest) request)
                .cast(SMSSentConfirmationDTO.class)
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.v("", "!!! Nexmo error:"+throwable.getLocalizedMessage());
                    }
                });
    }

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
                return throwable instanceof THRetrofitException
                        ? Observable.<NexmoSMSSentConfirmationDTO>error(new TwilioRetrofitException((THRetrofitException) throwable))
                        : Observable.<NexmoSMSSentConfirmationDTO>error(throwable);
            }
        };
    }
}