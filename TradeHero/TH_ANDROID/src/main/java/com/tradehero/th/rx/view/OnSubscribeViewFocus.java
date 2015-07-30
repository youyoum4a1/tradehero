package com.tradehero.th.rx.view;

import android.view.View;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.AndroidSubscriptions;
import rx.android.internal.Assertions;
import rx.functions.Action0;

class OnSubscribeViewFocus implements Observable.OnSubscribe<OnFocusChangeEvent>
{
    private final boolean emitInitialValue;
    private final View input;

    public OnSubscribeViewFocus(final View input, final boolean emitInitialValue)
    {
        this.input = input;
        this.emitInitialValue = emitInitialValue;
    }

    @Override
    public void call(final Subscriber<? super OnFocusChangeEvent> observer)
    {
        Assertions.assertUiThread();
        final View.OnFocusChangeListener watcher = new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                observer.onNext(OnFocusChangeEvent.create(input, hasFocus));
            }
        };

        final Subscription subscription = AndroidSubscriptions.unsubscribeInUiThread(new Action0()
        {
            @Override
            public void call()
            {
                input.setOnFocusChangeListener(null);
            }
        });

        if (emitInitialValue)
        {
            observer.onNext(OnFocusChangeEvent.create(input, input.hasFocus()));
        }

        input.setOnFocusChangeListener(watcher);
        observer.add(subscription);
    }
}
