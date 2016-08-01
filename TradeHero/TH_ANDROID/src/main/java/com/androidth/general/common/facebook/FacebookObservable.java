package com.androidth.general.common.facebook;

//import android.app.Activity;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//
//import com.facebook.AccessToken;
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.share.Sharer;
//import com.facebook.share.internal.ShareFeedContent;
//import com.facebook.share.widget.ShareDialog;
//
//import java.util.List;
//import rx.Observable;
//import rx.Subscriber;
//import rx.android.schedulers.AndroidSchedulers;

public class FacebookObservable
{
//    //<editor-fold desc="Open Active Session">
//    @NonNull public static Observable<AccessToken> openActiveToken(
//            @NonNull final Activity activity,
//            final boolean allowLoginUI)
//    {
//        return Observable.create(
//                new Observable.OnSubscribe<AccessToken>()
//                {
//                    @Override public void call(Subscriber<? super AccessToken> subscriber)
//                    {
////                        Session session = Session.getActiveSession();
////                        if (session != null)
////                        {
////                            subscriber.onNext(session);
////                            subscriber.onCompleted();
////                        }
////                        else
////                        {
////                            final Session.StatusCallback callback = new SubscriberCallback(subscriber);
////                            Session.openActiveSession(activity, allowLoginUI, callback);
////                            subscriber.add(AndroidSubscriptions.unsubscribeInUiThread(createRemoveCallback(callback)));
////                        }
//                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//                        if(accessToken!=null){
//                            subscriber.onNext(accessToken);
//                            subscriber.onCompleted();
//                        }
//                    }
//                })
//                .subscribeOn(AndroidSchedulers.mainThread());
//    }
//
//    @NonNull public static Observable<AccessToken> openActiveToken(
//            @NonNull final Activity activity,
//            final boolean allowLoginUI,
//            @NonNull final List<String> readPermissions)
//    {
//        return Observable.create(
//                new Observable.OnSubscribe<AccessToken>()
//                {
//                    @Override public void call(Subscriber<? super AccessToken> subscriber)
//                    {
////                        final Session.StatusCallback callback = new SubscriberCallback(subscriber);
////                        Session.openActiveSession(activity, allowLoginUI, readPermissions, callback);
////                        subscriber.add(AndroidSubscriptions.unsubscribeInUiThread(createRemoveCallback(callback)));
//
//                        AccessToken.getCurrentAccessToken();
//                    }
//                })
//                .subscribeOn(AndroidSchedulers.mainThread());
//    }
//
////    @NonNull private static Action0 createRemoveCallback(@NonNull final Session.StatusCallback callback)
////    {
////        return new Action0()
////        {
////            @Override public void call()
////            {
////                Session activeSession = Session.getActiveSession();
////                if (activeSession != null)
////                {
////                    activeSession.removeCallback(callback);
////                }
////            }
////        };
////    }
//    //</editor-fold>
//
//    @NonNull public static Observable<Bundle> createFeedDialog(@NonNull final ShareDialog shareDialog, @NonNull final ShareFeedContent shareFeedContent)
//    {
//        return Observable.create(
//                new Observable.OnSubscribe<Bundle>()
//                {
//                    @Override public void call(Subscriber<? super Bundle> subscriber)
//                    {
////                        final WebDialog dialog = dialogBuilder.setOnCompleteListener(new SubscriberOnCompleteListener(subscriber))
////                                .build();
////                        subscriber.add(AndroidSubscriptions.unsubscribeInUiThread(new Action0()
////                        {
////                            @Override public void call()
////                            {
////                                dialog.setOnCompleteListener(null);
////                            }
////                        }));
////                        dialog.show();
//                        shareDialog.registerCallback(CallbackManager.Factory.create(),
//                                new FacebookCallback<Sharer.Result>() {
//                                    @Override
//                                    public void onSuccess(Sharer.Result result) {
//
//                                    }
//
//                                    @Override
//                                    public void onCancel() {
//
//                                    }
//
//                                    @Override
//                                    public void onError(FacebookException error) {
//
//                                    }
//                                });
//
//                        shareDialog.show(shareFeedContent);
//                    }
//                })
//                .subscribeOn(AndroidSchedulers.mainThread());
//    }
}
