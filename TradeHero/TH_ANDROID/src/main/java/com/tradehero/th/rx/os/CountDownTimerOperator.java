package com.tradehero.th.rx.os;

import android.os.CountDownTimer;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class CountDownTimerOperator implements Observable.OnSubscribe<CountDownTick>
{
    private final long millisInFuture;
    private final long countDownInterval;
    private CountDownTimer countDownTimer;

    //<editor-fold desc="Constructor">
    public CountDownTimerOperator(long millisInFuture, long countDownInterval)
    {
        this.millisInFuture = millisInFuture;
        this.countDownInterval = countDownInterval;
    }
    //</editor-fold>

    @Override public void call(final Subscriber<? super CountDownTick> subscriber)
    {
        Schedulers.computation().createWorker().schedule(() -> {
            countDownTimer = new CountDownTimer(millisInFuture, countDownInterval)
            {
                @Override public void onTick(long millisUntilFinished)
                {
                    subscriber.onNext(new CountDownTick(millisUntilFinished));
                }

                @Override public void onFinish()
                {
                    countDownTimer = null;
                    subscriber.onCompleted();
                }
            };
            countDownTimer.start();
        });
    }
}
