//package com.androidth.general.common.facebook;
//Jeff removed this class
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.util.Pair;
//import com.facebook.Session;
//import com.facebook.SessionState;
//import com.facebook.UiLifecycleHelper;
//import com.facebook.widget.FacebookDialog;
//import rx.Observable;
//import rx.Subscriber;
//
//public class UiLifecycleOperator implements Observable.OnSubscribe<Pair<Session, SessionState>>
//{
////    @NonNull private final Activity activity;
////    @Nullable private UiLifecycleHelper lifecycleHelper;
////
////    //<editor-fold desc="Constructors">
////    public UiLifecycleOperator(@NonNull Activity activity)
////    {
////        this.activity = activity;
////    }
////    //</editor-fold>
////
//    @Override public void call(final Subscriber<? super Pair<Session, SessionState>> subscriber)
//    {
////        lifecycleHelper = new UiLifecycleHelper(
////                activity,
////                new Session.StatusCallback()
////                {
////                    @Override public void call(Session session, SessionState state, Exception exception)
////                    {
////                        if (exception != null)
////                        {
////                            subscriber.onError(exception);
////                        }
////                        else
////                        {
////                            subscriber.onNext(Pair.create(session, state));
////                            if (state.isClosed())
////                            {
////                                subscriber.onCompleted();
////                            }
////                        }
////                    }
////                });
//    }
////
////    public void onCreate(Bundle savedInstanceState)
////    {
////        if (lifecycleHelper != null)
////        {
////            lifecycleHelper.onCreate(savedInstanceState);
////        }
////    }
////
////    public void onResume()
////    {
////        if (lifecycleHelper != null)
////        {
////            lifecycleHelper.onResume();
////        }
////    }
////
////    public void onActivityResult(int requestCode, int resultCode, Intent data)
////    {
////        if (lifecycleHelper != null)
////        {
////            lifecycleHelper.onActivityResult(requestCode, resultCode, data);
////        }
////    }
////
////    public void onActivityResult(int requestCode, int resultCode, Intent data,
////            FacebookDialog.Callback facebookDialogCallback)
////    {
////        if (lifecycleHelper != null)
////        {
////            lifecycleHelper.onActivityResult(requestCode, resultCode, data, facebookDialogCallback);
////        }
////    }
////
////    public void onSaveInstanceState(Bundle outState)
////    {
////        if (lifecycleHelper != null)
////        {
////            lifecycleHelper.onSaveInstanceState(outState);
////        }
////    }
////
////    public void onPause()
////    {
////        if (lifecycleHelper != null)
////        {
////            lifecycleHelper.onPause();
////        }
////    }
////
////    public void onStop()
////    {
////        if (lifecycleHelper != null)
////        {
////            lifecycleHelper.onStop();
////        }
////    }
////
////    public void onDestroy()
////    {
////        if (lifecycleHelper != null)
////        {
////            lifecycleHelper.onDestroy();
////        }
////    }
//}
