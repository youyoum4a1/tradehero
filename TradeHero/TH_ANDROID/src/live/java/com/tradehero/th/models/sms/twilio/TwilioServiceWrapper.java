package com.tradehero.th.models.sms.twilio;

import android.support.annotation.NonNull;
import com.tradehero.th.models.sms.SMSId;
import com.tradehero.th.models.sms.SMSRequest;
import com.tradehero.th.models.sms.SMSSentConfirmationDTO;
import com.tradehero.th.models.sms.SMSServiceWrapper;
import javax.inject.Inject;
import rx.Observable;

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
                request.getMessageBody());
    }

    @NonNull @Override public Observable<SMSSentConfirmationDTO> sendMessage(@NonNull SMSRequest request)
    {
        return sendMessage((TwilioSMSRequest) request)
                .cast(SMSSentConfirmationDTO.class);
    }

    @NonNull public Observable<TwilioSMSSentConfirmationDTO> getMessageStatus(
            @NonNull TwilioSMSId sid)
    {
        return twilioServiceRx.getMessageStatus(sid.id);
    }

    @NonNull @Override public Observable<SMSSentConfirmationDTO> getMessageStatus(@NonNull SMSId id)
    {
        return getMessageStatus((TwilioSMSId) id).cast(SMSSentConfirmationDTO.class);
    }
}
