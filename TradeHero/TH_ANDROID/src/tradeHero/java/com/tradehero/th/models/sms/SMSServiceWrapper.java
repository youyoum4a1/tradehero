package com.androidth.general.models.sms;

import android.support.annotation.NonNull;
import rx.Observable;

public interface SMSServiceWrapper
{
    @NonNull Observable<SMSSentConfirmationDTO> sendMessage(@NonNull SMSRequest request);

    @NonNull Observable<SMSSentConfirmationDTO> getMessageStatus(@NonNull SMSId id);
}
