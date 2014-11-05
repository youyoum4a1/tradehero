package com.tradehero.th.auth.facebook;

import com.facebook.Session;
import com.facebook.SessionState;
import android.support.annotation.NonNull;
import rx.Observable;
import rx.Subscriber;

public class SessionStateOnSubscribe implements Observable.OnSubscribe<SessionState>
{
    @NonNull private Session session;

    public SessionStateOnSubscribe(@NonNull Session session)
    {
        this.session = session;
    }

    @Override public void call(@NonNull final Subscriber<? super SessionState> subscriber)
    {
        session.addCallback(new Session.StatusCallback()
        {
            @Override public void call(Session session, SessionState state, Exception exception)
            {
                if (exception != null)
                {
                    subscriber.onError(exception);
                }
                else
                {
                    subscriber.onNext(state);
                }
            }
        });
    }
}
