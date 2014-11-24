package com.tradehero.th.auth.facebook;

import android.support.annotation.Nullable;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import rx.Subscriber;

public class SubscriberCallback implements Session.StatusCallback
{
    @Nullable private Subscriber<? super Session> subscriber;

    //<editor-fold desc="Constructors">
    public SubscriberCallback(@Nullable Subscriber<? super Session> subscriber)
    {
        this.subscriber = subscriber;
    }
    //</editor-fold>

    public void setSubscriber(@Nullable Subscriber<? super Session> subscriber)
    {
        this.subscriber = subscriber;
    }

    @Override public void call(Session session, SessionState state, Exception exception)
    {
        Subscriber<? super Session> subscriberCopy= subscriber;
        if (state == SessionState.OPENING || subscriberCopy == null)
        {
            return;
        }
        if (state.isOpened())
        {
            subscriberCopy.onNext(session);
            subscriberCopy.onCompleted();
        }
        else if (exception != null)
        {
            subscriberCopy.onError(exception);
        }
        else
        {
            subscriberCopy.onError(new FacebookOperationCanceledException("Action has been canceled"));
        }
    }
}
