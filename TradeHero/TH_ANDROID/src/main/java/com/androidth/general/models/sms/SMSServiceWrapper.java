package com.androidth.general.models.sms;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;

public interface SMSServiceWrapper
{
    @NonNull Observable<SMSSentConfirmationDTO> sendMessage(@NonNull SMSRequest request);

    @Nullable Observable<SMSSentConfirmationDTO> getMessageStatus(@NonNull SMSId id);

    @Nullable Observable<SMSSentConfirmationDTO> getMessageStatus(@NonNull String id);
}
