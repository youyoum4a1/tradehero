package com.ayondo.academy.models.fastfill.jumio;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.ayondo.academy.api.users.CurrentUserId;
import javax.inject.Inject;
import rx.Observable;

public class DebugNetverifyFastFillUtil extends NetverifyFastFillUtil
{
    @Inject public DebugNetverifyFastFillUtil(
            @NonNull CurrentUserId currentUserId,
            @NonNull NetverifyServiceWrapper netverifyServiceWrapper)
    {
        super(currentUserId, netverifyServiceWrapper);
    }

    @NonNull @Override public Observable<Boolean> isAvailable(@NonNull final Activity activity)
    {
        return Observable.just(true);
    }
}
