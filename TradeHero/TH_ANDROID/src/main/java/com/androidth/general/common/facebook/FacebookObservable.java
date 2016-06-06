package com.androidth.general.common.facebook;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.android.AndroidSubscriptions;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

public class FacebookObservable
{
    //<editor-fold desc="Open Active Session">
    @NonNull public static Observable<Session> openActiveSession(
            @NonNull final Activity activity,
            final boolean allowLoginUI)
    {
        return Observable.create(
                new Observable.OnSubscribe<Session>()
                {
                    @Override public void call(Subscriber<? super Session> subscriber)
                    {
                        Session session = Session.getActiveSession();
                        if (session != null)
                        {
                            subscriber.onNext(session);
                            subscriber.onCompleted();
                        }
                        else
                        {
                            final Session.StatusCallback callback = new SubscriberCallback(subscriber);
                            Session.openActiveSession(activity, allowLoginUI, callback);
                            subscriber.add(AndroidSubscriptions.unsubscribeInUiThread(createRemoveCallback(callback)));
                        }
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    @NonNull public static Observable<Session> openActiveSession(
            @NonNull final Activity activity,
            final boolean allowLoginUI,
            @NonNull final List<String> readPermissions)
    {
        return Observable.create(
                new Observable.OnSubscribe<Session>()
                {
                    @Override public void call(Subscriber<? super Session> subscriber)
                    {
                        final Session.StatusCallback callback = new SubscriberCallback(subscriber);
                        Session.openActiveSession(activity, allowLoginUI, readPermissions, callback);
                        subscriber.add(AndroidSubscriptions.unsubscribeInUiThread(createRemoveCallback(callback)));
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    @NonNull private static Action0 createRemoveCallback(@NonNull final Session.StatusCallback callback)
    {
        return new Action0()
        {
            @Override public void call()
            {
                Session activeSession = Session.getActiveSession();
                if (activeSession != null)
                {
                    activeSession.removeCallback(callback);
                }
            }
        };
    }
    //</editor-fold>

    @NonNull public static Observable<Bundle> createFeedDialog(@NonNull final WebDialog.FeedDialogBuilder dialogBuilder)
    {
        return Observable.create(
                new Observable.OnSubscribe<Bundle>()
                {
                    @Override public void call(Subscriber<? super Bundle> subscriber)
                    {
                        final WebDialog dialog = dialogBuilder.setOnCompleteListener(new SubscriberOnCompleteListener(subscriber))
                                .build();
                        subscriber.add(AndroidSubscriptions.unsubscribeInUiThread(new Action0()
                        {
                            @Override public void call()
                            {
                                dialog.setOnCompleteListener(null);
                            }
                        }));
                        dialog.show();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread());
    }
}
