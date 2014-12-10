package com.tradehero.th.models.push.urbanairship;

import android.app.Application;
import android.support.annotation.NonNull;
import com.urbanairship.UAirship;
import rx.Observable;
import rx.Subscriber;

public class UrbanAirshipOnReadyOperator implements Observable.OnSubscribe<UAirship>
{
    @NonNull private final Application application;

    //<editor-fold desc="Constructors">
    public UrbanAirshipOnReadyOperator(@NonNull Application application)
    {
        this.application = application;
    }
    //</editor-fold>

    @Override public void call(Subscriber<? super UAirship> subscriber)
    {
        try
        {
            UAirship.takeOff(application, airship -> {
                subscriber.onNext(airship);
                subscriber.onCompleted();
            });
        } catch (Throwable e)
        {
            subscriber.onError(e);
        }
    }
}
