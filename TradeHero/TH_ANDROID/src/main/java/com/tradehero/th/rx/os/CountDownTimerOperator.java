package com.tradehero.th.rx.os;

import android.os.CountDownTimer;
import rx.Observable;
import rx.Subscriber;
import rx.android.observables.Assertions;

public class CountDownTimerOperator implements Observable.OnSubscribe<CountDownTick>
{
    private final long millisInFuture;
    private final long countDownInterval;

    //<editor-fold desc="Constructor">
    public CountDownTimerOperator(long millisInFuture, long countDownInterval)
    {
        this.millisInFuture = millisInFuture;
        this.countDownInterval = countDownInterval;
    }
    //</editor-fold>

    @Override public void call(final Subscriber<? super CountDownTick> subscriber)
    {
        Assertions.assertUiThread();
        new CountDownTimer(millisInFuture, countDownInterval)
        {
            @Override public void onTick(long millisUntilFinished)
            {
                subscriber.onNext(new CountDownTick(millisUntilFinished));
            }

            @Override public void onFinish()
            {
                subscriber.onCompleted();
            }
        }.start();
    }
}
