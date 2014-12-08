package com.tradehero.th.network;

import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.THApp;
import com.tradehero.th.misc.exception.THException;
import java.util.concurrent.TimeUnit;
import rx.Notification;
import rx.Observable;
import rx.functions.Func1;

public class DelayRetriesOrFailFunc1 implements Func1<Observable<? extends Notification<?>>, Observable<?>>
{
    private int countDown;
    private long delayMillis;

    //<editor-fold desc="Constructors">
    public DelayRetriesOrFailFunc1(int retries, long delayMillis)
    {
        this.countDown = retries;
        this.delayMillis = delayMillis;
    }
    //</editor-fold>

    @Override public Observable<?> call(Observable<? extends Notification<?>> attempts)
    {
        return attempts.flatMap(attempt ->
        {
            Throwable error = ((Notification) attempt).getThrowable();
            if (countDown <= 0)
            {
                return Observable.error(error);
            }
            THToast.show(String.format(
                    THApp.getResourceString(R.string.service_retry),
                    countDown,
                    new THException(error).getMessage()));
            countDown--;
            return Observable.just(attempt)
                    .delay(delayMillis, TimeUnit.MILLISECONDS);
        });
    }
}
