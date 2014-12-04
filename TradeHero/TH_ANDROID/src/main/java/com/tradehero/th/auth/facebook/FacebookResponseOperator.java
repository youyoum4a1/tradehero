package com.tradehero.th.auth.facebook;

import android.support.annotation.NonNull;
import com.facebook.FacebookRequestError;
import com.facebook.Response;
import rx.Observable;
import rx.Subscriber;

public class FacebookResponseOperator implements Observable.OnSubscribe<Response>
{
    @NonNull private final Response response;

    //<editor-fold desc="Constructors">
    public FacebookResponseOperator(@NonNull Response response)
    {
        this.response = response;
    }
    //</editor-fold>

    @Override public void call(Subscriber<? super Response> subscriber)
    {
        FacebookRequestError e = response.getError();
        if (e != null)
        {
            subscriber.onError(new FacebookRequestException(e));
        }
        else
        {
            subscriber.onNext(response);
            subscriber.onCompleted();
        }
    }
}
