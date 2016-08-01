//package com.androidth.general.common.facebook;
//Jeff removed this class
//import android.support.annotation.NonNull;
//import com.facebook.FacebookOperationCanceledException;
//import com.facebook.Session;
//import com.facebook.SessionState;
//import rx.Subscriber;
//
//public class SubscriberCallback implements Session.StatusCallback
//{
//    @NonNull private Subscriber<? super Session> subscriber;
//
//    //<editor-fold desc="Constructors">
//    public SubscriberCallback(@NonNull Subscriber<? super Session> subscriber)
//    {
//        this.subscriber = subscriber;
//    }
//    //</editor-fold>
//
//    @Override public void call(Session session, SessionState state, Exception exception)
//    {
//        if (state == SessionState.OPENING)
//        {
//            // We have to wait a return on onActivityResult().
//            return;
//        }
//        if (state.isOpened())
//        {
//            subscriber.onNext(session);
//            subscriber.onCompleted();
//        }
//        else if (exception != null)
//        {
//            subscriber.onError(exception);
//        }
//        else
//        {
//            subscriber.onError(new FacebookOperationCanceledException("Action has been canceled"));
//        }
//    }
//}
