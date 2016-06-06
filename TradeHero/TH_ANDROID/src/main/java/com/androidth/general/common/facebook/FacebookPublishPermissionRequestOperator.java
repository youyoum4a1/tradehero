package com.androidth.general.common.facebook;

import android.support.annotation.NonNull;
import android.util.Pair;
import com.facebook.Session;
import com.facebook.SessionState;
import rx.Observable;
import rx.Subscriber;

public class FacebookPublishPermissionRequestOperator implements Observable.OnSubscribe<Pair<Session, SessionState>>
{
    @NonNull private final Session session;
    @NonNull private final Session.NewPermissionsRequest request;

    //<editor-fold desc="Constructors">
    public FacebookPublishPermissionRequestOperator(
            @NonNull Session session,
            @NonNull Session.NewPermissionsRequest request)
    {
        this.session = session;
        this.request = request;
    }
    //</editor-fold>

    @Override public void call(final Subscriber<? super Pair<Session, SessionState>> subscriber)
    {
        request.setCallback(new Session.StatusCallback()
        {
            @Override public void call(Session session, SessionState sessionState, Exception e)
            {
                if (e != null)
                {
                    subscriber.onError(e);
                }
                else
                {
                    subscriber.onNext(Pair.create(session, sessionState));
                    subscriber.onCompleted();
                }
            }
        });
        session.requestNewPublishPermissions(request);
    }
}
