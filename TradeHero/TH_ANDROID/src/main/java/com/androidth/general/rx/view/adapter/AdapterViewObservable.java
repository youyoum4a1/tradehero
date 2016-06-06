package com.androidth.general.rx.view.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public class AdapterViewObservable
{
    @NonNull public static Observable<OnSelectedEvent> selects(@NonNull final AdapterView<?> adapterView)
    {
        return Observable.create(new Observable.OnSubscribe<OnSelectedEvent>()
        {
            @Override public void call(final Subscriber<? super OnSelectedEvent> subscriber)
            {
                adapterView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        subscriber.onNext(new OnItemSelectedEvent(parent, view, position, id));
                    }

                    @Override public void onNothingSelected(AdapterView<?> parent)
                    {
                        subscriber.onNext(new OnNothingSelectedEvent(parent));
                    }
                });
                subscriber.add(Subscriptions.create(new Action0()
                {
                    @Override public void call()
                    {
                        adapterView.setOnItemSelectedListener(null);
                    }
                }));
            }
        });
    }
}
