package com.tradehero.common.billing.samsung.rx;

import android.content.Context;
import android.support.annotation.NonNull;
import com.sec.android.iap.lib.helper.SamsungIapHelper;
import com.sec.android.iap.lib.listener.OnIapBindListener;
import com.tradehero.common.billing.samsung.BaseSamsungOperator;
import com.tradehero.common.billing.samsung.SamsungBillingMode;
import com.tradehero.common.billing.samsung.exception.SamsungBindException;
import rx.Observable;
import rx.Subscriber;

public class SamsungIapBindOperator extends BaseSamsungOperator
    implements Observable.OnSubscribe<Integer>
{
    //<editor-fold desc="Constructors">
    public SamsungIapBindOperator(@NonNull Context context, @SamsungBillingMode int mode)
    {
        super(context, mode);
    }
    //</editor-fold>

    @Override public void call(final Subscriber<? super Integer> subscriber)
    {
        getSamsungIapHelper().bindIapService(new OnIapBindListener()
        {
            @Override public void onBindIapFinished(int result)
            {
                if (result == SamsungIapHelper.IAP_RESPONSE_RESULT_OK)
                {
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                }
                else
                {
                    subscriber.onError(new SamsungBindException(result));
                }
            }
        });
    }
}
