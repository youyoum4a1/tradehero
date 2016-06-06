package com.androidth.general.common.rx;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.lifecycle.LifecycleEvent;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public class LifecycleObservableUtil
{
    @NonNull public static Observable<LifecycleEventWithActivity> getObservable(
            @NonNull final Application application)
    {
        return Observable.create(new Observable.OnSubscribe<LifecycleEventWithActivity>()
        {
            @Override public void call(final Subscriber<? super LifecycleEventWithActivity> subscriber)
            {
                final Application.ActivityLifecycleCallbacks callback = new Application.ActivityLifecycleCallbacks()
                {
                    @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState)
                    {
                        subscriber.onNext(new LifecycleEventWithActivity(activity, LifecycleEvent.CREATE));
                    }

                    @Override public void onActivityStarted(Activity activity)
                    {
                        subscriber.onNext(new LifecycleEventWithActivity(activity, LifecycleEvent.START));
                    }

                    @Override public void onActivityResumed(Activity activity)
                    {
                        subscriber.onNext(new LifecycleEventWithActivity(activity, LifecycleEvent.RESUME));
                    }

                    @Override public void onActivityPaused(Activity activity)
                    {
                        subscriber.onNext(new LifecycleEventWithActivity(activity, LifecycleEvent.PAUSE));
                    }

                    @Override public void onActivityStopped(Activity activity)
                    {
                        subscriber.onNext(new LifecycleEventWithActivity(activity, LifecycleEvent.STOP));
                    }

                    @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState)
                    {
                    }

                    @Override public void onActivityDestroyed(Activity activity)
                    {
                        subscriber.onNext(new LifecycleEventWithActivity(activity, LifecycleEvent.DESTROY));
                    }
                };
                Subscription cleanUp = Subscriptions.create(new Action0()
                {
                    @Override public void call()
                    {
                        application.unregisterActivityLifecycleCallbacks(callback);
                    }
                });
                subscriber.add(cleanUp);
                application.registerActivityLifecycleCallbacks(callback);
            }
        });
    }
}
